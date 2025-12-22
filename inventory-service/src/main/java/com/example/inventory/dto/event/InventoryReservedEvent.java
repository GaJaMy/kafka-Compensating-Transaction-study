package com.example.inventory.dto.event;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class InventoryReservedEvent {
    private Long orderId;
    private Long productId;
    private Integer quantity;
}
