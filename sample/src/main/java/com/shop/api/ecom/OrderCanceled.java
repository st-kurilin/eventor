package com.shop.api.ecom;

public class OrderCanceled {
    public final String orderId;

    public OrderCanceled(String orderId) {
        this.orderId = orderId;
    }
}
