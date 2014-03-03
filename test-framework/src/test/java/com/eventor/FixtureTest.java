package com.eventor;

import org.junit.Test;

import java.util.concurrent.TimeUnit;

import static com.eventor.Fixture.andNoMore;

public class FixtureTest {
    @Test
    public void testEmulatingWithDelay() throws Exception {
        new Fixture()
                .givenEvents(new EventA(), new EventA(), new EventB())
                .whenCommands(new CommandA(), new CommandB())
                .then()
                .eventsContainsAnyOf(new EventB());
    }

    @Test
    public void testEmulatingWithoutDelay() throws Exception {
        new Fixture()
                .givenEvents(new EventA(), new EventA(), new EventB())
                .whenCommands(new CommandA(), new CommandB())
                .addTimePassed(1, TimeUnit.HOURS)
                .then()
                .eventsDoesntContain(new EventB(), new EventB(), andNoMore());
    }

    private static class EventA {
    }

    private static class EventB {
    }

    private static class CommandA {
    }

    private static class CommandB {
    }
}
