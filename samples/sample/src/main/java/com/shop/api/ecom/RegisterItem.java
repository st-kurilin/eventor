package com.shop.api.ecom;

public class RegisterItem {
    public final String catalog;
    public final String title;
    public final String price;

    public RegisterItem(String catalog, String title, String price) {
        this.catalog = catalog;
        this.title = title;
        this.price = price;
    }
}
