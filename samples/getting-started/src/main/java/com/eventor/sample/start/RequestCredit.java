package com.eventor.sample.start;

public class RequestCredit {
    private final String trackingId;
    private final int amount;

    public RequestCredit(String trackingId, int amount) {
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
