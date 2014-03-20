package com.eventor.impl;

import com.eventor.api.AggregateRepository;

import java.util.Map;

import static com.eventor.internal.EventorCollections.newMap;
import static com.eventor.internal.EventorPreconditions.assume;

public class InMemoryAggregateRepository implements AggregateRepository {
    private final Map<Object, Object> aggregates = newMap();

    @Override
    public <T> T getById(Class<T> clazz, Object id) {
        return (T) aggregates.get(id);
    }

    @Override
    public void save(Object id, Object aggregate) {
        assume(!aggregates.containsKey(id), "Could not create aggregate with duplicate id [%s]", id);
        aggregates.put(id, aggregate);
    }
}
