package com.example.order.dto.event;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class OrderCreatedEvent {
    private Long orderId;
    private Long userId;
    private Long productId;
    private Integer quantity;
}
