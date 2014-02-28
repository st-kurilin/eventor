package com.eventor;

import org.junit.Test;

import java.util.concurrent.TimeUnit;

import static com.eventor.Feature.andNoMore;

public class FeatureTest {
    @Test
    public void testEmulatingWithDelay() throws Exception {
        new Feature()
                .givenEvents(new EventA(), new EventA(), new EventB())
                .whenCommands(new CommandA(), new CommandB())
                .thenEventsContainsAnyOf(new EventB());
    }

    @Test
    public void testEmulatingWithoutDelay() throws Exception {
        new Feature()
                .givenEvents(new EventA(), new EventA(), new EventB())
                .whenCommands(new CommandA(), new CommandB())
                .addTimePassed(1, TimeUnit.HOURS)
                .thenEventsContainsAllOf(new EventB(), new EventB(), andNoMore());
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
