package com.eventor.api;

import java.util.concurrent.TimeUnit;

public class Timeout {
    public final long duration;
    public final TimeUnit unit;
    public final Object timeoutEvent;

    public Timeout(long duration, TimeUnit unit, Object timeoutEvent) {
        this.duration = duration;
        this.unit = unit;
        this.timeoutEvent = timeoutEvent;
    }
}
