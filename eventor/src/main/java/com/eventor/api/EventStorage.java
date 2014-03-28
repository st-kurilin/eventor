package com.eventor.api;

public interface EventStorage {
    void fired(Object event, Object source);

    Iterable<Object> getForSource(Object source);

    Iterable<Object> getAll();
}
