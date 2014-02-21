package com.eventor.api;

public interface EventBus {
    void publish(Object event);

    void subscribe(Listener subscriber);
}
