package com.shop;

import com.eventor.Fixture;
import com.shop.api.registration.ConfirmEmail;
import com.shop.api.registration.PersonRegistered;
import com.shop.api.registration.RegisterRequest;
import org.junit.Test;

import java.util.concurrent.TimeUnit;

public class RegistrationTest {
    @Test
    public void testRegistration() throws Exception {
        new Fixture()
                .givenEvents()
                .whenCommands(new RegisterRequest("e@e.com", "bob", "1234"),
                        new ConfirmEmail("e@e.com", "e@e.com"))
                .thenEventsContainsAnyOf(new PersonRegistered("e@e.com", "bob", "1234"));
    }

    @Test
    public void testRegistrationTimeout() throws Exception {
        new Fixture()
                .givenEvents()
                .whenCommands(new RegisterRequest("e@e.com", "bob", "1234"))
                .addTimePassed(30, TimeUnit.DAYS)
                .thenEventsDoesntContain(new PersonRegistered("e@e.com", "bob", "1234"));
    }
}
