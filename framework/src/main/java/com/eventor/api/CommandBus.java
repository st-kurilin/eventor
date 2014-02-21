package com.eventor.api;

public interface CommandBus {
    void submit(Object event);
}
