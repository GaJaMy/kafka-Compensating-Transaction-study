package com.example.payment.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    /**
     * payment-completed 토픽에 결제 완료 이벤트를 발행합니다.
     * TODO: kafkaTemplate.send() 사용
     */
    public void sendPaymentCompletedEvent(Long orderId, Long paymentId) {
        // TODO: 구현 필요
        // Topic name: "payment-completed"
        throw new UnsupportedOperationException("Not implemented yet");
    }

    /**
     * order-failed 토픽에 주문 실패 이벤트를 발행합니다.
     * TODO: 결제 실패 시 사용
     */
    public void sendOrderFailedEvent(Long orderId, String reason) {
        // TODO: 구현 필요
        // Topic name: "order-failed"
        throw new UnsupportedOperationException("Not implemented yet");
    }
}
