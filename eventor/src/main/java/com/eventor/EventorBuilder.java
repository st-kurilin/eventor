package com.eventor;

import com.eventor.api.AggregateRepository;
import com.eventor.api.EventSourcedAggregateRepository;
import com.eventor.api.EventStorage;
import com.eventor.api.InstanceCreator;
import com.eventor.impl.*;
import com.eventor.internal.ClassProcessor;
import com.eventor.internal.meta.Info;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Set;

import static com.eventor.internal.EventorCollections.newSet;

public class EventorBuilder {
    private InstanceCreator instanceCreator = new SimpleInstanceCreator();
    private AggregateRepository aggregateRepository = new InMemoryAggregateRepository();
    private SagaStorage sagaStorage = new InMemorySagaStorage();
    private Set<Class<?>> classes = newSet();
    private Scheduler<? extends Serializable> scheduler = new InMemoryScheduler<Serializable>();
    private EventStorage eventStore = new InMemEventStorage();
    private boolean useEventSourcedAggregateRepository;

    public EventorBuilder addClasses(Class<?>... classes) {
        this.classes.addAll(Arrays.asList(classes));
        return this;
    }

    public EventorBuilder addClasses(Iterable<Class<?>> classes) {
        for (Class<?> each : classes) {
            this.classes.add(each);
        }
        return this;
    }

    public EventorBuilder withInstanceCreator(InstanceCreator instanceCreator) {
        this.instanceCreator = instanceCreator;
        return this;
    }

    public EventorBuilder withAggregateRepository(AggregateRepository aggregateRepository) {
        this.aggregateRepository = aggregateRepository;
        return this;
    }

    public EventorBuilder scheduler(Scheduler<? extends Serializable> scheduler) {
        this.scheduler = scheduler;
        return this;
    }

    public EventorBuilder eventStorage(EventStorage eventStore) {
        this.eventStore = eventStore;
        return this;
    }

    public EventorBuilder eventSourcedAggregateRepository() {
        this.useEventSourcedAggregateRepository = true;
        return this;
    }

    public Eventor build() {
        Info info = new ClassProcessor().apply(classes);
        if (useEventSourcedAggregateRepository) {
            aggregateRepository = new EventSourcedAggregateRepository(eventStore, instanceCreator);
        }
        return new Eventor(info,
                instanceCreator,
                aggregateRepository,
                sagaStorage,
                scheduler,
                eventStore);
    }
}
