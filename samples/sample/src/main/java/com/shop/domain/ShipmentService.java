package com.shop.domain;

import com.shop.api.ShipmentInfo;

import java.util.Set;

public interface ShipmentService {
    String send(ShipmentInfo shipmentInfo, Set<String> items);
}
