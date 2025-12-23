package com.example.payment.dto.event;

import lombok.Data;

@Data
public class InventoryReservedEvent {
    private Long orderId;
    private Long userId;
    private Integer amount;
}
