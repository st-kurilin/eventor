package com.eventor;

public interface SagaStorage {
    boolean contains(Object id);

    <T> T find(Class<T> sagaClass, Object id);

    void save(Iterable<?> ids, Object saga);
}
