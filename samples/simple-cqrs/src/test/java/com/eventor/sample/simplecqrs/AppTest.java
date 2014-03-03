package com.eventor.sample.simplecqrs;

import com.eventor.api.CommandBus;
import com.eventor.guice.EventorModule;
import com.eventor.sample.simplecqrs.command.CheckInItemsToInventory;
import com.eventor.sample.simplecqrs.command.CreateInventoryItem;
import com.eventor.sample.simplecqrs.read.InventoryListView;
import com.eventor.sample.simplecqrs.read.ReadModelFacade;
import com.google.common.collect.ImmutableSet;
import com.google.inject.Guice;
import com.google.inject.Injector;
import org.junit.Ignore;
import org.junit.Test;

import java.util.UUID;

import static org.junit.Assert.assertEquals;

public class AppTest {
    @Test
    @Ignore //Aggregate creation by command is not implemented yet.
    public void testName() throws Exception {
        Injector injector = Guice.createInjector(
                new EventorModule(ImmutableSet.of(InventoryItem.class, InventoryListView.class))
        );
        CommandBus cb = injector.getInstance(CommandBus.class);
        UUID inventoryItemId = UUID.randomUUID();
        cb.submit(new CreateInventoryItem(inventoryItemId, "first"));
        cb.submit(new CheckInItemsToInventory(inventoryItemId, 10, 0));
        cb.submit(new CheckInItemsToInventory(inventoryItemId, 5, 0));


        ReadModelFacade read = injector.getInstance(ReadModelFacade.class);
        Thread.sleep(2000);
        assertEquals(15, read.getInventoryItemDetails(inventoryItemId).currentCount);
    }
}
