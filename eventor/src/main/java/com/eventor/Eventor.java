package com.eventor;

import com.eventor.api.*;
import com.eventor.api.annotations.OnTimeout;
import com.eventor.impl.Scheduler;
import com.eventor.internal.Akka;
import com.eventor.internal.EventorCollections;
import com.eventor.internal.meta.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.List;

import static com.eventor.internal.EventorCollections.newList;
import static com.eventor.internal.EventorPreconditions.assume;
import static com.eventor.internal.EventorPreconditions.assumeNotNull;
import static com.eventor.internal.EventorReflections.getMethodsAnnotated;
import static com.eventor.internal.EventorReflections.invoke;

public class Eventor implements CommandBus {
    private final Info info;
    private final InstanceCreator instanceCreator;
    private final Akka akka = new Akka();
    private final CommandBus commandBus;
    private final EventBus eventBus;
    private final AggregateRepository repository;
    private final SagaStorage sagaStorage;
    private final Scheduler<SagaTimeoutData> scheduler;

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    public Eventor(Info info,
                   InstanceCreator instanceCreator,
                   AggregateRepository repository,
                   SagaStorage sagaStorage,
                   Scheduler<?> scheduler) {
        this.instanceCreator = instanceCreator;
        this.repository = repository;
        this.sagaStorage = sagaStorage;
        this.info = info;
        this.scheduler = configScheduler(scheduler);
        this.eventBus = createEventBus();
        this.commandBus = createCommandBus();
    }

    private Scheduler<SagaTimeoutData> configScheduler(Scheduler<?> orig) {
        //TODO: should scheduler factory be introduced?
        Scheduler<SagaTimeoutData> res = (Scheduler<SagaTimeoutData>) orig;
        res.addListener(new Listener() {
            @Override
            public void apply(Object obj) {
                SagaTimeoutData timeoutData = (SagaTimeoutData) obj;
                log.info("Handle delayed execution {} for saga {}", timeoutData.timeoutEvent, timeoutData.sagaId);
                if (sagaStorage.contains(timeoutData.sagaId)) {
                    Object saga = sagaStorage.find(timeoutData.getSagaClass(), timeoutData.sagaId);
                    for (Method each : getMethodsAnnotated(saga.getClass(), OnTimeout.class).get(OnTimeout.class)) {
                        if (each.getParameterTypes().length == 1) {
                            sagaResultsPostprocess(saga,
                                    invoke(saga, each, timeoutData.timeoutEvent),
                                    timeoutData.sagaId);
                        } else {
                            assume(each.getParameterTypes().length == 0,
                                    "On timeout methods could take 0 or 1 argument");
                            sagaResultsPostprocess(saga, invoke(saga, each), timeoutData.sagaId);
                        }
                    }
                }
            }
        });
        return res;
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
                    Object aggregate = repository.getById(eachMetaAggregate.origClass, aggregateId);
                    if (aggregate != null) {
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

                if (sagaStorage.contains(sagaId)) {
                    Object saga = sagaStorage.find(eachMetaSaga.origClass, sagaId);
                    handleMessageBySaga(saga, eachMetaSaga, eachMetaHandler, cmd);
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
                }
            }
        }
    }

    private void saveAggregate(MetaAggregate eachMetaAggregate, Object aggregate, Object message) {
        Object id = eachMetaAggregate.retrieveId(aggregate);
        assumeNotNull(id, "Aggregate id could not be null");
        repository.save(id, aggregate);
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
                Object sagaId = eachMetaHandler.extractId(event);
                if (sagaStorage.contains(sagaId)) {
                    handleMessageBySaga(sagaStorage.find(eachMetaSaga.origClass, sagaId), eachMetaSaga, eachMetaHandler, event);
                } else if (eachMetaHandler.alwaysStart) {
                    Object saga = instanceCreator.findOrCreateInstanceOf(eachMetaSaga.origClass, false);
                    handleMessageBySaga(saga, eachMetaSaga, eachMetaHandler, event);
                    Object id = sagaId == null ? eachMetaSaga.retrieveId(saga) : sagaId;
                    assume(!sagaStorage.contains(id),
                            "Could not create saga with duplicate id [%s] on event [%s]",
                            id, event);
                    sagaStorage.save(EventorCollections.toCollection(id), saga);
                    log.info("Saga with id {} registered", id);
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

    private void handleMessageBySaga(Object saga, MetaSaga eachMetaSaga, MetaHandler eachMetaHandler, Object msg) {
        Collection<?> handlingResult = eachMetaHandler.execute(saga, msg);
        Object sagaId = eachMetaSaga.retrieveId(saga);
        sagaResultsPostprocess(saga, handlingResult, sagaId);
    }

    private void sagaResultsPostprocess(Object saga, Collection<?> handlingResult, Object sagaId) {
        for (Object each : handlingResult) {
            if (each instanceof Timeout) {
                Timeout timeout = (Timeout) each;
                scheduler.doLater(new SagaTimeoutData(sagaId, timeout.timeoutEvent, saga.getClass()), timeout.duration, timeout.unit);
            } else {
                commandBus.submit(each);
            }
        }
    }

    private static class SagaTimeoutData implements Serializable {
        Object sagaId;
        String sagaClassName;
        Object timeoutEvent;

        private SagaTimeoutData(Object sagaId, Object timeoutEvent, Class<?> sagaClass) {
            this.sagaId = sagaId;
            this.timeoutEvent = timeoutEvent;
            this.sagaClassName = sagaClass.getName();
        }

        Class<?> getSagaClass() {
            try {
                return Class.forName(sagaClassName);
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
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
