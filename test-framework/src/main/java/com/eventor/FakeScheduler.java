package com.eventor;

import com.eventor.api.Listener;
import com.eventor.impl.Scheduler;
import com.eventor.internal.EventorCollections;

import java.io.Serializable;
import java.util.Set;
import java.util.concurrent.DelayQueue;
import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;

public final class FakeScheduler<D extends Serializable> implements Scheduler<D> {
    private final Set<Listener> listeners = EventorCollections.newSet();
    private DelayQueue<ActionDelayed> queue = new DelayQueue<ActionDelayed>();
    private long currentMilliseconds = 0;

    @Override
    public void doLater(D action, final long delay, TimeUnit unit) {
        queue.add(new ActionDelayed(action, delay, unit));
    }

    @Override
    public void addListener(Listener doer) {
        listeners.add(doer);
    }

    public void timePassed(long delay, TimeUnit unit) {
        currentMilliseconds += unit.toMillis(delay);
        ActionDelayed delayed;
        while ((delayed = queue.poll()) != null) {
            for (Listener each : listeners) {
                each.apply(delayed.action);
            }
        }
    }

    private class ActionDelayed implements Delayed {
        D action;
        long delay;
        TimeUnit delayUnit;

        ActionDelayed(D action, long delay, TimeUnit unit) {
            this.action = action;
            this.delay = delay;
            this.delayUnit = unit;
        }

        @Override
        public long getDelay(TimeUnit unit) {
            long delayInMilliseconds = TimeUnit.MILLISECONDS.convert(delay, delayUnit) - currentMilliseconds;
            return unit.convert(delayInMilliseconds, TimeUnit.MILLISECONDS);
        }

        @Override
        public int compareTo(Delayed o) {
            return (int) (getDelay(TimeUnit.MILLISECONDS) - o.getDelay(TimeUnit.MILLISECONDS));
        }
    }
}
