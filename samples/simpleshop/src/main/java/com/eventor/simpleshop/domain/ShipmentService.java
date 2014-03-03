package com.eventor.simpleshop.domain;

import com.eventor.simpleshop.api.ShipmentInfo;

import java.util.Set;

public interface ShipmentService {
    String send(ShipmentInfo shipmentInfo, Set<String> items);
}
