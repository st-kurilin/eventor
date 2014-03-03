package com.eventor.sample.simplecqrs.read;

import java.util.*;

public class BullShitDatabase {
    public static Map<UUID, InventoryItemDetailsDto> details = new HashMap<UUID, InventoryItemDetailsDto>();
    public static List<InventoryItemListDto> list = new ArrayList<InventoryItemListDto>();
}
