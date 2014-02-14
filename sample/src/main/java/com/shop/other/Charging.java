package com.shop.other;

import com.eventor.api.EventBus;
import com.eventor.api.EventHandler;
import com.eventor.api.EventListener;
import com.shop.api.money.Charge;
import com.shop.api.money.Charged;
import com.shop.api.money.ChargingFailed;

@EventListener
public class Charging {
    private final EventBus eventBus;

    public Charging(EventBus eventBus) {
        this.eventBus = eventBus;
    }

    @EventHandler
    public void ship(Charge evn) {
        if (evn.amount.amountInCents > 100) {
            eventBus.publish(new Charged(evn.transactionId));
        } else {
            eventBus.publish(new ChargingFailed(evn.transactionId, "Failed"));
        }
    }
}
