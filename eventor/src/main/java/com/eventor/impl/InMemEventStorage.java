package com.eventor.impl;

import com.eventor.api.EventStorage;

import java.util.List;
import java.util.Map;

import static com.eventor.internal.EventorCollections.newList;
import static com.eventor.internal.EventorCollections.newMap;

public class InMemEventStorage implements EventStorage {
    private final List<Object> events;
    private final Map<Object, Object> sources;

    public InMemEventStorage(List<Object> events, Map<Object, Object> sources) {
        this.events = events;
        this.sources = sources;
    }

    public InMemEventStorage() {
        this(newList(), newMap());
    }

    @Override
    public void fired(Object event, Object source) {
        events.add(event);
        sources.put(event, source);
    }

    @Override
    public Iterable<Object> getForSource(Object source) {
        List<Object> result = newList();
        for (Object each : getAll()) {
            if (source.equals(sources.get(each))) {
                result.add(each);
            }
        }
        return result;
    }

    @Override
    public Iterable<Object> getAll() {
        return events;
    }
}
