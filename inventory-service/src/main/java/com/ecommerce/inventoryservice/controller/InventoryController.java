package com.ecommerce.inventoryservice.controller;

import com.ecommerce.inventoryservice.dto.response.InventoryResponse;
import com.ecommerce.inventoryservice.service.InventoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.List;

@RestController
@RequestMapping("/api/inventory")
@RequiredArgsConstructor
public class InventoryController {

    private static final Logger log = LoggerFactory.getLogger(InventoryController.class);

    private final InventoryService inventoryService;

    // http://localhost:8083/api/inventory/iphone_13,iphone13_red

    // http://localhost:8083/api/inventory?productName=iphone_13&productName=iphone13_red

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<InventoryResponse> isInStockNoAccessToken(@RequestParam List<String> productName) {
        log.info("Received inventory check request for skuCode: {}", productName);
        return inventoryService.isInStock(productName);
    }

}
