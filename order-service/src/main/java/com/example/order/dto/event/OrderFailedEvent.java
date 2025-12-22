package com.example.order.dto.event;

import lombok.Data;

@Data
public class OrderFailedEvent {
    private Long orderId;
    private String reason;
}
