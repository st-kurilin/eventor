package com.eventor.sample.simplecqrs.read;

import com.eventor.api.annotations.EventListener;
import com.eventor.sample.simplecqrs.event.InventoryItemCreated;
import com.eventor.sample.simplecqrs.event.InventoryItemDeactivated;
import com.eventor.sample.simplecqrs.event.InventoryItemRenamed;

import java.util.Iterator;

@EventListener
public class InventoryListView {
    @EventListener
    public void on(InventoryItemCreated message) {
        BullShitDatabase.list.add(new InventoryItemListDto(message.id, message.name));
    }

    @EventListener
    public void on(InventoryItemRenamed message) {
        for (InventoryItemListDto each : BullShitDatabase.list) {
            if (each.id.equals(message.inventoryItemId)) {
                each.name = message.newName;
            }
        }
    }

    @EventListener
    public void on(InventoryItemDeactivated message) {
        Iterator<InventoryItemListDto> it = BullShitDatabase.list.iterator();
        while (it.hasNext()) {
            InventoryItemListDto each = it.next();
            if (each.id.equals(message.inventoryItemId)) {
                it.remove();
            }
        }
    }
}
