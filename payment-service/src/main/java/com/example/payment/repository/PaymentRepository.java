package com.example.payment.repository;

import com.example.payment.domain.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    // TODO: 필요한 커스텀 쿼리 메서드 추가 가능
    Optional<Payment> findByOrderId(Long orderId);
}
