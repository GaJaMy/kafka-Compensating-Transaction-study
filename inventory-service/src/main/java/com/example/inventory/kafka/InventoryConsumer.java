package com.example.inventory.kafka;

import com.example.inventory.dto.event.OrderCreatedEvent;
import com.example.inventory.dto.event.OrderFailedEvent;
import com.example.inventory.service.InventoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class InventoryConsumer {

    private final InventoryService inventoryService;

    /**
     * order-created 토픽을 구독하여 주문 생성 이벤트를 처리합니다.
     * TODO: 1. @KafkaListener 설정
     * TODO: 2. 메시지 파싱 (orderId, productId, quantity)
     * TODO: 3. InventoryService.reserveInventory() 호출
     */
    @KafkaListener(topics = "order-created", groupId = "inventory-service-group")
    public void consumeOrderCreatedEvent(OrderCreatedEvent event) {
        inventoryService.reserveInventory(
                event.getOrderId(),
                event.getProductId(),
                event.getQuantity()
        );
    }

    /**
     * order-failed 토픽을 구독하여 주문 실패 이벤트를 처리합니다 (보상 트랜잭션).
     * TODO: 1. @KafkaListener 설정
     * TODO: 2. InventoryService.rollbackInventory() 호출
     * TODO: 3. 실패 사유 로깅
     */
    @KafkaListener(topics = "order-failed", groupId = "inventory-service-group")
    public void consumeOrderFailedEvent(OrderFailedEvent event) {
        inventoryService.rollbackInventory(
                event.getOrderId(),
                event.getProductId(),
                event.getQuantity()
        );
    }
}
