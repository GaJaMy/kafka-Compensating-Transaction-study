package com.example.inventory.controller;

import com.example.inventory.dto.InventoryResponseDto;
import com.example.inventory.service.InventoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/inventory")
@RequiredArgsConstructor
public class InventoryController {

    private final InventoryService inventoryService;

    /**
     * 상품의 재고 수량을 조회합니다.
     * TODO: InventoryService.getInventoryQuantity() 호출
     */
    @GetMapping("/{productId}")
    public ResponseEntity<InventoryResponseDto> getInventory(@PathVariable Long productId) {
        // TODO: 구현 필요
        throw new UnsupportedOperationException("Not implemented yet");
    }
}
