package com.shop.app;

import com.eventor.api.CommandBus;
import com.eventor.api.annotations.Aggregate;
import com.eventor.api.annotations.EventListener;
import com.eventor.api.annotations.Saga;
import com.google.common.collect.ImmutableSet;
import com.shop.api.registration.RegisterRequest;
import org.reflections.Reflections;

import java.util.Set;

public class Runner {
    public static void main(String[] args) {
        Reflections reflections = new Reflections("com.shop");
        Set<Class<?>> classes = ImmutableSet.<Class<?>>builder()
                .addAll(reflections.getTypesAnnotatedWith(Aggregate.class))
                .addAll(reflections.getTypesAnnotatedWith(Saga.class))
                .addAll(reflections.getTypesAnnotatedWith(EventListener.class)).build();

        CommandBus commandBus = null; //todo: init

        commandBus.submit(new RegisterRequest("my@gmail.com", "Joe", "1234"));
    }
}
