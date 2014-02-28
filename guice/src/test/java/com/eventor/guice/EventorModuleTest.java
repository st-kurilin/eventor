package com.eventor.guice;

import com.eventor.api.CommandBus;
import com.eventor.api.EventBus;
import com.eventor.api.annotations.*;
import com.google.common.collect.ImmutableMap;
import com.google.inject.*;
import com.google.inject.Key;
import com.google.inject.name.Names;
import org.junit.Test;

import javax.inject.Named;
import javax.inject.Singleton;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static com.google.common.base.Preconditions.checkState;
import static org.junit.Assert.assertEquals;

public class EventorModuleTest {
    @Test
    public void testBasicInjection() throws Exception {
        Injector injector = createInjector();
        Ch ch = injector.getInstance(Ch.class);
        ch.handle(new Cmd());
        ch.handle(new Cmd());
        El el = injector.getInstance(El.class);
        Thread.sleep(500);
        assertEquals(2, el.createdCount);
        //TODO: test aggregate identity
    }

    @Test
    public void testAggregateIdentity() throws Exception {
        Injector injector = createInjector();
        EventBus eb = injector.getInstance(EventBus.class);
        CommandBus cb = injector.getInstance(CommandBus.class);
        eb.publish(new Event(1));
        eb.publish(new Event(2));
        Thread.sleep(500);
        cb.submit(new Cmd2(1));
        cb.submit(new Cmd2(1));
        cb.submit(new Cmd2(2));
        Thread.sleep(500);
        Map<Integer, Integer> counter = injector.getInstance(Key.get(Map.class, Names.named("counter")));
        assertEquals(ImmutableMap.of(1, 2, 2, 1), counter);
    }

    private Injector createInjector() {
        return Guice.createInjector(new AbstractModule() {
            @Override
            protected void configure() {
                bind(Map.class).annotatedWith(Names.named("counter")).toInstance(new HashMap());
            }
        }, new EventorModule(Arrays.<Class<?>>asList(new Class<?>[]{MyAggregate.class, El.class})));
    }

    public static class Cmd {
    }

    public static class Cmd2 {
        int aid;

        public Cmd2(int id) {
            this.aid = id;
        }
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
        @Inject
        @Named("counter")
        Map counter;

        @EventListener
        @Start
        public void on(Event e) {
            this.id = e.id;
            checkState(!counter.containsKey(e.id));
            counter.put(e.id, 0);
        }

        @CommandHandler
        public void handle(@IdIn("aid") Cmd2 c) {
            checkState(c.aid == id);
            counter.put(id, ((Integer) counter.get(id)) + 1);
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
