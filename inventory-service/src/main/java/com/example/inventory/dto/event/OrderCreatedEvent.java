package com.example.inventory.dto.event;

import lombok.Data;

@Data
public class OrderCreatedEvent {
    private Long orderId;
    private Long userId;
    private Long productId;
    private Integer quantity;
}
