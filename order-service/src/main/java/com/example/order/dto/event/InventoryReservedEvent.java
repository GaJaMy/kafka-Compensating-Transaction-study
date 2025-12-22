package com.example.order.dto.event;

import lombok.Data;

@Data
public class InventoryReservedEvent {
    private Long orderId;
    private Long productId;
    private Integer quantity;
}
