package com.hoangtien2k3.inventoryservice.controller;

import com.hoangtien2k3.inventoryservice.dto.InventoryResponse;
import com.hoangtien2k3.inventoryservice.service.InventoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/inventory")
@RequiredArgsConstructor
@Slf4j
public class InventoryController {

    private final InventoryService inventoryService;

    // http://localhost:8082/api/inventory/iphone-13,iphone13-red

    // http://localhost:8082/api/inventory?productName=iphone-13&productName=iphone13-red

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<InventoryResponse> isInStock(@RequestParam List<String> productName) {
        log.info("Received inventory check request for skuCode: {}", productName);
        return inventoryService.isInStock(productName);
    }


}