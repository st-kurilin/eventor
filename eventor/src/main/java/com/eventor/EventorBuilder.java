package com.eventor;

import com.eventor.api.AggregateRepository;
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

    public Eventor build() {
        Info info = new ClassProcessor().apply(classes);
        return new Eventor(info, instanceCreator, aggregateRepository, sagaStorage, scheduler);
    }
}
