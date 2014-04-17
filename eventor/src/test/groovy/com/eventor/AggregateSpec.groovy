package com.eventor

import com.eventor.api.annotations.*
import com.eventor.impl.InMemEventStorage
import com.eventor.impl.SimpleInstanceCreator
import org.junit.runner.RunWith
import org.spockframework.runtime.Sputnik
import spock.lang.Specification

@RunWith(Sputnik)
class AggregateSpec extends Specification {
    def "Aggregate can be created by cmd"() {
        when:
        cb.submit(new CreateGreen(id: "my", data: "big"))
        Thread.sleep(500)
        then:
        aggregateRepo.getById(GreenAgg, "my").data == "big"
    }

    def "Aggregate can be created by evt"() {
        when:
        eb.publish(new GreenCreated(id: "my", data: "big"), null)
        Thread.sleep(500)
        then:
        aggregateRepo.getById(GreenAgg, "my").data == "big"
    }

    def "Aggregate can handle commands"() {
        when:
        eb.publish(new GreenCreated(id: "my", data: "big"), null)
        Thread.sleep(500)
        cb.submit(new ChangeData(id: "my", data: "very big"))
        Thread.sleep(500)
        then:
        aggregateRepo.getById(GreenAgg, "my").data == "big - very big"
    }

    def "Aggregate maintain state"() {
        when:
        eb.publish(new GreenCreated(id: "my", data: "big"), null)
        Thread.sleep(500)
        cb.submit(new ChangeData(id: "my", data: "very big"))
        cb.submit(new ChangeData(id: "my", data: "bigger"))
        Thread.sleep(500)
        then:
        aggregateRepo.getById(GreenAgg, "my").data == "big - very big - bigger"
    }

    def "Aggregate have independent state"() {
        when:
        eb.publish(new GreenCreated(id: "my", data: "big"), null)
        eb.publish(new GreenCreated(id: "their", data: "big"), null)
        Thread.sleep(500)
        cb.submit(new ChangeData(id: "my", data: "bigger"))
        Thread.sleep(500)
        then:
        aggregateRepo.getById(GreenAgg, "their").data == "big"
    }

    def instanceCreator = new SimpleInstanceCreator()
    def eventStorage = new InMemEventStorage()
    //def aggregateRepo = new InMemoryAggregateRepository()
    def eventor = new EventorBuilder()
            .addClasses(GreenAgg)
            .withInstanceCreator(instanceCreator)
            .eventStorage(eventStorage)
    //.withAggregateRepository(aggregateRepo)
            .eventSourcedAggregateRepository()
            .build()
    def eb = eventor.getEventBus()
    def cb = eventor
    def aggregateRepo = eventor.getRepository()

    def setup() {
    }
}

@Aggregate
class GreenAgg {
    @Id
    def id
    def data

    @CommandHandler
    @Start
    def handleCreate(CreateGreen c) {
        new GreenCreated(id: c.id, data: c.data)
    }

    @com.eventor.api.annotations.EventListener
    @Start
    def onCreated(GreenCreated e) {
        id = e.id
        data = e.data
        null
    }

    @CommandHandler
    def handleChange(@IdIn("id") ChangeData c) {
        new DataChanged(id: id, data: c.data)
    }

    @com.eventor.api.annotations.EventListener
    def onChange(DataChanged e) {
        data = "${data} - ${e.data}"
        null
    }

}

class CreateGreen {
    def id
    def data
}

class GreenCreated {
    def id
    def data
}

class ChangeData {
    def id
    def data
}

class DataChanged {
    def id
    def data
}

