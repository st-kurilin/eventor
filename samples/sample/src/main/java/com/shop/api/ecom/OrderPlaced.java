package com.shop.api.ecom;

import com.shop.api.CreditCardInfo;
import com.shop.api.Money;
import com.shop.api.ShipmentInfo;

import java.util.Set;

public class OrderPlaced {
    public OrderPlaced(String orderId, String userEmail, Set<String> items, Money amount, CreditCardInfo cc, ShipmentInfo shipmentInfo) {
        this.orderId = orderId;
        this.userEmail = userEmail;
        this.items = items;
        this.amount = amount;
        this.cc = cc;
        this.shipmentInfo = shipmentInfo;
    }

    public final String orderId;
    public final String userEmail;
    public final Set<String> items;
    public final Money amount;
    public final CreditCardInfo cc;
    public final ShipmentInfo shipmentInfo;


}
