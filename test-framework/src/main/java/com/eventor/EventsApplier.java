package com.eventor;

import com.eventor.internal.ClassProcessor;
import com.eventor.internal.meta.MetaHandler;
import com.eventor.internal.meta.MetaSubscriber;

import java.util.Collections;
import java.util.Set;

public class EventsApplier {
    public static void apply(Object listener, Object... events) {
        Set<MetaSubscriber> subscribers =
                new ClassProcessor().apply(Collections.<Class<?>>nCopies(1, listener.getClass())).subscribers;
        if (subscribers.size() != 1) {
            throw new IllegalStateException(subscribers.toString());
        }
        MetaSubscriber next = subscribers.iterator().next();
        for (Object eachEvent : events) {
            for (MetaHandler eachHandler : next.eventHandlers) {
                if (eachHandler.expected.equals(eachEvent.getClass())) {
                    eachHandler.execute(listener, eachEvent);
                }
            }
        }

    }
}
