package com.shop.domain;

import com.eventor.api.IdGenerator;
import com.eventor.api.annotations.*;
import com.shop.api.Money;
import com.shop.api.ecom.CancelOrder;
import com.shop.api.ecom.OrderCanceled;
import com.shop.api.ecom.OrderPlaced;
import com.shop.api.ecom.PlaceOrder;
import com.shop.api.registration.PersonRegistered;

import java.util.Random;
import java.util.Set;

@Aggregate
public class Person {
    @Id
    private String email;

    @Start
    @EventListener
    public void on(PersonRegistered evt) {
        email = evt.email;
    }

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
        return new OrderCanceled(cmd.userEmail);
    }

    private Money calcAmount(Set<String> itemId) {
        return new Money(new Random().nextInt(3000), "USD");
    }
}
