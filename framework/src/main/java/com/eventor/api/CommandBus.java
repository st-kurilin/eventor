package com.eventor.api;

public interface CommandBus {
    void publish(Object event);
}
