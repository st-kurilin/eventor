package com.shop.api.ecom;

public class OrderCanceled {
    public final String userEmail;
    public final String itemId;

    public OrderCanceled(String userEmail, String itemId) {
        this.userEmail = userEmail;
        this.itemId = itemId;
    }
}
