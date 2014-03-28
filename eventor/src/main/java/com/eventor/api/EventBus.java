package com.eventor.api;

public interface EventBus {
    void publish(Object event, Object source);
}
