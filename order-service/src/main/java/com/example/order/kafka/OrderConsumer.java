package com.example.order.kafka;

import com.example.order.dto.event.InventoryReservedEvent;
import com.example.order.dto.event.PaymentCompletedEvent;
import com.example.order.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderConsumer {

    private final OrderService orderService;

    /**
     * inventory-reserved 토픽을 구독하여 재고 예약 완료 이벤트를 처리합니다.
     * TODO: 1. @KafkaListener 설정 (topic, groupId)
     * TODO: 2. 메시지 파싱
     * TODO: 3. OrderService.handleInventoryReserved() 호출
     */
    @KafkaListener(topics = "inventory-reserved", groupId = "order-service-group")
    public void consumeInventoryReservedEvent(InventoryReservedEvent event) {
        Long orderId = event.getOrderId();
        orderService.handleInventoryReserved(orderId);
    }

    /**
     * payment-completed 토픽을 구독하여 결제 완료 이벤트를 처리합니다.
     * TODO: 1. @KafkaListener 설정
     * TODO: 2. 메시지 파싱
     * TODO: 3. OrderService.handlePaymentCompleted() 호출
     */
    @KafkaListener(topics = "payment-completed", groupId = "order-service-group")
    public void consumePaymentCompletedEvent(PaymentCompletedEvent event) {
        Long orderId = event.getOrderId();
        orderService.handlePaymentCompleted(orderId);
    }
}
