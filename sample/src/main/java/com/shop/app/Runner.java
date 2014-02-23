package com.shop.app;

import com.eventor.api.Aggregate;
import com.eventor.api.CommandBus;
import com.eventor.api.EventListener;
import com.eventor.api.Saga;
import com.google.common.collect.ImmutableSet;
import com.shop.api.registration.AskToJoin;
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

        commandBus.submit(new AskToJoin("my@gmail.com", "Joe", "1234"));
    }
}
