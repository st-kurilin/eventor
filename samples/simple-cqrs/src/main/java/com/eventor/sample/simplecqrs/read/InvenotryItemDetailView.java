package com.eventor.sample.simplecqrs.read;

import com.eventor.api.annotations.EventListener;
import com.eventor.sample.simplecqrs.event.*;

import java.util.UUID;

public class InvenotryItemDetailView {
    @EventListener
    public void on(InventoryItemCreated message) {
        BullShitDatabase.details.put(message.id, new InventoryItemDetailsDto(message.id, message.name, 0, 0));
    }

    @EventListener
    public void on(InventoryItemRenamed message) {
        InventoryItemDetailsDto d = getDetailsItem(message.inventoryItemId);
        d.name = message.newName;
        d.version = 0;//TODO:
    }

    private InventoryItemDetailsDto getDetailsItem(UUID id) {
        if (!BullShitDatabase.details.containsKey(id)) {
            throw new IllegalStateException("did not find the original inventory this shouldnt happen");
        }
        return BullShitDatabase.details.get(id);
    }

    @EventListener
    public void on(ItemsRemovedFromInventory message) {
        InventoryItemDetailsDto d = getDetailsItem(message.id);
        d.currentCount -= message.count;
        d.version = 0; //TODO:
    }

    @EventListener
    public void on(ItemsCheckedInToInventory message) {
        InventoryItemDetailsDto d = getDetailsItem(message.id);
        d.currentCount += message.count;
        d.version = 0;//TODO:
    }

    @EventListener
    public void on(InventoryItemDeactivated message) {
        BullShitDatabase.details.remove(message.inventoryItemId);
    }
}
