package com.eventor.api;

public interface AggregateRepository {
    <T> T getById(Class<T> clazz, Object id);

    void save(Object id, Object aggregate);
}
