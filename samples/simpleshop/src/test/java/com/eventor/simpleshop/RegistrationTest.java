package com.eventor.simpleshop;

import com.eventor.Fixture;
import com.eventor.simpleshop.api.registration.ConfirmEmail;
import com.eventor.simpleshop.api.registration.PersonRegistered;
import com.eventor.simpleshop.api.registration.RegisterRequest;
import org.junit.Test;

import java.util.concurrent.TimeUnit;

public class RegistrationTest {
    @Test
    public void testRegistration() throws Exception {
        new Fixture()
                .givenEvents()
                .whenCommands(new RegisterRequest("e@e.com", "bob", "1234"),
                        new ConfirmEmail("e@e.com", "e@e.com"))
                .then()
                .eventsContainsAnyOf(new PersonRegistered("e@e.com", "bob", "1234"));
    }

    @Test
    public void testRegistrationTimeout() throws Exception {
        new Fixture()
                .givenEvents()
                .whenCommands(new RegisterRequest("e@e.com", "bob", "1234"))
                .addTimePassed(30, TimeUnit.DAYS)
                .whenCommands(new ConfirmEmail("e@e.com", "e@e.com"))
                .then()
                .eventsDoesntContain(new PersonRegistered("e@e.com", "bob", "1234"));
    }
}
