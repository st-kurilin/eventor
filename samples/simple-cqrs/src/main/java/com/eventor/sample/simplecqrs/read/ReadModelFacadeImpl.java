package com.eventor.sample.simplecqrs.read;

import java.util.UUID;

public class ReadModelFacadeImpl implements ReadModelFacade {
    public Iterable<InventoryItemListDto> getInventoryItems() {
        return BullShitDatabase.list;
    }

    public InventoryItemDetailsDto getInventoryItemDetails(UUID id) {
        return BullShitDatabase.details.get(id);
    }
}
