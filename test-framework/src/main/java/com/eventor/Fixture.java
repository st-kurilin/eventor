package com.eventor;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import static com.eventor.internal.EventorCollections.newList;
import static com.eventor.internal.EventorCollections.newSet;

public class Fixture {
    private Iterable<Object> events;
    private Iterable<Object> commands;
    private long delay;
    private TimeUnit delayUnit;
    private Iterable<Object> expectedEvents;
    private Iterable<Object> notExpectedEvents;
    private boolean andNoMoreFlag;
    private static Object NO_MORE = new Object();
    private Eventor eventor;

    public Fixture(Class<?>... classes) {
    }

    public When givenEvents(Object... events) {
        this.events = convert(events);
        return new When();
    }

    public class When {
        public When whenCommands(Object... commands) {
            Fixture.this.commands = convert(commands);
            return this;
        }

        public When addTimePassed(long time, TimeUnit unit) {
            Fixture.this.delay = time;
            Fixture.this.delayUnit = unit;
            return this;
        }

        public Then then() {
            return new Then();
        }
    }


    public class Then {
        public Then eventsContainsAnyOf(Object... events) {
            return this;
        }

        public Then eventsContainsAllOf(Object... events) {
            //new EventMatchers.Contains(convertFinalEvents(events)).on(EventorCollections.newSet(), false);
            return this;
        }

        public Then eventsDoesntContain(Object... events) {
            return this;
        }
    }

    public static Object andNoMore() {
        return NO_MORE;
    }

    private Iterable<Object> convert(Object[] inp) {
        List<Object> res = newList();
        Collections.addAll(res, inp);
        return res;
    }

    private Set<Object> convertFinalEvents(Object[] inp) {
        Set<Object> res = newSet();
        for (Object each : inp) {
            if (NO_MORE.equals(each)) {
                if (andNoMoreFlag) throw new RuntimeException("andNoMore() should not be passed twice");
                andNoMoreFlag = true;
            } else {
                res.add(each);
            }
        }
        return res;
    }
}
