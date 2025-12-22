package com.example.payment.service;

import com.example.payment.dto.PaymentResponseDto;
import org.springframework.stereotype.Service;

@Service
public class PaymentService {

    /**
     * 재고 예약 완료 후 결제를 처리합니다.
     * TODO: 1. Payment 엔티티 생성 (orderId, userId, amount, status=PENDING)
     * TODO: 2. DB에 저장 (PaymentRepository.save)
     * TODO: 3. 결제 처리 로직 (실제로는 외부 결제 API 호출)
     * TODO: 4. 성공 시: status를 COMPLETED로 변경 후 DB 저장
     * TODO: 5. 성공 시: PaymentProducer.sendPaymentCompletedEvent 호출
     * TODO: 6. 실패 시: status를 FAILED로 변경 후 DB 저장
     * TODO: 7. 실패 시: PaymentProducer.sendOrderFailedEvent 호출
     */
    public void processPayment(Long orderId, Long userId, Integer amount) {
        // TODO: 구현 필요
        throw new UnsupportedOperationException("Not implemented yet");
    }

    /**
     * 주문 실패 시 결제를 취소합니다 (보상 트랜잭션).
     * TODO: 1. PaymentRepository.findByOrderId로 결제 조회
     * TODO: 2. status를 FAILED로 변경
     * TODO: 3. DB에 저장
     * TODO: 4. 로깅 (실제로는 환불 API 호출)
     */
    public void cancelPayment(Long orderId) {
        // TODO: 구현 필요
        throw new UnsupportedOperationException("Not implemented yet");
    }

    /**
     * 결제 정보를 조회합니다.
     * TODO: 1. PaymentRepository.findByOrderId로 조회
     * TODO: 2. Payment Entity → PaymentResponseDto 변환 후 반환
     */
    public PaymentResponseDto getPaymentByOrderId(Long orderId) {
        // TODO: 구현 필요
        throw new UnsupportedOperationException("Not implemented yet");
    }
}
