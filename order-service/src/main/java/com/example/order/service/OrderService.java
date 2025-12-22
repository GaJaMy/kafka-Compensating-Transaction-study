package com.example.order.service;

import com.example.order.domain.Order;
import com.example.order.domain.OrderStatus;
import com.example.order.dto.OrderRequestDto;
import com.example.order.dto.OrderResponseDto;
import com.example.order.kafka.OrderProducer;
import com.example.order.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderProducer orderProducer;
    private final OrderRepository orderRepository;

    /**
     * 주문을 생성하고 Kafka에 order-created 이벤트를 발행합니다.
     * TODO: 1. OrderRequestDto → Order Entity 변환
     * TODO: 2. DB에 저장 (OrderRepository.save)
     * TODO: 3. OrderProducer를 통해 Kafka에 order-created 이벤트 발행
     * TODO: 4. Order Entity → OrderResponseDto 변환 후 반환
     */
    @Transactional
    public OrderResponseDto createOrder(OrderRequestDto request) {
        // TODO: 구현 필요
        Order order = Order.builder()
                .productId(request.getProductId())
                .quantity(request.getQuantity())
                .userId(request.getUserId())
                .build();

        orderRepository.save(order);

        orderProducer.sendOrderCreatedEvent(order);

        return OrderResponseDto.builder()
                .orderId(order.getOrderId())
                .productId(order.getProductId())
                .quantity(order.getQuantity())
                .userId(order.getUserId())
                .status(OrderStatus.CREATED)
                .build();
    }

    /**
     * 주문 정보를 조회합니다.
     * TODO: 1. OrderRepository.findById로 조회
     * TODO: 2. Order Entity → OrderResponseDto 변환 후 반환
     */
    public OrderResponseDto getOrder(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("주문 정보가 업습니다."));

        return OrderResponseDto.builder()
                .orderId(order.getOrderId())
                .productId(order.getProductId())
                .quantity(order.getQuantity())
                .userId(order.getUserId())
                .status(order.getStatus())
                .build();
    }

    /**
     * 재고 예약 완료 이벤트를 처리합니다.
     * TODO: 1. OrderRepository.findById로 주문 조회
     * TODO: 2. Order 상태를 INVENTORY_RESERVED로 업데이트
     * TODO: 3. DB에 저장
     */
    @Transactional
    public void handleInventoryReserved(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("주문 정보가 없습니다."));

        order.setStatus(OrderStatus.INVENTORY_RESERVED);
    }

    /**
     * 결제 완료 이벤트를 처리합니다.
     * TODO: 1. OrderRepository.findById로 주문 조회
     * TODO: 2. Order 상태를 COMPLETED로 업데이트
     * TODO: 3. DB에 저장
     */
    @Transactional
    public void handlePaymentCompleted(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("주문 정보가 없습니다."));

        order.setStatus(OrderStatus.COMPLETED);
    }

    /**
     * 주문 실패를 처리합니다 (보상 트랜잭션).
     * TODO: 1. OrderRepository.findById로 주문 조회
     * TODO: 2. Order 상태를 FAILED로 업데이트
     * TODO: 3. DB에 저장
     */
    @Transactional
    public void handleOrderFailed(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException(""));

        order.setStatus(OrderStatus.FAILED);
    }
}
