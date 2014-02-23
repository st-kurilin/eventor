package com.eventor.api;

public interface InstanceCreator {
    <T> T getInstanceOf(Class<T> clazz);

    <T> void putInstance(Class<T> clazz, T obj);
}
