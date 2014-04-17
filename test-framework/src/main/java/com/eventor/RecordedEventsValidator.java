package com.eventor;

import java.util.Set;

public class RecordedEventsValidator {
    private Set<Object> recordedEvents;

    public RecordedEventsValidator(Set<Object> recordedEvents) {
        this.recordedEvents = recordedEvents;
    }

    public RecordedEventsValidator contains(Object event) {
        if (!recordedEvents.contains(event)) {
            fail("Could not find event %s in %s", event, recordedEvents);
        }
        return this;
    }

    public RecordedEventsValidator doesntContain(Object event) {
        if (recordedEvents.contains(event)) {
            fail("Event %s wasn't expected in %s", event, recordedEvents);
        }
        return this;
    }

    public RecordedEventsValidator containsAnyOf(Object... events) {
        for (Object each : events) {
            if (recordedEvents.contains(each)) return this;
        }
        return fail("Non from %s are contained in %s", events, recordedEvents);
    }


    private RecordedEventsValidator fail(String templ, Object... args) {
        throw new AssertionError(String.format(templ, args));
    }

    public Set<Object> getEvents() {
        return recordedEvents;
    }
}
