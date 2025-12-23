package com.example.payment.kafka;

// import com.example.payment.dto.event.OrderFailedEvent;  // TODO: Create this DTO
import com.example.payment.dto.event.InventoryReservedEvent;
import com.example.payment.service.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentConsumer {

    private final PaymentService paymentService;

    /**
     * inventory-reserved 토픽을 구독하여 재고 예약 완료 이벤트를 처리합니다.
     * TODO: 1. @KafkaListener 설정
     * TODO: 2. 메시지 파싱
     * TODO: 3. PaymentService.processPayment() 호출
     */
    @KafkaListener(topics = "inventory-reserved", groupId = "payment-service-group")
    public void consumeInventoryReservedEvent(InventoryReservedEvent event) {
        paymentService.processPayment(event.getOrderId(), event.getUserId(), event.getAmount());
    }
}
