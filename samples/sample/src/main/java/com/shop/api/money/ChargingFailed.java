package com.shop.api.money;

public class ChargingFailed {
    public final String transactionId;
    public final String message;

    public ChargingFailed(String transactionId, String message) {
        this.transactionId = transactionId;
        this.message = message;
    }
}
