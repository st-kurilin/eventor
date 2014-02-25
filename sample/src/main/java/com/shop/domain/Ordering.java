package com.shop.domain;

import com.eventor.api.IdGenerator;
import com.eventor.api.Timeout;
import com.eventor.api.annotations.*;
import com.shop.api.ecom.OrderCanceled;
import com.shop.api.ecom.OrderPlaced;
import com.shop.api.money.Charge;
import com.shop.api.money.Charged;
import com.shop.api.shipment.Send;

import java.io.Serializable;
import java.util.concurrent.TimeUnit;

@Saga
@Keys({@Key(field = "orderId", classes = {OrderPlaced.class, OrderCanceled.class}),
        @Key(field = "transactionId", classes = Charged.class)})
public class Ordering {
    OrderPlaced orderPlaced;

    @Start
    @EventHandler
    public Timeout on(@IdIn("orderId") OrderPlaced evn) {
        this.orderPlaced = evn;
        return new Timeout(2, TimeUnit.HOURS, new TimeToCharge());
    }

    public Charge charge(TimeToCharge charge) {
        return new Charge(IdGenerator.generate(), orderPlaced.cc, orderPlaced.amount);
    }

    public Send charged(Charged charged) {
        return new Send(IdGenerator.generate(), orderPlaced.shipmentInfo, orderPlaced.items);
    }

    private static class TimeToCharge implements Serializable {
    }
}
