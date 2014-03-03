package com.eventor.simpleshop;

import com.eventor.api.CommandBus;
import com.eventor.api.annotations.Aggregate;
import com.eventor.api.annotations.EventListener;
import com.eventor.api.annotations.Saga;
import com.eventor.guice.EventorModule;
import com.google.common.collect.ImmutableSet;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.eventor.simpleshop.api.registration.ConfirmEmail;
import com.eventor.simpleshop.api.registration.RegisterRequest;
import org.junit.Test;
import org.reflections.Reflections;

import java.util.Set;

public class IntegTest {
    @Test
    public void startup() throws Exception {
        Reflections reflections = new Reflections("com.shop");
        Set<Class<?>> classes = ImmutableSet.<Class<?>>builder()
                .addAll(reflections.getTypesAnnotatedWith(Aggregate.class))
                .addAll(reflections.getTypesAnnotatedWith(Saga.class))
                .addAll(reflections.getTypesAnnotatedWith(EventListener.class)).build();

        Injector injector = Guice.createInjector(new EventorModule(classes));
        injector.getInstance(CommandBus.class).submit(new RegisterRequest("foo@bar.com", "Peter", "123"));
        injector.getInstance(CommandBus.class).submit(new ConfirmEmail("foo@bar.com", "foo@bar.com"));

        //TODO: make assertion after saga impl.
        /*assertEquals(1, injector.getInstance(UsersList.class).getUsers().size());
        assertEquals("Peter", injector.getInstance(UsersList.class).getUsers().iterator().next());*/
    }
}
