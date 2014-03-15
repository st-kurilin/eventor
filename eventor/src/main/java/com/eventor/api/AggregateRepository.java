package com.eventor.api;

public interface AggregateRepository {
    <T> T getById(Class<T> clazz, Object id);

    void save(Object aggregate);

    void save(Object aggregate, int expectedVersion);
}
