package com.example.payment.dto.event;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class OrderFailedEvent {
    private Long orderId;
    private String reason;
}
