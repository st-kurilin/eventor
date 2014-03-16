package com.eventor.sample.start;

public class CreditRequestDeclined {
    private final String trackingId;
    private final String reasonMessage;

    public CreditRequestDeclined(String trackingId, String reasonMessage) {
        this.trackingId = trackingId;
        this.reasonMessage = reasonMessage;
    }

    public String getTrackingId() {
        return trackingId;
    }

    public String getReasonMessage() {
        return reasonMessage;
    }
}
