package com.example.order.controller;

import com.example.order.dto.OrderRequestDto;
import com.example.order.dto.OrderResponseDto;
import com.example.order.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    /**
     * 새로운 주문을 생성합니다.
     * TODO: 1. 요청 검증
     * TODO: 2. OrderService.createOrder() 호출
     * TODO: 3. 적절한 HTTP 응답 반환
     */
    @PostMapping
    public ResponseEntity<OrderResponseDto> createOrder(@RequestBody OrderRequestDto request) {
        // TODO: 구현 필요
        throw new UnsupportedOperationException("Not implemented yet");
    }

    /**
     * 주문 정보를 조회합니다.
     * TODO: 주문 조회 로직 구현
     */
    @GetMapping("/{orderId}")
    public ResponseEntity<OrderResponseDto> getOrder(@PathVariable Long orderId) {
        // TODO: 구현 필요
        throw new UnsupportedOperationException("Not implemented yet");
    }
}
