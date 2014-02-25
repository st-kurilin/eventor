package com.shop.domain;

import com.eventor.api.IdGenerator;
import com.eventor.api.annotations.Aggregate;
import com.eventor.api.annotations.CommandHandler;
import com.eventor.api.annotations.EventHandler;
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
                cmd.itemIds,
                calcAmount(cmd.itemIds),
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
