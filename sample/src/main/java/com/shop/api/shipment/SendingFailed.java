package com.shop.api.shipment;

public class SendingFailed {
    public final String trackingId;
    public final String message;

    public SendingFailed(String trackingId, String message) {
        this.trackingId = trackingId;
        this.message = message;
    }
}
