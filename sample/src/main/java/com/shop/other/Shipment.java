package com.shop.other;

import com.eventor.api.EventHandler;
import com.eventor.api.EventListener;
import com.shop.api.shipment.Send;

@EventListener
public class Shipment {
    @EventHandler
    public void ship(Send send) {
        System.out.println("Send");
    }
}
