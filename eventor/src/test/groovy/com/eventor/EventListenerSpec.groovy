package com.eventor

import com.eventor.api.annotations.*
import com.eventor.impl.InMemEventStorage
import com.eventor.impl.SimpleInstanceCreator
import org.junit.runner.RunWith
import org.spockframework.runtime.Sputnik
import spock.lang.Specification

@RunWith(Sputnik)
class EventListenerSpec extends Specification {

    def "Event listener doesnt consumes non fired events"() {
        when:
        Thread.sleep(500)
        then:
        instanceCreator.findOrCreateInstanceOf(AppleListener, true).counters == ['simpleEventCaptured': 0,
                'simpleEventWithSideEffectCaptured': 0,
                'aggregateEventCaptured': 0,
                'aggregateEventWithSideEffectCaptured': 0]
    }

    def "Event listener consumes aggregate events"() {
        when:
        cb.submit(new CreateApple())
        Thread.sleep(500)
        then:
        instanceCreator.findOrCreateInstanceOf(AppleListener, true).counters == ['simpleEventCaptured': 0,
                'simpleEventWithSideEffectCaptured': 0,
                'aggregateEventCaptured': 1,
                'aggregateEventWithSideEffectCaptured': 1]
    }

    def "Event listener consumes simple events"() {
        when:
        eb.publish(new AppleEvt(), null)
        Thread.sleep(500)
        then:
        instanceCreator.findOrCreateInstanceOf(AppleListener, true).counters == ['simpleEventCaptured': 1,
                'simpleEventWithSideEffectCaptured': 1,
                'aggregateEventCaptured': 0,
                'aggregateEventWithSideEffectCaptured': 0]
    }

    def "Event listener reply simple events without side effect"() {
        when:
        eb.publish(new AppleEvt(), null)
        Thread.sleep(500)
        eventor.replyEventsForAllViews()
        Thread.sleep(500)
        then:
        instanceCreator.findOrCreateInstanceOf(AppleListener, true).counters == ['simpleEventCaptured': 2,
                'simpleEventWithSideEffectCaptured': 1,
                'aggregateEventCaptured': 0,
                'aggregateEventWithSideEffectCaptured': 0]
    }

    def "Event listener reply domain events without side effect"() {
        when:
        cb.submit(new CreateApple())
        Thread.sleep(500)
        eventor.replyEventsForAllViews()
        Thread.sleep(500)
        then:
        instanceCreator.findOrCreateInstanceOf(AppleListener, true).counters == ['simpleEventCaptured': 0,
                'simpleEventWithSideEffectCaptured': 0,
                'aggregateEventCaptured': 2,
                'aggregateEventWithSideEffectCaptured': 1]
    }

    def "Event listener reply only events without side effect"() {
        when:
        eb.publish(new AppleEvt(), null)
        cb.submit(new CreateApple())
        Thread.sleep(500)
        eventor.replyEventsForAllViews()
        Thread.sleep(500)
        then:
        instanceCreator.findOrCreateInstanceOf(AppleListener, true).counters == ['simpleEventCaptured': 2,
                'simpleEventWithSideEffectCaptured': 1,
                'aggregateEventCaptured': 2,
                'aggregateEventWithSideEffectCaptured': 1]
    }


    def instanceCreator = new SimpleInstanceCreator()
    def eventStorage = new InMemEventStorage()
    def eventor = new EventorBuilder()
            .addClasses(AppleAggregate, AppleListener)
            .withInstanceCreator(instanceCreator)
            .eventStorage(eventStorage)
            .build()
    def eb = eventor.getEventBus()
    def cb = eventor
}

class CreateApple {}

class AppleCreated {}

class AppleEvt {}

@Aggregate
class AppleAggregate {
    @Id
    String id = UUID.randomUUID().toString();

    @Start
    @CommandHandler
    def on(CreateApple cmd) { return new AppleCreated() }
}

@EventListener
class AppleListener {
    def counters = [
            'simpleEventCaptured': 0,
            'aggregateEventCaptured': 0,
            'simpleEventWithSideEffectCaptured': 0,
            'aggregateEventWithSideEffectCaptured': 0]

    @EventListener
    public void on0(AppleEvt e) {
        counters['simpleEventCaptured'] = counters['simpleEventCaptured'] + 1
    }

    @EventListener
    public void on1(AppleCreated e) {
        counters['aggregateEventCaptured'] = counters['aggregateEventCaptured'] + 1
    }

    @EventListener
    @SideEffect
    public void on2(AppleEvt e) {
        counters['simpleEventWithSideEffectCaptured'] = counters['simpleEventWithSideEffectCaptured'] + 1
    }

    @EventListener
    @SideEffect
    public void on3(AppleCreated e) {
        counters['aggregateEventWithSideEffectCaptured'] = counters['aggregateEventWithSideEffectCaptured'] + 1
    }
}
