package com.shop.view;

import com.eventor.api.annotations.EventListener;
import com.shop.api.registration.PersonRegistered;

import javax.inject.Singleton;
import java.util.ArrayList;
import java.util.List;

@EventListener
@Singleton
public class UsersList {
    private final List<String> users = new ArrayList<String>();

    @EventListener
    public void on(PersonRegistered event) {
        users.add(event.name);
    }

    public List<String> getUsers() {
        return users;
    }
}
