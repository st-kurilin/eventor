package com.shop.domain;

import com.eventor.api.annotations.*;
import com.shop.api.ecom.CatalogRegistered;
import com.shop.api.ecom.ItemRegistered;
import com.shop.api.ecom.RegisterItem;

@Aggregate
public class Catalog {
    @Id
    private String id;

    @Start
    @CommandHandler
    public Object[] handle(RegisterItem cmd) {
        return new Object[]{new CatalogRegistered(cmd.catalog),
                new ItemRegistered(cmd.catalog, cmd.title, cmd.price)};
    }

    @EventListener
    public void on(CatalogRegistered e) {
        id = e.title;
    }

    @EventListener
    public void on(ItemRegistered e) {
    }
}
