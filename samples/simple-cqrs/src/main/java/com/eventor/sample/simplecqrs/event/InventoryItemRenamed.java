package com.eventor.sample.simplecqrs.event;

import java.util.UUID;

public class InventoryItemRenamed {
    public final UUID inventoryItemId;
    public final String newName;

    public InventoryItemRenamed(UUID inventoryItemId, String newName) {
        this.inventoryItemId = inventoryItemId;
        this.newName = newName;
    }
}
