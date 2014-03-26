package com.eventor.internal.meta;

import com.eventor.api.annotations.Id;
import com.eventor.internal.EventorReflections;

import java.util.Set;

public class MetaAggregate extends MetaSubscriber {
    public final Class<?> idClass;

    public MetaAggregate(Class<?> origClass, Class<?> idClass,
                         Set<MetaHandler> commandHandlers, Set<MetaHandler> eventHandlers) {
        super(origClass, commandHandlers, eventHandlers);
        this.idClass = idClass;
    }

    public Object retrieveId(Object aggregate) {
        return EventorReflections.retrieveAnnotatedValue(aggregate, Id.class);
    }
}
