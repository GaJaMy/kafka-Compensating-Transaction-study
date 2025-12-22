package com.example.order.dto;

import com.example.order.domain.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderResponseDto {

    private Long orderId;
    private Long productId;
    private Integer quantity;
    private Long userId;
    private OrderStatus status;

}
