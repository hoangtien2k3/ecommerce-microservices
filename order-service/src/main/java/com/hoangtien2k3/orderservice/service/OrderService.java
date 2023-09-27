package com.hoangtien2k3.orderservice.service;

import com.hoangtien2k3.orderservice.dto.response.InventoryResponse;
import com.hoangtien2k3.orderservice.dto.OrderItemsDto;
import com.hoangtien2k3.orderservice.dto.request.OrderRequest;
import com.hoangtien2k3.orderservice.model.Order;
import com.hoangtien2k3.orderservice.model.OrderItems;
import com.hoangtien2k3.orderservice.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrderService {

    @Autowired
    private final OrderRepository orderRepository;

    @Autowired
    private final WebClient webClient;


    public void placeOrder(OrderRequest orderRequest) {
        Order order = new Order();
        // tạo ra một chuỗi UUID ngẫu nhiên: Ví dụ: 54947df8-0e9e-4471-a2f9-9af509fb58892
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


        // Call inventory-service, and place order if product is in // stock
        // TODO: Add token
        InventoryResponse[] inventoryResponsesArray = webClient.get()
                .uri("http://localhost:8083/api/inventory",
                        uriBuilder -> uriBuilder.queryParam("productName", productNameList).build())
                .retrieve() // sen request and return response
                .bodyToMono(InventoryResponse[].class) // Chuyển đổi phần thân của phản hồi thành một Mono của mảng InventoryResponse.
                .block();   // Đợi cho Mono hoàn thành và trả về giá trị của nó.


        assert inventoryResponsesArray != null;
        boolean allProductsInStock = Arrays.stream(inventoryResponsesArray)
                .allMatch(InventoryResponse::isInStock);

        if (allProductsInStock) {
            orderRepository.save(order);
        } else {
            // hết hàng
            throw new IllegalArgumentException("Product is not in store, try again!");
        }

    }

    private OrderItems mapToOrderItemsDto(OrderItemsDto orderLineItemsDto) {
        OrderItems orderLineItems = new OrderItems();
        orderLineItems.setPrice(orderLineItemsDto.getPrice());
        orderLineItems.setQuantity(orderLineItems.getQuantity());
        orderLineItems.setProductName(orderLineItems.getProductName());
        return orderLineItems;
    }

}
