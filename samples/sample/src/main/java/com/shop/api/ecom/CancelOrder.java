package com.shop.api.ecom;

public class CancelOrder {
    public String userEmail;
    public String itemId;

    public CancelOrder(String userEmail, String itemId) {
        this.userEmail = userEmail;
        this.itemId = itemId;
    }
}
