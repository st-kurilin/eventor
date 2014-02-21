package com.eventor.api;

import java.util.HashSet;
import java.util.Set;

public class SimpleEventBus implements EventBus {
    Set<Listener> subscribers = new HashSet<Listener>();

    @Override
    public void publish(Object event) {
        for (Listener each : subscribers) {
            each.apply(event);
        }
    }

    @Override
    public void subscribe(Listener subscriber) {
        subscribers.add(subscriber);
    }
}
