package com.rohankumar.easylodge.mappers.inventory;

import com.rohankumar.easylodge.dtos.inventory.InventoryResponse;
import com.rohankumar.easylodge.entities.inventory.Inventory;

public class InventoryMapper {

    public static InventoryResponse toResponse(Inventory inventory) {

        return InventoryResponse.builder()
                .id(inventory.getId())
                .inventoryDate(inventory.getInventoryDate())
                .bookedCount(inventory.getBookedCount())
                .reservedCount(inventory.getReservedCount())
                .totalRoomsCount(inventory.getTotalRoomsCount())
                .surgeFactor(inventory.getSurgeFactor())
                .price(inventory.getPrice())
                .closed(inventory.getClosed())
                .createdAt(inventory.getCreatedAt())
                .updatedAt(inventory.getUpdatedAt())
                .build();
    }
}
