package com.example.payment.dto.event;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PaymentCompleteEvent {
    private Long orderId;
    private Long paymentId;
}
