package com.eventor.sample.start;

import com.eventor.EventsApplier;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class CreditDecisionStatTest {
    @Test
    public void testAcceptedAllForZeroTotal() throws Exception {
        CreditDecisionStat stat = new CreditDecisionStat();

        assertEquals(100.0, stat.getAcceptanceRate(), 0.1);
    }

    @Test
    public void testAcceptedRate() throws Exception {
        CreditDecisionStat stat = new CreditDecisionStat();

        EventsApplier.apply(stat,
                new CreditRequestAccepted("U1", 200),
                new CreditRequestAccepted("M2", 200),
                new CreditRequestDeclined("U3", "Declined"),
                new CreditRequestAccepted("U6", 200)
        );

        assertEquals(75.0, stat.getAcceptanceRate(), 0.1);
    }
}
