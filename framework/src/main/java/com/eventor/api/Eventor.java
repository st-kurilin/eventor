package com.eventor.api;

import com.eventor.internal.meta.Info;
import com.eventor.internal.meta.MetaAggregate;
import com.eventor.internal.meta.MetaHandler;
import com.eventor.internal.meta.MetaSubscriber;
import com.eventor.internal.reflection.ClassProcessor;

public class Eventor implements CommandBus {
    private final Iterable<Class<?>> aggregates;
    private final Info info;
    private final EventBus eventBus;
    private final CommandBus commandBus;
    private final InstanceCreator instanceCreator;

    public Eventor(final Iterable<Class<?>> aggregates, final InstanceCreator instanceCreator) {
        this.aggregates = aggregates;
        this.instanceCreator = instanceCreator;
        info = new ClassProcessor().apply(aggregates);
        createAllNeededInstancies(instanceCreator);
        eventBus = createEventBus();
        commandBus = createCommandBus();
    }

    private void createAllNeededInstancies(InstanceCreator instanceCreator) {
        for (MetaSubscriber each : info.subscribers) {
            instanceCreator.getInstanceOf(each.origClass);
        }
    }

    private CommandBus createCommandBus() {
        CommandBus commandBus = instanceCreator.getInstanceOf(SimpleCommandBus.class);
        return commandBus;
    }

    private EventBus createEventBus() {
        final EventBus eventBus = instanceCreator.getInstanceOf(SimpleEventBus.class);
        eventBus.subscribe(new Listener() {
            @Override
            public void apply(Object event) {
                for (MetaAggregate eachMetaAggregate : info.aggregates) {
                    for (MetaHandler eachMetaHandler : eachMetaAggregate.eventHandlers) {
                        if (eachMetaHandler.expected.equals(event.getClass()) && eachMetaHandler.alwaysStart) {
                            Object aggregate = instanceCreator.getInstanceOf(eachMetaAggregate.origClass);
                            Iterable<Object> events = Collections3.toIterable(eachMetaHandler.execute(aggregate, event));
                            for (Object eachEvent : events) {
                                eventBus.publish(eachEvent);
                            }
                        } else {
                            if (eachMetaHandler.expected.equals(event.getClass())) {
                                Object aggregate = instanceCreator.getInstanceOf(eachMetaAggregate.origClass);
                                Iterable<Object> events = Collections3.toIterable(eachMetaHandler.execute(aggregate, event));
                                for (Object eachEvent : events) {
                                    eventBus.publish(eachEvent);
                                }
                            }
                        }
                    }

                    for (MetaHandler eachMetaHandler : eachMetaAggregate.commandHandlers) {
                        if (eachMetaHandler.expected.equals(event.getClass()) && eachMetaHandler.alwaysStart) {
                            Object aggregate = instanceCreator.getInstanceOf(eachMetaAggregate.origClass);
                            Iterable<Object> events = Collections3.toIterable(eachMetaHandler.execute(aggregate, event));
                            for (Object eachEvent : events) {
                                eventBus.publish(eachEvent);
                            }
                        } else {
                            if (eachMetaHandler.expected.equals(event.getClass())) {
                                Object aggregate = instanceCreator.getInstanceOf(eachMetaAggregate.origClass);
                                Iterable<Object> events = Collections3.toIterable(eachMetaHandler.execute(aggregate, event));
                                for (Object eachEvent : events) {
                                    eventBus.publish(eachEvent);
                                }
                            }
                        }
                    }
                }
            }
        });
        eventBus.subscribe(new Listener() {
            @Override
            public void apply(Object event) {
                for (MetaSubscriber each : info.subscribers) {
                    for (MetaHandler eachMetaHandler : each.eventHandlers) {
                        if (eachMetaHandler.expected.equals(event.getClass())) {
                            eachMetaHandler.execute(instanceCreator.getInstanceOf(each.origClass), event);
                        }
                    }
                }
            }
        });
        return eventBus;
    }


    @Override
    public void submit(Object cmd) {

    }
}
