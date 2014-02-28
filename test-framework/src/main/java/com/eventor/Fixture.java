package com.eventor;

import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.eventor.internal.EventorCollections.newList;

public class Fixture {
    private Iterable<Object> events;
    private Iterable<Object> commands;
    private long delay;
    private TimeUnit delayUnit;
    private Iterable<Object> expectedEvents;
    private Iterable<Object> notExpectedEvents;
    private MatchMode matchMode;
    private boolean andNoMoreFlag;
    private static Object NO_MORE = new Object();

    public When givenEvents(Object... events) {
        this.events = convert(events);
        return new When();
    }

    public class When {
        public ThenOrTime whenCommands(Object... commands) {
            Fixture.this.commands = convert(commands);
            return new ThenOrTime();
        }
    }

    public class ThenOrTime extends Then {

        public Then addTimePassed(long time, TimeUnit unit) {
            Fixture.this.delay = time;
            Fixture.this.delayUnit = unit;
            return new Then();
        }
    }

    public class Then {
        public void thenEventsContainsAnyOf(Object... events) {
            Fixture.this.expectedEvents = convertFinalEvents(events);
            matchMode = MatchMode.ANY;
            run();
        }

        public void thenEventsContainsAllOf(Object... events) {
            Fixture.this.expectedEvents = convert(events);
            matchMode = MatchMode.ALL;
            run();
        }

        public void thenEventsDoesntContain(Object... events) {
            Fixture.this.notExpectedEvents = convert(events);
            matchMode = MatchMode.ALL;
            run();
        }
    }

    public static Object andNoMore() {
        return NO_MORE;
    }

    private enum MatchMode {
        ALL, ANY
    }

    private void run() {
    }

    private Iterable<Object> convert(Object[] inp) {
        List<Object> res = newList();
        for (Object each : inp) res.add(each);
        return res;
    }

    private Iterable<Object> convertFinalEvents(Object[] inp) {
        List<Object> res = newList();
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
