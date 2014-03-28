package com.eventor

import com.eventor.api.Timeout
import com.eventor.api.annotations.*
import com.eventor.impl.SimpleInstanceCreator
import org.junit.runner.RunWith
import org.spockframework.runtime.Sputnik
import spock.lang.Specification

import java.util.concurrent.TimeUnit

@RunWith(Sputnik)
class SagaSpec extends Specification {

    def "Saga doesnt produce cmd without delay"() {
        when:
        eb.publish(new Init(), null)
        Thread.sleep(500)
        then:
        instanceCreator.findOrCreateInstanceOf(El, true).aCreated == 0
    }

    def "Saga produce cmd on delay"() {
        when:
        eb.publish(new Init(), null)
        Thread.sleep(1500)
        then:
        instanceCreator.findOrCreateInstanceOf(El, true).aCreated == 1
    }

    def instanceCreator = new SimpleInstanceCreator()
    def eventor = new EventorBuilder()
            .addClasses(AppleAggregate, SagaWithTimeout, El)
            .withInstanceCreator(instanceCreator)
            .build()
    def eb = eventor.getEventBus()
    def cb = eventor
}

class Init {}

class CreateA {}

class ACreated {}

@Aggregate
class A {
    @Id
    String id = "single"

    @Start
    @CommandHandler
    def on(CreateApple cmd) { return new AppleCreated() }
}

class TimeoutEvent implements Serializable {}

@Saga
class SagaWithTimeout {
    @Id
    String id = "single"

    @Start
    @com.eventor.api.annotations.EventListener
    public Object on(Init e) {
        return new Timeout(1, TimeUnit.SECONDS, new TimeoutEvent());
    }

    @OnTimeout(TimeoutEvent)
    public Object timeout() {
        return new CreateApple()
    }
}

@EventListener
class El {
    def aCreated = 0;

    @EventListener
    public void on(AppleCreated e) {
        aCreated++
    }
}

