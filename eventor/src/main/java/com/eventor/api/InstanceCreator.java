package com.eventor.api;

public interface InstanceCreator {
    <T> T findInstanceOf(Class<T> clazz);

    <T> T findOrCreateInstanceOf(Class<T> clazz, boolean isExpectedToBeSingleton);
}
