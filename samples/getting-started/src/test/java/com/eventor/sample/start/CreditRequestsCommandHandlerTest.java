package com.eventor.sample.start;

import com.eventor.Fixture;
import org.junit.Test;

public class CreditRequestsCommandHandlerTest {
    @Test
    public void testDecline() throws Exception {
        new Fixture(CreditRequestsCommandHandler.class)
                .givenEvents()
                .whenCommands(new RequestCredit("U1", 10))
                .then()
                .eventsContainsAllOf(CreditRequestDeclined.class);
    }

    @Test
    public void testAccept() throws Exception {
        new Fixture(CreditRequestsCommandHandler.class)
                .givenEvents()
                .whenCommands(new RequestCredit("U1", 400))
                .then()
                .eventsContainsAllOf(new CreditRequestAccepted("U1", 400));
    }
}
