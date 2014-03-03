package com.eventor.simpleshop.domain;

import com.eventor.api.annotations.*;
import com.eventor.simpleshop.api.ecom.CatalogRegistered;
import com.eventor.simpleshop.api.ecom.ItemRegistered;
import com.eventor.simpleshop.api.ecom.RegisterItem;

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
