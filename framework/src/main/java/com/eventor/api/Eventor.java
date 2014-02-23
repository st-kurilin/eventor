package com.eventor.api;

import com.eventor.impl.Akka;
import com.eventor.internal.meta.Info;
import com.eventor.internal.meta.MetaAggregate;
import com.eventor.internal.meta.MetaHandler;
import com.eventor.internal.meta.MetaSubscriber;
import com.eventor.internal.reflection.ClassProcessor;

import java.util.ArrayList;

public class Eventor implements CommandBus {
    private final Info info;
    private final EventBus eventBus;
    private final CommandBus commandBus;
    private final InstanceCreator instanceCreator;
    private final Akka akka = new Akka();
    private final Log log = akka.createLog(this);

    public Eventor(final Iterable<Class<?>> aggregates, final InstanceCreator instanceCreator) {
        this.instanceCreator = instanceCreator;
        info = new ClassProcessor().apply(aggregates);
        eventBus = createEventBus();
        commandBus = createCommandBus();
        instanceCreator.putInstance(CommandBus.class, commandBus);
        instanceCreator.putInstance(EventBus.class, eventBus);
    }

    private CommandBus createCommandBus() {
        ArrayList<Listener> commandHandlers = new ArrayList<Listener>();
        for (final MetaAggregate eachMetaAggregate : info.aggregates) {
            log.info("Register aggregate {} as command handler", eachMetaAggregate.origClass.getSimpleName());
            commandHandlers.add(new Listener() {
                @Override
                public void apply(Object event) {
                    log.info("Command {} will be handled by aggregates", event);
                    for (MetaHandler eachMetaHandler : eachMetaAggregate.commandHandlers) {
                        if (eachMetaHandler.expected.equals(event.getClass())) {
                            Object aggregate = instanceCreator.getInstanceOf(eachMetaAggregate.origClass);
                            Iterable<Object> events = Collections3.toIterable(eachMetaHandler.execute(aggregate, event));
                            for (Object eachEvent : events) {
                                eventBus.publish(eachEvent);
                            }
                        }
                    }
                }
            });
        }
        final Invokable router = akka.createBroadcaster(commandHandlers);

        CommandBus cb = new CommandBus() {
            @Override
            public void submit(Object cmd) {
                log.info("Command {} received", cmd);
                router.invoke(cmd, null);
            }
        };
        return cb;
    }

    private EventBus createEventBus() {
        ArrayList<Listener> eventListeners = new ArrayList<Listener>();
        for (final MetaAggregate eachMetaAggregate : info.aggregates) {
            log.info("Register aggregate {} as event listener", eachMetaAggregate.origClass.getSimpleName());
            eventListeners.add(new Listener() {
                @Override
                public void apply(Object event) {
                    log.info("Event {} will be handled by aggregates", event);
                    handleEventByAggregate(eachMetaAggregate, event);
                }
            });
        }
        for (final MetaSubscriber each : info.subscribers) {
            log.info("Register event listener {}", each.origClass.getSimpleName());
            eventListeners.add(new Listener() {
                @Override
                public void apply(Object event) {
                    log.info("Event {} will be handled by event listener {}", event, each.origClass.getSimpleName());
                    handleEventByEvenHandler(each, event);
                }
            });
        }
        final Invokable router = akka.createBroadcaster(eventListeners);

        return new EventBus() {
            @Override
            public void publish(Object event) {
                log.info("Published to bus {}", event);
                router.invoke(event, null);
            }

            @Override
            public void subscribe(Listener subscriber) {
                throw new UnsupportedOperationException();
            }
        };
    }

    private void handleEventByEvenHandler(MetaSubscriber eventListener, Object event) {
        for (MetaHandler eachMetaHandler : eventListener.eventHandlers) {
            if (eachMetaHandler.expected.equals(event.getClass())) {
                eachMetaHandler.execute(instanceCreator.getInstanceOf(eventListener.origClass), event);
            }
        }
    }

    private void handleEventByAggregate(MetaAggregate eachMetaAggregate, Object event) {
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
    }

    @Override
    public void submit(Object cmd) {
        commandBus.submit(cmd);
    }
}
