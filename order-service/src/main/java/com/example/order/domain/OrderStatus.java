package com.example.order.domain;

public enum OrderStatus {
    CREATED,           // 주문 생성
    INVENTORY_RESERVED, // 재고 예약 완료
    PAYMENT_COMPLETED, // 결제 완료
    COMPLETED,         // 주문 완료
    FAILED             // 주문 실패
}
