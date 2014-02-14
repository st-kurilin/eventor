package com.shop.api.shipment;

import com.shop.api.ShipmentInfo;

import java.util.Set;

public class Send {
    public final String shipmentId;
    public final ShipmentInfo shipmentInfo;
    public final Set<String> items;

    public Send(String shipmentId, ShipmentInfo shipmentInfo, Set<String> items) {
        this.shipmentId = shipmentId;
        this.shipmentInfo = shipmentInfo;
        this.items = items;
    }
}
