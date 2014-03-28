package com.eventor.api;

import com.eventor.internal.ClassProcessor;
import com.eventor.internal.EventorCollections;
import com.eventor.internal.meta.MetaAggregate;
import com.eventor.internal.meta.MetaHandler;

public class EventSourcedAggregateRepository implements AggregateRepository {
    private final EventStorage eventStorage;
    private final InstanceCreator instanceCreator;

    public EventSourcedAggregateRepository(EventStorage eventStorage, InstanceCreator instanceCreator) {
        this.eventStorage = eventStorage;
        this.instanceCreator = instanceCreator;
    }

    @Override
    public <T> T getById(Class<T> clazz, Object id) {
        T aggregate = instanceCreator.findOrCreateInstanceOf(clazz, false);
        MetaAggregate metaAggregate = new ClassProcessor()
                .apply((Iterable) EventorCollections.toCollection(clazz)).aggregates.iterator().next();
        for (Object event : eventStorage.getForSource(id)) {
            for (MetaHandler eachHandler : metaAggregate.eventHandlers) {
                if (eachHandler.expected.equals(event.getClass())) {
                    eachHandler.execute(aggregate, event);
                }
            }
        }
        return aggregate;
    }

    @Override
    public void save(Object id, Object aggregate) {
    }
}
