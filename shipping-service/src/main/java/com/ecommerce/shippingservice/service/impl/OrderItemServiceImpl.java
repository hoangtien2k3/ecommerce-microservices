package com.ecommerce.shippingservice.service.impl;

import com.ecommerce.shippingservice.constant.AppConstant;
import com.ecommerce.shippingservice.domain.id.OrderItemId;
import com.ecommerce.shippingservice.dto.OrderDto;
import com.ecommerce.shippingservice.dto.OrderItemDto;
import com.ecommerce.shippingservice.dto.ProductDto;
import com.ecommerce.shippingservice.exception.wrapper.OrderItemNotFoundException;
import com.ecommerce.shippingservice.helper.OrderItemMappingHelper;
import com.ecommerce.shippingservice.repository.OrderItemRepository;
import com.ecommerce.shippingservice.service.OrderItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderItemServiceImpl implements OrderItemService {

    private static final Logger log = LoggerFactory.getLogger(OrderItemServiceImpl.class);

    private final OrderItemRepository orderItemRepository;
    private final RestClient.Builder restClientBuilder;

    @Override
    public List<OrderItemDto> findAll() {
        log.info("OrderItemDto List, service; fetch all orderItems");
        RestClient restClient = restClientBuilder.build();
        return orderItemRepository.findAll()
                .stream()
                .map(OrderItemMappingHelper::map)
                .peek(o -> {
                    try {
                        ProductDto product = restClient.get()
                                .uri(AppConstant.DiscoveredDomainsApi.PRODUCT_SERVICE_API_URL + "/{id}",
                                        o.getProductDto().getProductId())
                                .retrieve()
                                .body(ProductDto.class);
                        o.setProductDto(product);
                        OrderDto order = restClient.get()
                                .uri(AppConstant.DiscoveredDomainsApi.ORDER_SERVICE_API_URL + "/{id}",
                                        o.getOrderDto().getOrderId())
                                .retrieve()
                                .body(OrderDto.class);
                        o.setOrderDto(order);
                    } catch (Exception e) {
                        log.error("Error fetching product/order for orderItem: {}", e.getMessage());
                    }
                })
                .distinct()
                .toList();
    }

    @Override
    public OrderItemDto findById(final OrderItemId orderItemId) {
        log.info("OrderItemDto, service; fetch orderItem by id");
        RestClient restClient = restClientBuilder.build();
        return orderItemRepository.findById(null)
                .map(OrderItemMappingHelper::map)
                .map(o -> {
                    try {
                        o.setProductDto(restClient.get()
                                .uri(AppConstant.DiscoveredDomainsApi.PRODUCT_SERVICE_API_URL + "/{id}",
                                        o.getProductDto().getProductId())
                                .retrieve()
                                .body(ProductDto.class));
                        o.setOrderDto(restClient.get()
                                .uri(AppConstant.DiscoveredDomainsApi.ORDER_SERVICE_API_URL + "/{id}",
                                        o.getOrderDto().getOrderId())
                                .retrieve()
                                .body(OrderDto.class));
                    } catch (Exception e) {
                        log.error("Error fetching product/order for orderItem: {}", e.getMessage());
                    }
                    return o;
                })
                .orElseThrow(() -> new OrderItemNotFoundException(
                        String.format("OrderItem with id: %s not found", orderItemId)));
    }

    @Override
    public OrderItemDto save(final OrderItemDto orderItemDto) {
        log.info("OrderItemDto, service; save orderItem");
        return OrderItemMappingHelper.map(orderItemRepository.save(OrderItemMappingHelper.map(orderItemDto)));
    }

    @Override
    public OrderItemDto update(final OrderItemDto orderItemDto) {
        log.info("OrderItemDto, service; update orderItem");
        return OrderItemMappingHelper.map(orderItemRepository.save(OrderItemMappingHelper.map(orderItemDto)));
    }

    @Override
    public void deleteById(final OrderItemId orderItemId) {
        log.info("Void, service; delete orderItem by id");
        orderItemRepository.deleteById(orderItemId);
    }
}
