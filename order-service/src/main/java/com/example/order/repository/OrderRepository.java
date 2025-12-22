package com.example.order.repository;

import com.example.order.domain.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    // TODO: 필요한 커스텀 쿼리 메서드 추가 가능
    // 예: List<Order> findByUserId(Long userId);
}
