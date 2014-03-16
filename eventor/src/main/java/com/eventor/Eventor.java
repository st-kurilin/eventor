package com.eventor;

import com.eventor.api.*;
import com.eventor.internal.Akka;
import com.eventor.internal.ClassProcessor;
import com.eventor.internal.EventorCollections;
import com.eventor.internal.meta.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.eventor.internal.EventorCollections.newList;
import static com.eventor.internal.EventorPreconditions.assume;
import static com.eventor.internal.EventorPreconditions.assumeNotNull;

public class Eventor implements CommandBus {
    private final Info info;
    private final InstanceCreator instanceCreator;
    private final Akka akka = new Akka();
    private final Map<Object, Object> aggregates = new HashMap<Object, Object>();
    private final Map<Object, Object> sagas = new HashMap<Object, Object>();
    private final CommandBus commandBus;
    private final EventBus eventBus;

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    public Eventor(final Iterable<Class<?>> aggregates, final InstanceCreator instanceCreator) {
        this.instanceCreator = instanceCreator;
        info = new ClassProcessor().apply(aggregates);
        this.eventBus = createEventBus();
        this.commandBus = createCommandBus();
    }

    private EventBus createEventBus() {
        List<Listener> eventListeners = EventorCollections.newList();
        for (MetaAggregate eachMetaAggregate : info.aggregates) {
            log.info("Register aggregate {} as event listener", eachMetaAggregate.origClass.getSimpleName());
            eventListeners.add(aggregateAsListener(eachMetaAggregate));
        }
        for (MetaSaga eachMetaSaga : info.sagas) {
            log.info("Register saga {} as event listener", eachMetaSaga.origClass.getSimpleName());
            eventListeners.add(sagaAsListener(eachMetaSaga));
        }
        for (MetaSubscriber each : info.subscribers) {
            log.info("Register event listener {}", each.origClass.getSimpleName());
            eventListeners.add(eventListenerAsListener(each));
        }
        final Invokable router = akka.createBroadcaster(eventListeners);
        return new EventBus() {
            @Override
            public void publish(Object event) {
                log.info("Published to bus {}", event);
                router.invoke(event, null);
            }
        };
    }

    private CommandBus createCommandBus() {
        List<Listener> commandHandlers = newList();
        for (final MetaSubscriber eachCommandHandler : info.subscribers) {
            log.info("Register command handler {}", eachCommandHandler.origClass.getSimpleName());
            commandHandlers.add(commandHandlerAsCommandHandlerAsListener(eachCommandHandler));
        }
        for (final MetaAggregate eachMetaAggregate : info.aggregates) {
            log.info("Register aggregate {} as command handler", eachMetaAggregate.origClass.getSimpleName());
            commandHandlers.add(aggregateAsCommandHandlerAsListener(eachMetaAggregate));
        }
        for (final MetaSaga eachMetaSaga : info.sagas) {
            log.info("Register saga {} as command handler", eachMetaSaga.origClass.getSimpleName());
            commandHandlers.add(sagaAsCommandHandlerAsListener(eachMetaSaga));
        }
        final Invokable router = akka.createBroadcaster(commandHandlers);
        return new CommandBus() {
            @Override
            public void submit(Object cmd) {
                log.info("Command {} received", cmd);
                router.invoke(cmd, null);
            }
        };
    }

    private void handleCmdByAggregate(MetaAggregate eachMetaAggregate, Object cmd) {
        for (MetaHandler eachMetaHandler : eachMetaAggregate.commandHandlers) {
            if (eachMetaHandler.expected.equals(cmd.getClass())) {
                Object aggregateId = eachMetaHandler.extractId(cmd);
                if (aggregateId == null && eachMetaHandler.alwaysStart) {
                    Object aggregate = instanceCreator.findOrCreateInstanceOf(eachMetaAggregate.origClass, false);
                    handleCmdByAggregate(cmd, eachMetaHandler, aggregate);
                    saveAggregate(eachMetaAggregate, aggregate, cmd);
                } else {
                    if (aggregates.containsKey(aggregateId)) {
                        Object aggregate = aggregates.get(aggregateId);
                        handleCmdByAggregate(cmd, eachMetaHandler, aggregate);
                    }
                }
            }
        }
    }

    private void handleCmdByAggregate(Object cmd, MetaHandler eachMetaHandler, Object aggregate) {
        Collection<?> events = EventorCollections.toCollection(eachMetaHandler.execute(aggregate, cmd));
        log.debug("Aggregate {} produced {} while handling cmd {}", aggregate, events, cmd);
        for (Object eachEvent : events) {
            handleEventByAggregate(aggregate, eachEvent);
        }
        for (Object eachEvent : events) {
            eventBus.publish(eachEvent);
        }
    }

    private void handleCmdBySaga(MetaSaga eachMetaSaga, Object cmd) {
        for (MetaHandler eachMetaHandler : eachMetaSaga.commandHandlers) {
            if (eachMetaHandler.expected.equals(cmd.getClass())) {
                Object sagaId = eachMetaHandler.extractId(cmd);
                if (sagas.containsKey(sagaId)) {
                    Object saga = sagas.get(sagaId);
                    handleMessageBySaga(saga, eachMetaHandler, cmd);
                }
            }
        }
    }

    private void handleEventByEvenListener(MetaSubscriber eventListener, Object event) {
        for (MetaHandler eachMetaHandler : eventListener.eventHandlers) {
            if (eachMetaHandler.expected.equals(event.getClass())) {
                Object l = instanceCreator.findOrCreateInstanceOf(eventListener.origClass, true);
                eachMetaHandler.execute(l, event);
            }
        }
    }

