package com.eventor.simpleshop.domain;

import com.eventor.api.Timeout;
import com.eventor.api.annotations.*;
import com.eventor.simpleshop.api.registration.ConfirmEmail;
import com.eventor.simpleshop.api.registration.PersonRegistered;
import com.eventor.simpleshop.api.registration.RegisterRequest;

import java.util.concurrent.TimeUnit;

//Registration Saga represents Registration with email confirmation process.
@Saga
public class Registration {
    private String email;
    private String name;
    private String password;

    //Start registration process
    @Start
    @CommandHandler
    public Timeout handle(@IdIn("email") RegisterRequest cmd) {
        email = cmd.email;
        name = cmd.name;
        password = cmd.password;
        sendEmailWithToken(email, generateToken(email));
        //hadntConfirmedForLongTime will be called in two weeks
        return new Timeout(14, TimeUnit.DAYS, new RegistrationTimeout());
    }

    //Try to confirm email
    @CommandHandler
    public Object handle(@IdIn("email") ConfirmEmail cmd) {
        if (validToken(cmd.email, cmd.token)) {
            return new Object[]{
                    new PersonRegistered(email, name, password),    //Generate Domain Event
                    Finish.RESULT};                                 //Finish Registration Saga
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
