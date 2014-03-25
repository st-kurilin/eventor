package com.eventor;

import com.eventor.api.Listener;
import org.junit.Test;

import java.util.concurrent.TimeUnit;

import static org.mockito.Mockito.*;

public class FakeSchedulerTest {
    @Test
    public void testNoActionsIfTimeDidntPass() throws Exception {
        Listener mock = mock(Listener.class);
        FakeScheduler<String> scheduler = new FakeScheduler<String>();
        scheduler.addListener(mock);
        scheduler.doLater("action", 10, TimeUnit.DAYS);
        scheduler.timePassed(9, TimeUnit.DAYS);
        verifyNoMoreInteractions(mock);
    }

    @Test
    public void testActionsIfTimePassed() throws Exception {
        Listener mock = mock(Listener.class);

        FakeScheduler<String> scheduler = new FakeScheduler<String>();
        scheduler.addListener(mock);
        scheduler.doLater("action", 10, TimeUnit.DAYS);
        scheduler.timePassed(11, TimeUnit.DAYS);
        verify(mock).apply("action");
    }

}
