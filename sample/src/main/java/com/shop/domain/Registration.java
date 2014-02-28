package com.shop.domain;

import com.eventor.api.CommandBus;
import com.eventor.api.EventBus;
import com.eventor.api.Timeout;
import com.eventor.api.annotations.*;
import com.shop.api.registration.ConfirmEmail;
import com.shop.api.registration.PersonRegistered;
import com.shop.api.registration.RegisterRequest;

import java.util.concurrent.TimeUnit;

//Registration Saga represents Registration with email confirmation process.
@Saga
public class Registration {
    private final CommandBus commandBus;
    private final EventBus eventBus;

    private String email;
    private String name;
    private String password;
    private RegisterRequest registerRequest;

    public Registration(CommandBus commandBus, EventBus eventBus) {
        this.commandBus = commandBus;
        this.eventBus = eventBus;
    }

    //Start registration process
    @Start
    @CommandHandler
    public Timeout handle(@IdIn("email") RegisterRequest cmd) {
        email = cmd.email;
        name = cmd.name;
        password = cmd.password;
        sendEmailWithToken(email, generateToken(email));
        return new Timeout(14, TimeUnit.DAYS, new RegistrationTimeout());
    }

    //Try to confirm email
    @CommandHandler
    public Object handle(@IdIn("email") ConfirmEmail cmd) {
        if (validToken(cmd.email, cmd.token)) {
            eventBus.publish(new PersonRegistered(email, name, password)); //Generate Domain Event
            return Finish.RESULT;   //Finish Registration Saga
        }
        return null;
    }

    //Email hadn't confirmed for long time
    @OnTimeout(RegistrationTimeout.class)
    public Object hadntConfirmedForLongTime() {
        return Finish.RESULT;   //Finish Registration Saga
    }

    private static class RegistrationTimeout {
    }

    //Fake email sender
    private void sendEmailWithToken(String email, String token) {
        System.out.println(String.format("Email with token [%s] send to [%s]", token, email));
    }

    //Dump security impl
    private boolean validToken(String email, String token) {
        return email.equals(token);
    }

    private String generateToken(String email) {
        return email;
    }
}
