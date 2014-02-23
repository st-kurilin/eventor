package com.eventor.impl;

import com.eventor.api.EventBus;
import com.eventor.api.Listener;

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
}
