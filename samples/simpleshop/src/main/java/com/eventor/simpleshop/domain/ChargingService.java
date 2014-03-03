package com.eventor.simpleshop.domain;

import com.eventor.simpleshop.api.CreditCardInfo;
import com.eventor.simpleshop.api.Money;

public interface ChargingService {
    String charge(CreditCardInfo cc, Money amount);

    void cancel(String chargingTransactionId);

    void cancelWithCommision(String chargingTransactionId, double commision);
}