    private void handleCommandByCommandHandler(MetaSubscriber eachCommandHandler, Object cmd) {
        for (MetaHandler eachMetaHandler : eachCommandHandler.commandHandlers) {
            if (eachMetaHandler.expected.equals(cmd.getClass())) {
                Object ch = instanceCreator.findOrCreateInstanceOf(eachCommandHandler.origClass, true);
                Collection<?> result = eachMetaHandler.execute(ch, cmd);
                for (Object event : result) {
                    eventBus.publish(event);
                }
            }
        }
    }

    private void handleEventByAggregate(Object aggregate, Object event) {
        Class<?> actualClass = aggregate.getClass();
        for (MetaAggregate metaAggregate : info.aggregates) {
            if (metaAggregate.origClass.equals(actualClass)) {
                for (MetaHandler eachMetaHandler : metaAggregate.eventHandlers) {
                    if (eachMetaHandler.expected.equals(event.getClass())) {
                        handleEventByAggregate(aggregate, eachMetaHandler, event);
                    }
                }
            }
        }
    }

    private void handleEventByAggregate(final MetaAggregate eachMetaAggregate, final Object event) {
        for (MetaHandler eachMetaHandler : eachMetaAggregate.eventHandlers) {
            if (eachMetaHandler.expected.equals(event.getClass())) {
                log.debug("Event {} will be handled by instances of {}", event, eachMetaAggregate.origClass);
                if (eachMetaHandler.alwaysStart) {
                    Object aggregate = instanceCreator.findOrCreateInstanceOf(eachMetaAggregate.origClass, false);
                    handleEventByAggregate(aggregate, eachMetaHandler, event);
                    saveAggregate(eachMetaAggregate, aggregate, event);
                } else {
                    for (Object aggregate : aggregates.values()) {
                        handleEventByAggregate(aggregate, eachMetaHandler, event);
                    }
                }
            }
        }
    }

    private void saveAggregate(MetaAggregate eachMetaAggregate, Object aggregate, Object message) {
        Object id = eachMetaAggregate.retrieveId(aggregate);
        assumeNotNull(id, "Aggregate id could not be null");
        assume(!aggregates.containsKey(id),
                "Could not create aggregate with duplicate id [%s] on [%s]",
                id, message);
        aggregates.put(id, aggregate);
        log.info("Aggregate with id {} registered", id);
    }

    private void handleEventByAggregate(Object aggregate, MetaHandler eachMetaHandler, Object event) {
        log.debug("Handle event {} by aggregate {}", event, aggregate);
        Collection<?> res = eachMetaHandler.execute(aggregate, event);
        if (!res.isEmpty()) throw new RuntimeException("Void expected");
    }

    private void handleEventBySaga(final MetaSaga eachMetaSaga, final Object event) {
        for (MetaHandler eachMetaHandler : eachMetaSaga.eventHandlers) {
            if (eachMetaHandler.expected.equals(event.getClass())) {
                if (eachMetaHandler.alwaysStart) {
                    Object saga = instanceCreator.findOrCreateInstanceOf(eachMetaSaga.origClass, false);
                    handleMessageBySaga(saga, eachMetaHandler, event);
                    Object id = eachMetaSaga.retrieveId(saga);
                    assume(!sagas.containsKey(id),
                            "Could not create saga with duplicate id [%s] on event [%s]",
                            id, event);
                    sagas.put(id, saga);
                    log.info("Saga with id {} registered", id);
                } else {
                    for (Object saga : sagas.values()) {
                        handleMessageBySaga(saga, eachMetaHandler, event);
                    }
                }
            }
        }
    }

    private Listener commandHandlerAsCommandHandlerAsListener(final MetaSubscriber eachCommandHandler) {
        return new Listener() {
            @Override
            public void apply(Object cmd) {
                log.info("Command {} will be handled by command handlers", cmd);
                handleCommandByCommandHandler(eachCommandHandler, cmd);
            }
        };
    }

    private void handleMessageBySaga(Object saga, MetaHandler eachMetaHandler, Object msg) {
        Collection<?> commands = eachMetaHandler.execute(saga, msg);
        for (Object message : commands) {
            commandBus.submit(message);
        }
    }

    private Listener aggregateAsCommandHandlerAsListener(final MetaAggregate eachMetaAggregate) {
        return new Listener() {
            @Override
            public void apply(Object cmd) {
                log.info("Command {} will be handled by aggregates", cmd);
                handleCmdByAggregate(eachMetaAggregate, cmd);
            }
        };
    }

    private Listener sagaAsCommandHandlerAsListener(final MetaSaga eachMetaSaga) {
        return new Listener() {
            @Override
            public void apply(Object cmd) {
                log.info("Command {} will be handled by sagas", cmd);
                handleCmdBySaga(eachMetaSaga, cmd);
            }
        };
    }

    private Listener eventListenerAsListener(final MetaSubscriber each) {
        return new Listener() {
            @Override
            public void apply(Object event) {
                log.info("Event {} will be handled by event listener {}", event, each.origClass.getSimpleName());
                handleEventByEvenListener(each, event);
            }
        };
    }

    private Listener aggregateAsListener(final MetaAggregate eachMetaAggregate) {
        return new Listener() {
            @Override
            public void apply(Object event) {
                log.info("Event {} will be handled by aggregates", event);
                handleEventByAggregate(eachMetaAggregate, event);
            }
        };
    }

    private Listener sagaAsListener(final MetaSaga eachMetaSaga) {
        return new Listener() {
            @Override
            public void apply(Object event) {
                log.info("Event {} will be handled by sagas", event);
                handleEventBySaga(eachMetaSaga, event);
            }
        };
    }

    @Override
    public void submit(Object cmd) {
        commandBus.submit(cmd);
    }

    public EventBus getEventBus() {
        return eventBus;
    }
}
