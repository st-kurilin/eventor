package com.eventor.impl;

import com.eventor.SagaStorage;

import java.util.Map;

import static com.eventor.internal.EventorCollections.newMap;

public class InMemorySagaStorage implements SagaStorage {
    private final Map<Object, Object> sagas = newMap();

    @Override
    public boolean contains(Object id) {
        return sagas.containsKey(id);
    }

    @Override
    public <T> T find(Class<T> sagaClass, Object id) {
        return (T) sagas.get(id);
    }

    @Override
    public void save(Iterable<?> ids, Object saga) {
        for (Object id : ids) {
            sagas.put(id, saga);
        }
    }
}
