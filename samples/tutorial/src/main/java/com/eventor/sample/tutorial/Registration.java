package com.eventor.sample.tutorial;

import com.eventor.api.Timeout;
import com.eventor.api.annotations.*;

import java.util.concurrent.TimeUnit;

//Registration Saga represents Registration with email confirmation process.
@Saga
public class Registration {
    private SecurityService securityService;
    private String email;
    private String name;
    private String protectedPassword;

    public Registration(SecurityService securityService) {
        this.securityService = securityService;
    }

    //Start registration process
    @Start
    @CommandHandler
    public Timeout handle(@IdIn("email") RequestRegistration cmd) {
        email = cmd.email;
        name = cmd.name;
        protectedPassword = securityService.protectPassword(email, cmd.password);
        sendEmailWithToken(email, securityService.generateRegistrationToken(email));
        //hadntConfirmedForLongTime will be called in two weeks
        return new Timeout(14, TimeUnit.DAYS, new RegistrationTimeout());
    }

    //Try to confirm email
    @CommandHandler
    public Object handle(@IdIn("email") ConfirmEmail cmd) {
        if (securityService.checkRegistrationToken(cmd.email, cmd.token)) {
            return new Object[]{
                    //TODO: should generate command instead of event
                    new PersonRegistered(email, name, protectedPassword),    //Generate Domain Event
                    Finish.RESULT};                                          //Finish Registration Saga
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
}
