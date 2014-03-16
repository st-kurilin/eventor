package com.eventor;

import com.eventor.api.InstanceCreator;
import com.eventor.impl.SimpleInstanceCreator;

import java.util.Arrays;
import java.util.Set;

import static com.eventor.internal.EventorCollections.newSet;

public class EventorBuilder {
    private InstanceCreator instanceCreator = new SimpleInstanceCreator();
    private Set<Class<?>> classes = newSet();

    public EventorBuilder addClasses(Class<?>... classes) {
        this.classes.addAll(Arrays.asList(classes));
        return this;
    }

    public EventorBuilder withInstanceCreator(InstanceCreator instanceCreator) {
        this.instanceCreator = instanceCreator;
        return this;
    }

    public Eventor build() {
        return new Eventor(classes, instanceCreator);
    }
}
