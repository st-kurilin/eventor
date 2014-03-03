package com.eventor.simpleshop.api.ecom;

public class OrderCanceled {
    public final String orderId;

    public OrderCanceled(String orderId) {
        this.orderId = orderId;
    }
}
