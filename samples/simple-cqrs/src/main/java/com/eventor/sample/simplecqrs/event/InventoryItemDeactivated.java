package com.eventor.sample.simplecqrs.event;

import java.util.UUID;

public class InventoryItemDeactivated {
    public final UUID inventoryItemId;

    public InventoryItemDeactivated(UUID inventoryItemId) {
        this.inventoryItemId = inventoryItemId;
    }
}
