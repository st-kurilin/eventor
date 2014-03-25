package com.eventor.impl;

import com.eventor.api.Listener;
import com.eventor.internal.EventorCollections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class InMemoryScheduler<D extends Serializable> implements Scheduler<D> {
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private final Set<Listener> listeners = EventorCollections.newSet();

    @Override
    public void doLater(final D action, long delay, TimeUnit unit) {
        scheduler.schedule(new Runnable() {
            @Override
            public void run() {
                log.info("Time to execute delayed action {}", action);
                for (Listener each : listeners) {
                    each.apply(action);
                }
            }
        }, delay, unit);
        log.info("Scheduled {} to be done in {} {}", action, delay, unit);

    }

    @Override
    public void addListener(Listener doer) {
        listeners.add(doer);
    }
}
