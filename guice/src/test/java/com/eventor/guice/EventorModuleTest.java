package com.eventor.guice;

import com.eventor.api.EventBus;
import com.eventor.api.annotations.*;
import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import org.junit.Test;

import javax.inject.Singleton;
import java.util.Arrays;

import static org.junit.Assert.assertEquals;

public class EventorModuleTest {
    @Test
    public void testName() throws Exception {
        Injector injector = Guice.createInjector(
                new EventorModule(Arrays.<Class<?>>asList(new Class<?>[]{MyAggregate.class, El.class})));
        Ch ch = injector.getInstance(Ch.class);
        ch.handle(new Cmd());
        ch.handle(new Cmd());
        El el = injector.getInstance(El.class);
        Thread.sleep(500);
        assertEquals(2, el.createdCount);
        //TODO: test aggregate identity
    }

    public static class Cmd {

    }

    public static class Event {
        int id;

        public Event(int id) {
            this.id = id;
        }
    }

    public static class Ch {
        @Inject
        EventBus eventBus;
        int count;

        @CommandHandler
        public void handle(Cmd c) {
            eventBus.publish(new Event(count++));
        }
    }

    @Aggregate
    public static class MyAggregate {
        @Id
        int id;

        @EventListener
        @Start
        public void on(Event e) {
            this.id = e.id;
        }
    }

    @EventListener
    @Singleton
    public static class El {
        int createdCount;

        @EventListener
        public void handle(Event e) {
            createdCount++;
        }
    }
}
