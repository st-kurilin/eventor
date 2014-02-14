package com.eventor.internal.meta;

import java.util.Set;

public class MetaAggregate extends MetaSubscriber {
    public MetaAggregate(Class<?> origClass, Set<MetaHandler> commandHandlers, Set<MetaHandler> eventHandlers) {
        super(origClass, commandHandlers, eventHandlers);
    }
}
