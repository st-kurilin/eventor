package com.eventor.sample.simplecqrs.read;

import java.util.UUID;

public class InventoryItemListDto {
    public final UUID id;
    public String name;

    public InventoryItemListDto(UUID id, String name) {
        this.id = id;
        this.name = name;
    }
}
