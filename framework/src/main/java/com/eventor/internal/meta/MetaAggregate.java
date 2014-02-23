package com.eventor.internal.meta;

import com.eventor.api.annotations.Id;

import java.lang.reflect.Field;
import java.util.Set;

public class MetaAggregate extends MetaSubscriber {
    public MetaAggregate(Class<?> origClass, Set<MetaHandler> commandHandlers, Set<MetaHandler> eventHandlers) {
        super(origClass, commandHandlers, eventHandlers);
    }

    public Object retrieveId(Object aggregate) {
        try {
            for (Field f : aggregate.getClass().getDeclaredFields()) {
                if (f.isAnnotationPresent(Id.class)) {
                    f.setAccessible(true);
                    return f.get(aggregate);
                }
            }
            throw new RuntimeException("Fail while getting id on " + aggregate);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}
