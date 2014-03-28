package com.eventor

import com.eventor.api.annotations.*
import com.eventor.impl.SimpleInstanceCreator
import org.junit.runner.RunWith
import org.spockframework.runtime.Sputnik
import spock.lang.Specification

@RunWith(Sputnik)
class AggregateRepositorySpec extends Specification {
    def "Aggregates should maintain state"() {
        when:
        cb.submit(new CreateOrange(orangeId: 1))
        Thread.sleep(100)
        cb.submit(new DoOrangeStuff(orangeId: 1))
        cb.submit(new DoOrangeStuff(orangeId: 1))
        Thread.sleep(500)
        then:
        instanceCreator.findInstanceOf(OrangeEl).top == ['1': 2]
    }

    def "Aggregates should maintain independent state"() {
        when:
        cb.submit(new CreateOrange(orangeId: 1))
        cb.submit(new CreateOrange(orangeId: 2))
        Thread.sleep(100)
        cb.submit(new DoOrangeStuff(orangeId: 1))
        cb.submit(new DoOrangeStuff(orangeId: 2))
        cb.submit(new DoOrangeStuff(orangeId: 2))
        Thread.sleep(500)
        then:
        instanceCreator.findInstanceOf(OrangeEl).top == ['1': 1, '2': 2]
    }

    def instanceCreator = new SimpleInstanceCreator()
    def eventor = new EventorBuilder()
            .addClasses(OrangeAgg, OrangeEl)
            .withInstanceCreator(instanceCreator)
            .eventSourcedAggregateRepository()
            .build()

    def eb = eventor.getEventBus()
    def cb = eventor
}

class CreateOrange {
    String orangeId
}

class OrangeCreated {
    String orangeId
}

class DoOrangeStuff {
    String orangeId
}

class OrangeStuffDone {
    String orangeId
    int stuffDone
}

@Aggregate
class OrangeAgg {
    @Id
    String id;
    int stuffDone

    @Start
    @CommandHandler
    def handle(CreateOrange c) {
        return new OrangeCreated(orangeId: c.orangeId)
    }

    @com.eventor.api.annotations.EventListener
    def void on(OrangeCreated e) {
        id = e.orangeId
    }

    @CommandHandler
    def handle2(@IdIn("orangeId") DoOrangeStuff c) {
        return new OrangeStuffDone(orangeId: id, stuffDone: stuffDone + 1)
    }

    @com.eventor.api.annotations.EventListener
    def void on2(OrangeStuffDone c) {
        stuffDone++
    }
}

@com.eventor.api.annotations.EventListener
class OrangeEl {
    def top = [:]

    @com.eventor.api.annotations.EventListener
    def on1(OrangeCreated e) {
        top[e.orangeId] = 0
    }

    @com.eventor.api.annotations.EventListener
    def on(OrangeStuffDone e) {
        top[e.orangeId] = e.stuffDone
    }

}

