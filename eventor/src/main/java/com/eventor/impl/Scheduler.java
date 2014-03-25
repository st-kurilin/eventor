package com.eventor.impl;

import com.eventor.api.Listener;

import java.io.Serializable;
import java.util.concurrent.TimeUnit;

public interface Scheduler<D extends Serializable> {
    void doLater(D action, long delay, TimeUnit unit);

    void addListener(Listener doer);
}
