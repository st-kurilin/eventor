package com.eventor.api;

import java.util.Set;

public class Eventor implements CommandBus {
    private final Set<Class<?>> aggregates;

    public Eventor(Set<Class<?>> aggregates) {
        this.aggregates = aggregates;
    }

    @Override
    public void publish(Object event) {

    }
}
