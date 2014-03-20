package com.eventor;

import com.eventor.api.AggregateRepository;
import com.eventor.api.InstanceCreator;
import com.eventor.impl.InMemoryAggregateRepository;
import com.eventor.impl.SimpleInstanceCreator;

import java.util.Arrays;
import java.util.Set;

import static com.eventor.internal.EventorCollections.newSet;

public class EventorBuilder {
    private InstanceCreator instanceCreator = new SimpleInstanceCreator();
    private AggregateRepository aggregateRepository = new InMemoryAggregateRepository();
    private Set<Class<?>> classes = newSet();

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

    public Eventor build() {
        return new Eventor(classes, instanceCreator, aggregateRepository);
    }
}
