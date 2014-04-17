package com.eventor;

import com.eventor.api.annotations.EventListener;

import java.util.Set;

import static com.eventor.internal.EventorCollections.newSet;

@EventListener
public class EventsRecorder {
    private Set<Object> evetns = newSet();

    @EventListener
    public void on(Object evt) {
        evetns.add(evt);
    }

    public Set<Object> getAllEvetns() {
        return evetns;
    }
}
