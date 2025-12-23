package com.example.inventory.service;

import com.example.inventory.domain.Inventory;
import com.example.inventory.dto.InventoryResponseDto;
import com.example.inventory.kafka.InventoryProducer;
import com.example.inventory.repository.InventoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class InventoryService {
    private final InventoryRepository inventoryRepository;
    private final InventoryProducer inventoryProducer;
    /**
     * 주문에 대한 재고를 예약합니다.
     * TODO: 1. InventoryRepository.findById로 재고 확인
     * TODO: 2. 재고가 충분한지 검증
     * TODO: 3. 재고 차감 (quantity -= 주문수량)
     * TODO: 4. DB에 저장
     * TODO: 5. 성공 시 InventoryProducer.sendInventoryReservedEvent 호출
     * TODO: 6. 실패 시 InventoryProducer.sendOrderFailedEvent 호출
     */
    @Transactional
    public void reserveInventory(Long orderId, Long productId, Long userId, Integer quantity) {
        Optional<Inventory> optionalInventory =
                inventoryRepository.findByProductIdWithLock(productId);

        if (optionalInventory.isEmpty()) {
            inventoryProducer.sendOrderFailedEvent(orderId, "상품이 존재하지 않습니다.");
            return;
        }

        Inventory inventory = optionalInventory.get();
        if (inventory.getQuantity() < quantity) {
            inventoryProducer.sendOrderFailedEvent(orderId, "재고가 부족합니다.");
            return;
        }

        inventory.setQuantity(inventory.getQuantity() - quantity);
        int amount = quantity * inventory.getPrice();  // 주문 수량 × 단가
        inventoryProducer.sendInventoryReservedEvent(orderId, userId, amount);
    }

    /**
     * 주문 실패 시 재고를 복구합니다 (보상 트랜잭션).
     * TODO: 1. InventoryRepository.findById로 재고 조회
     * TODO: 2. 재고 복구 (quantity += 주문수량)
     * TODO: 3. DB에 저장
     * TODO: 4. 로깅
     */
    @Transactional
    public void rollbackInventory(Long orderId, Long productId, Integer quantity) {
        Inventory inventory = inventoryRepository.findByProductIdWithLock(productId)
                .orElseThrow(() -> new RuntimeException("상품이 존재하지 않습니다."));

        inventory.setQuantity(inventory.getQuantity() + quantity);
    }

    /**
     * 재고 정보를 조회합니다.
     * TODO: 1. InventoryRepository.findById로 조회
     * TODO: 2. Inventory Entity → InventoryResponseDto 변환 후 반환
     */
    public InventoryResponseDto getInventory(Long productId) {
        Inventory inventory = inventoryRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("상품이 존재하지 않습니다."));
        return InventoryResponseDto.builder()
                .productId(inventory.getProductId())
                .quantity(inventory.getQuantity())
                .build();
    }
}
