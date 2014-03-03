package com.eventor.sample.simplecqrs;

import com.eventor.api.annotations.*;
import com.eventor.sample.simplecqrs.command.*;
import com.eventor.sample.simplecqrs.event.*;

import java.util.UUID;

@Aggregate
public class InventoryItem {
    private boolean activated;
    @Id
    private UUID id;

    @EventListener
    private void apply(InventoryItemCreated e) {
        id = e.id;
        activated = true;
    }

    @EventListener
    private void apply(InventoryItemDeactivated e) {
        activated = false;
    }

    @CommandHandler
    public Object changeName(@IdIn("inventoryItemId") RenameInventoryItem cmd) {
        if (cmd.newName.isEmpty()) throw new IllegalArgumentException("newName");
        return new InventoryItemRenamed(id, cmd.newName);
    }

    @CommandHandler
    public Object remove(@IdIn("inventoryItemId") RemoveItemsFromInventory cmd) {
        if (cmd.count <= 0) throw new IllegalStateException("cant remove negative count from inventory");
        return new ItemsRemovedFromInventory(id, cmd.count);
    }

    @CommandHandler
    public Object checkIn(@IdIn("inventoryItemId") CheckInItemsToInventory cmd) {
        if (cmd.count <= 0) throw new IllegalStateException("must have a count greater than 0 to add to inventory");
        return new ItemsCheckedInToInventory(id, cmd.count);
    }

    @CommandHandler
    public Object deactivate(@IdIn("inventoryItemId") DeactivateInventoryItem cmd) {
        if (!activated) throw new IllegalStateException("already deactivated");
        return new InventoryItemDeactivated(id);
    }

    @Start
    @CommandHandler
    public Object create(CreateInventoryItem cmd) {
        return new InventoryItemCreated(cmd.inventoryItemId, cmd.name);
    }
}
