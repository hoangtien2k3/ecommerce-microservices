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
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class OrderItemServiceImpl implements OrderItemService {

    private final OrderItemRepository orderItemRepository;
    private final RestTemplate restTemplate;

    @Override
    public List<OrderItemDto> findAll() {
        log.info("OrderItemDto List, service; fetch all orderItems");
        return this.orderItemRepository.findAll()
                .stream()
                .map(OrderItemMappingHelper::map)
                .peek(o -> {
                    o.setProductDto(this.restTemplate.getForObject(AppConstant.DiscoveredDomainsApi
                            .PRODUCT_SERVICE_API_URL + "/" + o.getProductDto().getProductId(), ProductDto.class));
                    o.setOrderDto(this.restTemplate.getForObject(AppConstant.DiscoveredDomainsApi
                            .ORDER_SERVICE_API_URL + "/" + o.getOrderDto().getOrderId(), OrderDto.class));
                })
                .distinct()
                .toList();
    }

    @Override
    public OrderItemDto findById(final OrderItemId orderItemId) {
        log.info("OrderItemDto, service; fetch orderItem by id");
        return this.orderItemRepository.findById(null)
                .map(OrderItemMappingHelper::map)
                .map(o -> {
                    o.setProductDto(this.restTemplate.getForObject(AppConstant.DiscoveredDomainsApi
                            .PRODUCT_SERVICE_API_URL + "/" + o.getProductDto().getProductId(), ProductDto.class));
                    o.setOrderDto(this.restTemplate.getForObject(AppConstant.DiscoveredDomainsApi
                            .ORDER_SERVICE_API_URL + "/" + o.getOrderDto().getOrderId(), OrderDto.class));
                    return o;
                })
                .orElseThrow(() -> new OrderItemNotFoundException(String.format("OrderItem with id: %s not found", orderItemId)));
    }

    @Override
    public OrderItemDto save(final OrderItemDto orderItemDto) {
        log.info("OrderItemDto, service; save orderItem");
        return OrderItemMappingHelper.map(this.orderItemRepository
                .save(OrderItemMappingHelper.map(orderItemDto)));
    }

    @Override
    public OrderItemDto update(final OrderItemDto orderItemDto) {
        log.info("OrderItemDto, service; update orderItem");
        return OrderItemMappingHelper.map(this.orderItemRepository
                .save(OrderItemMappingHelper.map(orderItemDto)));
    }

    @Override
    public void deleteById(final OrderItemId orderItemId) {
        log.info("Void, service; delete orderItem by id");
        this.orderItemRepository.deleteById(orderItemId);
    }

}
