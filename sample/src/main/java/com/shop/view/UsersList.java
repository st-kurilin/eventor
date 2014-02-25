package com.shop.view;

import com.eventor.api.annotations.EventListener;
import com.shop.api.registration.PersonRegistered;

import java.util.ArrayList;
import java.util.List;

@EventListener
public class UsersList {
    private final List<String> users = new ArrayList<String>();

    @EventListener
    public void on(PersonRegistered event) {
        users.add(event.name);
    }
}
