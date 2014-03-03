package com.eventor.simpleshop.domain;

import com.eventor.api.Timeout;
import com.eventor.api.annotations.*;
import com.eventor.simpleshop.api.ecom.OrderCanceled;
import com.eventor.simpleshop.api.ecom.OrderPlaced;
import com.eventor.simpleshop.api.money.Charged;
import com.eventor.simpleshop.api.money.ChargingFailed;
import com.eventor.simpleshop.api.shipment.SendingFailed;
import com.eventor.simpleshop.api.shipment.Sent;

import java.io.Serializable;
import java.util.concurrent.TimeUnit;

@Saga
public class Ordering {

    private final ShipmentService shipmentService;
    private final ChargingService chargingService;

    @Id
    @ComesFrom({OrderPlaced.class, OrderCanceled.class})
    private String orderId;

    @Id
    @ComesFrom({Charged.class, ChargingFailed.class})
    private String chargingTransactionId;

    @Id
    @ComesFrom({Sent.class, SendingFailed.class})
    private String trackingId;

    private State state;
    private OrderPlaced orderPlaced;

    public Ordering(ShipmentService shipmentService, ChargingService chargingService) {
        this.shipmentService = shipmentService;
        this.chargingService = chargingService;
    }

    @Start
    @EventListener
    public Timeout on(@IdIn("orderId") OrderPlaced evn) {
        state = State.INIT_WAIT;
        this.orderPlaced = evn;
        return new Timeout(2, TimeUnit.HOURS, new TimeToCharge());
    }

    @OnTimeout(TimeToCharge.class)
    public void charge() {
        chargingTransactionId = chargingService.charge(orderPlaced.cc, orderPlaced.amount);
        state = State.CHARGING;
    }

    @EventListener
    public void charged(Charged charged) {
        trackingId = shipmentService.send(orderPlaced.shipmentInfo, orderPlaced.items);
        state = State.SHIPPING;
    }

    @Finish
    @EventListener
    public void on(@IdIn("trackingId") Sent evn) {
    }

    @Finish
    @EventListener
    public void on(@IdIn("orderId") OrderCanceled evn) {
        switch (state) {
            case INIT_WAIT:
                return;
            case CHARGING:
                chargingService.cancel(chargingTransactionId);
                return;
            case SHIPPING:
                chargingService.cancelWithCommision(chargingTransactionId, 0.1);
                return;
            default:
                throw new AssertionError(state);
        }
    }

    @Finish
    @EventListener
    public void on(@IdIn("transactionId") ChargingFailed evn) {
    }

    @Finish
    @EventListener
    public void on(@IdIn("trackingId") SendingFailed evn) {
        chargingService.cancel(chargingTransactionId);
    }

    private enum State {
        INIT_WAIT, CHARGING, SHIPPING
    }

    private static class TimeToCharge implements Serializable {
    }
}
