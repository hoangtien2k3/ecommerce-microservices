package com.hoangtien2k3.orderservice.service;

import com.hoangtien2k3.orderservice.dto.response.InventoryResponse;
import com.hoangtien2k3.orderservice.dto.OrderItemsDto;
import com.hoangtien2k3.orderservice.dto.request.OrderRequest;
import com.hoangtien2k3.orderservice.model.Order;
import com.hoangtien2k3.orderservice.model.OrderItems;
import com.hoangtien2k3.orderservice.repository.OrderRepository;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor
public class OrderService {

    @Autowired
    private final OrderRepository orderRepository;

    @Autowired
    private final RestTemplate restTemplate; // Sử dụng RestTemplate

    // Phương thức này thực hiện cuộc gọi tới inventory-service với token
    public void placeOrderWithToken(OrderRequest orderRequest, String token) {
        Order order = new Order();
        order.setOrderNumber(UUID.randomUUID().toString().replace("-", ""));

        List<OrderItems> orderItems = orderRequest
                .getOrderItemsDtoList()
                .stream()
                .map(this::mapToOrderItemsDto)
                .toList();
        order.setOrderItemsList(orderItems);

        List<String> productNameList = order.getOrderItemsList()
                .stream()
                .map(OrderItems::getProductName)
                .toList();

        // Tạo HttpHeaders và thêm token vào
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + token); // Sử dụng token ở đây

        // Tạo HttpEntity với các headers
        HttpEntity<String> requestEntity = new HttpEntity<>(headers);

        // Gọi API từ inventory-service
        ResponseEntity<InventoryResponse[]> responseEntity = restTemplate.exchange(
                "http://localhost:8083/api/inventory",
                HttpMethod.GET, // Hoặc POST, PUT tùy vào yêu cầu của inventory-service
                requestEntity,
                InventoryResponse[].class
        );

        // Kiểm tra phản hồi từ API
        if (responseEntity.getStatusCode() == HttpStatus.OK) {
            InventoryResponse[] inventoryResponsesArray = responseEntity.getBody();
            assert inventoryResponsesArray != null;
            boolean allProductsInStock = Arrays.stream(inventoryResponsesArray)
                    .allMatch(InventoryResponse::isInStock);

            if (allProductsInStock) {
                orderRepository.save(order);
            } else {
                // Hết hàng
                throw new IllegalArgumentException("Product is not in store, try again!");
            }
        } else {
            // Xử lý lỗi nếu có lỗi từ API
            throw new RuntimeException("Failed to call inventory service. Status code: " + responseEntity.getStatusCode());
        }

        orderRepository.save(order);

    }

    private OrderItems mapToOrderItemsDto(OrderItemsDto orderLineItemsDto) {
        OrderItems orderLineItems = new OrderItems();
        orderLineItems.setPrice(orderLineItemsDto.getPrice());
        orderLineItems.setQuantity(orderLineItems.getQuantity());
        orderLineItems.setProductName(orderLineItems.getProductName());
        return orderLineItems;
    }
}
