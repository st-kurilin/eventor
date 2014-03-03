package com.eventor.internal.meta;

import com.eventor.api.annotations.Id;
import com.eventor.internal.EventorReflections;

import java.util.Set;

public class MetaSaga extends MetaSubscriber {

    public MetaSaga(Class<?> origClass, Set<MetaHandler> commandHandlers, Set<MetaHandler> eventHandlers) {
        super(origClass, commandHandlers, eventHandlers);
    }

    public Object retrieveId(Object saga) {
        return EventorReflections.retrieveAnnotatedValue(saga, Id.class);

    }
}
