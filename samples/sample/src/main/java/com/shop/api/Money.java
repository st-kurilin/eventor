package com.shop.api;

public class Money {
    public final long amountInCents;
    public final String currency;

    public Money(long amountInCents, String currency) {
        this.amountInCents = amountInCents;
        this.currency = currency;
    }
}
