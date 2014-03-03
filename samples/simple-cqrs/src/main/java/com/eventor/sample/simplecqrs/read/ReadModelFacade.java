package com.eventor.sample.simplecqrs.read;

import com.google.inject.ImplementedBy;

import java.util.UUID;

@ImplementedBy(ReadModelFacadeImpl.class)
public interface ReadModelFacade {
    Iterable<InventoryItemListDto> getInventoryItems();

    InventoryItemDetailsDto getInventoryItemDetails(UUID id);
}
