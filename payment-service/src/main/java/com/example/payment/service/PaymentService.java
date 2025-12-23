package com.example.payment.service;

import com.example.payment.domain.Payment;
import com.example.payment.domain.PaymentStatus;
import com.example.payment.dto.PaymentResponseDto;
import com.example.payment.kafka.PaymentProducer;
import com.example.payment.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PaymentService {
    private final PaymentRepository paymentRepository;
    private final PaymentProducer paymentProducer;

    /**
     * 재고 예약 완료 후 결제를 처리합니다.
     *
     * PENDING 상태를 먼저 저장하는 이유:
     * - 외부 API 호출 중 서버 장애 시 결제 시도 이력 보존
     * - 결제 처리 중 상태를 DB에 기록하여 추적 가능
     * - 장애 복구 후 PENDING 상태 건들을 재처리할 수 있음
     *
     * TODO: 1. Payment 엔티티 생성 (orderId, userId, amount, status=PENDING)
     * TODO: 2. DB에 저장 (결제 시도 기록)
     * TODO: 3. try-catch로 결제 처리 로직 실행
     *         - Thread.sleep()으로 외부 PG사 API 호출 시뮬레이션
     *         - 금액 검증 (50,000원 이상이면 예외 발생)
     * TODO: 4. 성공 시:
     *         - status를 COMPLETED로 변경
     *         - DB 업데이트
     *         - PaymentProducer.sendPaymentCompletedEvent 호출
     * TODO: 5. 실패 시 (catch):
     *         - status를 FAILED로 변경
     *         - DB 업데이트
     *         - PaymentProducer.sendOrderFailedEvent 호출 (보상 트랜잭션 시작)
     */
    public void processPayment(Long orderId, Long userId, Integer amount) {
        Payment payment = Payment.builder()
                .orderId(orderId)
                .userId(userId)
                .amount(amount)
                .status(PaymentStatus.PENDING)
                .build();

        paymentRepository.save(payment);

        try {
            Thread.sleep(2000);

            if (amount >= 50000) {
                throw new RuntimeException("결제 한도 초과");
            }

            payment.setStatus(PaymentStatus.COMPLETED);
            paymentRepository.save(payment);
            paymentProducer.sendPaymentCompletedEvent(orderId, payment.getPaymentId());
        } catch (Exception e) {
            payment.setStatus(PaymentStatus.FAILED);
            paymentRepository.save(payment);
            paymentProducer.sendOrderFailedEvent(orderId, e.getMessage());
        }
    }

    /**
     * 주문 실패 시 결제를 취소합니다 (보상 트랜잭션).
     * TODO: 1. PaymentRepository.findByOrderId로 결제 조회
     * TODO: 2. status를 FAILED로 변경
     * TODO: 3. DB에 저장
     * TODO: 4. 로깅 (실제로는 환불 API 호출)
     */
    public void cancelPayment(Long orderId) {
        Payment payment = paymentRepository.findByOrderId(orderId)
                .orElseThrow(() -> new RuntimeException("결재 정보가 없습니다."));

        payment.setStatus(PaymentStatus.FAILED);
    }

    /**
     * 결제 정보를 조회합니다.
     * TODO: 1. PaymentRepository.findByOrderId로 조회
     * TODO: 2. Payment Entity → PaymentResponseDto 변환 후 반환
     */
    public PaymentResponseDto getPaymentByOrderId(Long orderId) {
        Payment payment = paymentRepository.findByOrderId(orderId)
                .orElseThrow(() -> new RuntimeException("결재 정보가 없습니다."));

        return PaymentResponseDto.builder()
                .paymentId(payment.getPaymentId())
                .orderId(payment.getOrderId())
                .userId(payment.getUserId())
                .amount(payment.getAmount())
                .status(payment.getStatus())
                .build();
    }
}
