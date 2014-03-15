package com.eventor.internal;

import akka.actor.*;
import akka.japi.Creator;
import akka.routing.BroadcastRouter;
import com.eventor.api.Invokable;
import com.eventor.api.Listener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class Akka {
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private final ActorSystem system = ActorSystem.create("BlackDragon");

    public Invokable createInvokable(Listener listener) {
        return wrap(createActor(listener));
    }

    private ActorRef createActor(Listener listener) {
        return system.actorOf(Props.create(new ActorFromListenerCreator(listener)));
    }

    public Invokable createBroadcaster(Iterable<Listener> listeners) {
        List<ActorRef> refs = EventorCollections.newList();
        for (Listener each : listeners) {
            refs.add(createActor(each));
        }
        if (refs.isEmpty()) {
            return new Invokable() {
                @Override
                public Object invoke(Object arg, Object invoker) {
                    log.debug("Nothing to invoke on {}", arg);
                    return null;
                }
            };
        }
        return wrap(system.actorOf(Props.empty().withRouter(BroadcastRouter.create(refs))));
    }

    private Invokable wrap(final ActorRef actorRef) {
        return new Invokable() {
            @Override
            public Object invoke(Object arg, Object invoker) {
                ActorRef sender = invoker == null ? ActorRef.noSender() : ((ActorRef) invoker);
                actorRef.tell(arg, sender);
                return null;
            }
        };
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
