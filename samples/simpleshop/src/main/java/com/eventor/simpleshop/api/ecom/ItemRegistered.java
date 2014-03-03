package com.eventor.simpleshop.api.ecom;

public class ItemRegistered {
    public final String catalog;
    public final String title;
    public final String price;

    public ItemRegistered(String catalog, String title, String price) {
        this.catalog = catalog;
        this.title = title;
        this.price = price;
    }
}
