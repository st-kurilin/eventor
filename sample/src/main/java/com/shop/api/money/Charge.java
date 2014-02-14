package com.shop.api.money;

import com.shop.api.CreditCardInfo;
import com.shop.api.Money;


public class Charge {
    public final String transactionId;
    public final CreditCardInfo cc;
    public final Money amount;

    public Charge(String transactionId, CreditCardInfo cc, Money amount) {
        this.transactionId = transactionId;
        this.cc = cc;
        this.amount = amount;
    }
}
