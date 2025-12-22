package com.example.order.dto.event;

import lombok.Data;

@Data
public class PaymentCompletedEvent {
    private Long orderId;
    private Long paymentId;
    private Integer amount;
}
