package com.example.payment.dto;

import com.example.payment.domain.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentResponseDto {

    private Long paymentId;
    private Long orderId;
    private Long userId;
    private Integer amount;
    private PaymentStatus status;

}
