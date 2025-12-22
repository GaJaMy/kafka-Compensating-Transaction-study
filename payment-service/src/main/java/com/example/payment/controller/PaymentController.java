package com.example.payment.controller;

import com.example.payment.dto.PaymentResponseDto;
import com.example.payment.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    /**
     * 주문 ID로 결제 정보를 조회합니다.
     * TODO: PaymentService.getPaymentByOrderId() 호출
     */
    @GetMapping("/order/{orderId}")
    public ResponseEntity<PaymentResponseDto> getPaymentByOrderId(@PathVariable Long orderId) {
        // TODO: 구현 필요
        throw new UnsupportedOperationException("Not implemented yet");
    }
}
