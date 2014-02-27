package com.eventor.api;

import scala.concurrent.duration.FiniteDuration;

public interface Scheduler {
    public void scheduleOnce(FiniteDuration duration, Object arg);

    public void cancel();
}
