package com.shop.domain;

import com.eventor.api.CommandBus;
import com.eventor.api.EventBus;
import com.eventor.api.Timeout;
import com.eventor.api.annotations.*;
import com.shop.api.registration.AskToJoin;
import com.shop.api.registration.PersonRegistered;
import com.shop.api.registration.VerifyEmail;

import java.util.concurrent.TimeUnit;

@Saga
@Keys(@Key(field = "email", classes = {AskToJoin.class, VerifyEmail.class}))
public class Registration {
    private final CommandBus commandBus;
    private final EventBus eventBus;
    private AskToJoin askToJoin;

    public Registration(CommandBus commandBus, EventBus eventBus) {
        this.commandBus = commandBus;
        this.eventBus = eventBus;
    }

    @CommandHandler
    @Start
    public Timeout handle(AskToJoin cmd) {
        sendEmail(cmd.email);
        return new Timeout(2, TimeUnit.HOURS, new RegistrationTimeout());
    }

    @CommandHandler
    public Object handle(VerifyEmail cmd) {
        if (valid(cmd.email, cmd.token)) {
            eventBus.publish(new PersonRegistered(askToJoin.email, askToJoin.name, askToJoin.password));
            return Finish.RESULT;
        }
        return null;
    }

    private boolean valid(String email, String token) {
        return true;
    }


    private void sendEmail(String email) {

    }

    private static class RegistrationTimeout {
    }
}
