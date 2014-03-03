package com.shop.api.ecom;

import com.shop.api.CreditCardInfo;
import com.shop.api.ShipmentInfo;

import java.util.Set;

public class PlaceOrder {
    public final String userEmail;
    public final Set<String> itemIds;
    public final CreditCardInfo cc;
    public final ShipmentInfo shipmentInfo;

    public PlaceOrder(String userEmail, Set<String> itemIds, CreditCardInfo cc, ShipmentInfo shipmentInfo) {
        this.userEmail = userEmail;
        this.itemIds = itemIds;
        this.cc = cc;
        this.shipmentInfo = shipmentInfo;
    }
}
