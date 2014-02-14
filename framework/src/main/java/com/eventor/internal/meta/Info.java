package com.eventor.internal.meta;

import java.util.Set;

public class Info {
    public final Set<MetaAggregate> aggregates;
    public final Set<MetaSaga> sagas;
    public final Set<MetaSubscriber> subscribers;

    public Info(Set<MetaAggregate> aggregates, Set<MetaSaga> sagas, Set<MetaSubscriber> subscribers) {
        this.aggregates = aggregates;
        this.sagas = sagas;
        this.subscribers = subscribers;
    }
}
