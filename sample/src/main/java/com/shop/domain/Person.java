package com.shop.domain;

import com.eventor.api.Aggregate;
import com.eventor.api.CommandHandler;
import com.eventor.api.EventHandler;
import com.eventor.api.IdGenerator;
import com.shop.api.Money;
import com.shop.api.ecom.CancelOrder;
import com.shop.api.ecom.OrderCanceled;
import com.shop.api.ecom.OrderPlaced;
import com.shop.api.ecom.PlaceOrder;
import com.shop.api.registration.PersonRegistered;

import java.util.Random;
import java.util.Set;

@Aggregate(initBy = PersonRegistered.class)
public class Person {

    @CommandHandler
    public OrderPlaced handle(PlaceOrder cmd) {
        return new OrderPlaced(IdGenerator.generate(),
                cmd.userEmail,
                cmd.itemId,
                calcAmount(cmd.itemId),
                cmd.cc,
                cmd.shipmentInfo);
    }

    @CommandHandler
    public OrderCanceled handle(CancelOrder cmd) {
        return new OrderCanceled(cmd.userEmail, cmd.itemId);
    }

    @EventHandler
    public void on(OrderPlaced event) {
    }

    @EventHandler
    public void on(OrderCanceled event) {
    }

    private Money calcAmount(Set<String> itemId) {
        return new Money(new Random().nextInt(3000), "USD");
    }

}
