package com.example.order.kafka;

import com.example.order.domain.Order;
import com.example.order.dto.event.OrderCreatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    /**
     * order-created 토픽에 주문 생성 이벤트를 발행합니다.
     * TODO: 1. kafkaTemplate.send()를 사용하여 메시지 발행
     * TODO: 2. 로깅 추가
     * TODO: 3. 에러 핸들링
     */
    public void sendOrderCreatedEvent(Order order) {
        // Topic name: "order-created"
        OrderCreatedEvent event = OrderCreatedEvent.builder()
                .orderId(order.getOrderId())
                .productId(order.getProductId())
                .userId(order.getUserId())
                .quantity(order.getQuantity())
                .build();

        kafkaTemplate.send("order-created", event);
    }
}
