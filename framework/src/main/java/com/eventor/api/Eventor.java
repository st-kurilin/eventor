package com.eventor.api;

import akka.actor.*;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.japi.Creator;
import akka.routing.BroadcastRouter;
import com.eventor.internal.meta.Info;
import com.eventor.internal.meta.MetaAggregate;
import com.eventor.internal.meta.MetaHandler;
import com.eventor.internal.meta.MetaSubscriber;
import com.eventor.internal.reflection.ClassProcessor;

import java.util.ArrayList;

public class Eventor implements CommandBus {
    private final Iterable<Class<?>> aggregates;
    private final Info info;
    private final EventBus eventBus;
    private final CommandBus commandBus;
    private final InstanceCreator instanceCreator;
    ActorSystem system = ActorSystem.create("BlackDragon");
    LoggingAdapter log = Logging.getLogger(system, this);

    public Eventor(final Iterable<Class<?>> aggregates, final InstanceCreator instanceCreator) {
        this.aggregates = aggregates;
        this.instanceCreator = instanceCreator;
        info = new ClassProcessor().apply(aggregates);
        eventBus = createEventBus();
        commandBus = createCommandBus();
        instanceCreator.putInstance(CommandBus.class, commandBus);
        instanceCreator.putInstance(EventBus.class, eventBus);
    }

    private CommandBus createCommandBus() {
        ArrayList<ActorRef> commandHandlers = new ArrayList<ActorRef>();
        for (final MetaAggregate eachMetaAggregate : info.aggregates) {
            log.info("Register aggregate {} as command handler", eachMetaAggregate.origClass.getSimpleName());
            commandHandlers.add(createActor(new Listener() {
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

            }));
        }
        final ActorRef router = system.actorOf(
                Props.empty().withRouter(BroadcastRouter.create(commandHandlers)));

        CommandBus cb = new CommandBus() {
            @Override
            public void submit(Object cmd) {
                log.info("Command {} received", cmd);
                router.tell(cmd, null);
            }
        };
        return cb;
    }

    private EventBus createEventBus() {
        ArrayList<ActorRef> eventListeners = new ArrayList<ActorRef>();
        for (final MetaAggregate eachMetaAggregate : info.aggregates) {
            log.info("Register aggregate {} as event listener", eachMetaAggregate.origClass.getSimpleName());
            eventListeners.add(createActor(new Listener() {
                @Override
                public void apply(Object event) {
                    log.info("Event {} will be handled by aggregates", event);
                    handleEventByAggregate(eachMetaAggregate, event);
                }
            }));
        }
        for (final MetaSubscriber each : info.subscribers) {
            log.info("Register event listener {}", each.origClass.getSimpleName());
            eventListeners.add(createActor(new Listener() {
                @Override
                public void apply(Object event) {
                    log.info("Event {} will be handled by event listener {}", event, each.origClass.getSimpleName());
                    handleEventByEvenHandler(each, event);
                }
            }));
        }
        final ActorRef router = system.actorOf(
                Props.empty().withRouter(BroadcastRouter.create(eventListeners)));

        return new EventBus() {
            @Override
            public void publish(Object event) {
                log.info("Published to bus {}", event);
                router.tell(event, null);
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

    private ActorRef createActor(Listener listener) {
        return system.actorOf(Props.create(new ActorFromListenerCreator(listener)));
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

    //Akka limitation: Creator should be static class.
    private static class ActorFromListenerCreator implements Creator<Actor> {
        final Listener listener;

        private ActorFromListenerCreator(Listener listener) {
            this.listener = listener;
        }

        @Override
        public Actor create() throws Exception {
            return new UntypedActor() {
                @Override
                public void onReceive(Object o) throws Exception {
                    listener.apply(o);
                }
            };
        }

    }
}
