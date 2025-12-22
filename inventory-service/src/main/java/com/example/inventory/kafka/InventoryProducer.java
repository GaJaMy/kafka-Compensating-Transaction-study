package com.example.inventory.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class InventoryProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    /**
     * inventory-reserved 토픽에 재고 예약 완료 이벤트를 발행합니다.
     * TODO: kafkaTemplate.send() 사용
     */
    public void sendInventoryReservedEvent(Long orderId, Long productId, Integer quantity) {
        // TODO: 구현 필요
        // Topic name: "inventory-reserved"
        throw new UnsupportedOperationException("Not implemented yet");
    }

    /**
     * order-failed 토픽에 주문 실패 이벤트를 발행합니다.
     * TODO: 재고 부족 등의 이유로 주문 실패 시 사용
     */
    public void sendOrderFailedEvent(Long orderId, String reason) {
        // TODO: 구현 필요
        // Topic name: "order-failed"
        throw new UnsupportedOperationException("Not implemented yet");
    }
}
