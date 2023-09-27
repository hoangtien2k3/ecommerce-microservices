package com.hoangtien2k3.inventoryservice.service;


import com.hoangtien2k3.inventoryservice.dto.InventoryResponse;
import com.hoangtien2k3.inventoryservice.repository.InventoryRepository;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class InventoryService {

    private final InventoryRepository inventoryRepository;

    @Transactional(readOnly = true)
    @SneakyThrows
    public List<InventoryResponse> isInStock(List<String> productName) {
        log.info("Checking Inventory"); // còn hàng hay không
        return inventoryRepository.findByProductNameIn(productName).stream()
                .map(inventory ->
                        InventoryResponse.builder()
                                .productName(inventory.getProductName())
                                .isInStock(inventory.getQuantity() > 0)
                                .build()
                ).toList();
    }
}
