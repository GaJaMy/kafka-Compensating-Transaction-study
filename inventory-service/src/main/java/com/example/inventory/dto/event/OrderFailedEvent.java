package com.example.inventory.dto.event;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class OrderFailedEvent {
    private Long orderId;
    private Long productId;
    private Integer quantity;
    private String reason;
}
