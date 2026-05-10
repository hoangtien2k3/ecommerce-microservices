package com.ecommerce.inventoryservice.service;

import com.ecommerce.inventoryservice.dto.response.InventoryResponse;
import com.ecommerce.inventoryservice.repository.InventoryRepository;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Slf4j
@Service
public class InventoryService {

    @Autowired
    private final InventoryRepository inventoryRepository;

    @Transactional(readOnly = true)
    @SneakyThrows
    public List<InventoryResponse> isInStock(List<String> productName) {
        log.info("Checking Inventory");
        return inventoryRepository.findByProductNameIn(productName)
                .stream()
                .map(inventory ->
                        InventoryResponse.builder()
                                .productName(inventory.getProductName())
                                .isInStock(inventory.getQuantity() > 0)
                                .build()
                ).toList();
    }

}
