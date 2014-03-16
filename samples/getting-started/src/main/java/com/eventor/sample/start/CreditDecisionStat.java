package com.eventor.sample.start;

import com.eventor.api.annotations.EventListener;

@EventListener
public class CreditDecisionStat {
    private int total;
    private int accepted;

    public double getAcceptanceRate() {
        if (total == 0) {
            return 100;
        }
        return 100. * accepted / total;
    }

    @EventListener
    public void on(CreditRequestAccepted e) {
        total++;
        accepted++;
    }

    @EventListener
    public void on(CreditRequestDeclined e) {
        total++;
    }
}
