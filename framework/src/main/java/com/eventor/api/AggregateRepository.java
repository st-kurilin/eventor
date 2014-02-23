package com.eventor.api;

public interface AggregateRepository {
    <T> T load(Class<T> clazz, Object id);
}
