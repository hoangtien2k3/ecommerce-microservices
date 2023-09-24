package com.hoangtien2k3.orderservice.service;

import com.hoangtien2k3.orderservice.dto.response.InventoryResponse;
import com.hoangtien2k3.orderservice.dto.OrderLineItemsDto;
import com.hoangtien2k3.orderservice.dto.request.OrderRequest;
import com.hoangtien2k3.orderservice.model.Order;
import com.hoangtien2k3.orderservice.model.OrderLineItems;
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
        order.setOrderNumber(UUID.randomUUID().toString());

        List<OrderLineItems> orderLineItems = orderRequest.getOrderLineItemsDtoList()
                .stream()
                .map(this::mapToDto)
                .toList();
        order.setOrderLineItemsList(orderLineItems);

        List<String> skuCodes = order.getOrderLineItemsList()
                .stream()
                .map(OrderLineItems::getSkuCode)
                .toList();

        // Call inventory-service, and place order if product is in // stock
        InventoryResponse[] inventoryResponsesArray = webClient.get()
                .uri("http://localhost:8083/api/inventory",
                        uriBuilder -> uriBuilder.queryParam("skuCode", skuCodes).build())
                .retrieve()
                .bodyToMono(InventoryResponse[].class)
                .block();

        boolean allProductsInStock = Arrays.stream(inventoryResponsesArray)
                .allMatch(InventoryResponse::isInStock);

        if (allProductsInStock) {
            orderRepository.save(order);
        } else {
            // hết hàng
            throw new IllegalArgumentException("Product is not in stock, please try again later");
        }

    }

    private OrderLineItems mapToDto(OrderLineItemsDto orderLineItemsDto) {
        OrderLineItems orderLineItems = new OrderLineItems();
        orderLineItems.setPrice(orderLineItemsDto.getPrice());
        orderLineItems.setQuantity(orderLineItems.getQuantity());
        orderLineItems.setSkuCode(orderLineItems.getSkuCode());
        return orderLineItems;
    }

}
