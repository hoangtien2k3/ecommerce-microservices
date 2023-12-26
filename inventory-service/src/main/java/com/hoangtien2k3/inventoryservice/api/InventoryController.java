package com.hoangtien2k3.inventoryservice.api;

import com.hoangtien2k3.inventoryservice.dto.response.InventoryResponse;
import com.hoangtien2k3.inventoryservice.security.JwtValidate;
import com.hoangtien2k3.inventoryservice.service.InventoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/inventory")
@RequiredArgsConstructor
@Slf4j
public class InventoryController {

    @Autowired
    private final InventoryService inventoryService;

    @Autowired
    private final JwtValidate jwtValidate;


    // http://localhost:8083/api/inventory/iphone_13,iphone13_red

    // http://localhost:8083/api/inventory?productName=iphone_13&productName=iphone13_red

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<InventoryResponse> isInStockNoAccessToken(@RequestHeader(name = "Authorization") String authorizationHeader,
                                                          @RequestParam List<String> productName) {
        if (jwtValidate.validateTokenUserService(authorizationHeader)) {
            log.info("Received inventory check request for skuCode: {}", productName);
            return inventoryService.isInStock(productName);
        }
        return List.of(new InventoryResponse(null, false));
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/validateToken")
    public String getOrderDetails(@RequestHeader(name = "Authorization") String authorizationHeader) {
        if (jwtValidate.validateTokenUserService(authorizationHeader)) {
            return inventoryService.getTokenUserService(authorizationHeader);
        } else {
            return "Unauthorized accessToken";
        }
    }

}