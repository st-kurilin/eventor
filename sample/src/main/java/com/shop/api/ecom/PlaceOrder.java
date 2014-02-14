package com.shop.api.ecom;

import com.shop.api.CreditCardInfo;
import com.shop.api.ShipmentInfo;

import java.util.Set;

public class PlaceOrder {
    public final String userEmail;
    public final Set<String> itemId;
    public final CreditCardInfo cc;
    public final ShipmentInfo shipmentInfo;

    public PlaceOrder(String userEmail, Set<String> itemId, CreditCardInfo cc, ShipmentInfo shipmentInfo) {
        this.userEmail = userEmail;
        this.itemId = itemId;
        this.cc = cc;
        this.shipmentInfo = shipmentInfo;
    }
}
