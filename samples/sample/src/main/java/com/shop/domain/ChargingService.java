package com.shop.domain;

import com.shop.api.CreditCardInfo;
import com.shop.api.Money;

public interface ChargingService {
    String charge(CreditCardInfo cc, Money amount);

    void cancel(String chargingTransactionId);

    void cancelWithCommision(String chargingTransactionId, double commision);
}
