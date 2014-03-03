package com.eventor.sample.simplecqrs.read;

import java.util.UUID;

public class InventoryItemDetailsDto {
    public final UUID id;
    public String name;
    public int currentCount;
    public int version;

    public InventoryItemDetailsDto(UUID id, String name, int currentCount, int version) {
        this.id = id;
        this.name = name;
        this.currentCount = currentCount;
        this.version = version;
    }
}
