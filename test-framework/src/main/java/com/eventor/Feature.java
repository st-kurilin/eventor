package com.eventor;

import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.eventor.internal.EventorCollections.newList;

public class Feature {
    private Iterable<Object> events;
    private Iterable<Object> commands;
    private long delay;
    private TimeUnit delayUnit;
    private Iterable<Object> expectedEvents;
    private MatchMode matchMode;
    private boolean andNoMoreFlag;
    private static Object NO_MORE = new Object();

    public When givenEvents(Object... events) {
        this.events = convert(events);
        return new When();
    }

    public class When {
        ThenOrTime whenCommands(Object... commands) {
            Feature.this.commands = convert(commands);
            return new ThenOrTime();
        }
    }

    public class ThenOrTime extends Then {

        Then addTimePassed(long time, TimeUnit unit) {
            Feature.this.delay = time;
            Feature.this.delayUnit = unit;
            return new Then();
        }
    }

    public class Then {
        void thenEventsContainsAnyOf(Object... events) {
            Feature.this.expectedEvents = convertFinalEvents(events);
            matchMode = MatchMode.ANY;
            run();
        }

        void thenEventsContainsAllOf(Object... events) {
            Feature.this.expectedEvents = convert(events);
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
