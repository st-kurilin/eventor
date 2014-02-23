package com.eventor.impl;

import akka.actor.*;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.japi.Creator;
import akka.routing.BroadcastRouter;
import com.eventor.api.Invokable;
import com.eventor.api.Listener;
import com.eventor.api.Log;

import java.util.ArrayList;

public class Akka {
    private final ActorSystem system = ActorSystem.create("BlackDragon");
    private final Log log = createLog(this);

    public Log createLog(Object that) {
        final LoggingAdapter loggingAdapter = Logging.getLogger(system, that);
        return new Log() {
            @Override
            public void debug(String msg, Object... args) {
                loggingAdapter.debug(msg, args);
            }

            @Override
            public void info(String msg, Object... args) {
                loggingAdapter.info(msg, args);
            }

            @Override
            public void warn(String msg, Object... args) {
                loggingAdapter.warning(msg, args);
            }
        };
    }

    public ActorRef createActor(Listener listener) {
        return system.actorOf(Props.create(new ActorFromListenerCreator(listener)));
    }

    public Invokable createBroadcaster(ArrayList<Listener> listeners) {
        ArrayList<ActorRef> refs = new ArrayList<ActorRef>();
        for (Listener each : listeners) {
            refs.add(createActor(each));
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
