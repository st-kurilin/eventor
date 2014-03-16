package com.eventor.sample.start;

public class CreditRequestAccepted {
    private final String trackingId;
    private final int amount;

    public CreditRequestAccepted(String trackingId, int amount) {
        this.trackingId = trackingId;
        this.amount = amount;
    }

    public String getTrackingId() {
        return trackingId;
    }

    public int getAmount() {
        return amount;
    }
}
