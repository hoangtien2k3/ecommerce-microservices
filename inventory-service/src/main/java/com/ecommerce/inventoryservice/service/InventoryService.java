package com.ecommerce.inventoryservice.service;

import com.ecommerce.inventoryservice.dto.response.InventoryResponse;
import com.ecommerce.inventoryservice.repository.InventoryRepository;
import lombok.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.List;

@RequiredArgsConstructor
@Service
public class InventoryService {

    private static final Logger log = LoggerFactory.getLogger(InventoryService.class);

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
