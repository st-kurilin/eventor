package com.eventor.api;

public interface InstanceCreator {
    <T> T getInstanceOf(Class<T> clazz);
}
