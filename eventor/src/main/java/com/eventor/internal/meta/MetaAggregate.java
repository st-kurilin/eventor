package com.eventor.internal.meta;

import com.eventor.api.annotations.Id;
import com.eventor.internal.EventorReflections;

import java.util.Set;

public class MetaAggregate extends MetaSubscriber {
    public MetaAggregate(Class<?> origClass, Set<MetaHandler> commandHandlers, Set<MetaHandler> eventHandlers) {
        super(origClass, commandHandlers, eventHandlers);
    }

    public Object retrieveId(Object aggregate) {
        return EventorReflections.retrieveAnnotatedValue(aggregate, Id.class);

    }
}
