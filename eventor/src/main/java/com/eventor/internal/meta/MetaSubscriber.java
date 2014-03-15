package com.eventor.internal.meta;

import java.util.Set;

public class MetaSubscriber {
    public final Class<?> origClass;
    public final Set<MetaHandler> commandHandlers;
    public final Set<MetaHandler> eventHandlers;

    public MetaSubscriber(Class<?> origClass, Set<MetaHandler> commandHandlers, Set<MetaHandler> eventHandlers) {
        this.origClass = origClass;
        this.commandHandlers = commandHandlers;
        this.eventHandlers = eventHandlers;
    }
}
