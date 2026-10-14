package com.ecommerce.orderservice.service.impl;

import com.ecommerce.orderservice.dto.order.OrderDto;
import com.ecommerce.orderservice.dto.promotion.UnusedPromotionIssueResponse;
import com.ecommerce.orderservice.exception.wrapper.OrderNotFoundException;
import com.ecommerce.orderservice.helper.OrderMappingHelper;
import com.ecommerce.orderservice.repository.OrderRepository;
import com.ecommerce.orderservice.security.JwtTokenFilter;
import com.ecommerce.orderservice.service.CallAPI;
import com.ecommerce.orderservice.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@RequiredArgsConstructor
@Service
public class OrderServiceImpl implements OrderService {

    private static final Logger log = LoggerFactory.getLogger(OrderServiceImpl.class);

    private final OrderRepository orderRepository;
    private final ModelMapper modelMapper;
    private final CallAPI callAPI;

    @Override
    public List<OrderDto> findAll() {
        log.info("OrderDto List, service; fetch all orders");
        return orderRepository.findAll()
                .stream()
                .map(OrderMappingHelper::map)
                .peek(orderDto -> {
                    try {
                        orderDto.setProductDto(callAPI.receiverProductDto(orderDto.getProductId()));
                    } catch (Exception e) {
                        log.error("Error fetching product info: {}", e.getMessage());
                    }
                })
                .toList();
    }

    @Override
    public Page<OrderDto> findAll(int page, int size, String sortBy, String sortOrder) {
        log.info("OrderDto List, service; fetch all orders with paging and sorting");
        Sort sort = Sort.by(Sort.Direction.fromString(sortOrder), sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);

        List<OrderDto> orderDtos = orderRepository.findAll(pageable)
                .stream()
                .map(OrderMappingHelper::map)
                .peek(orderDto -> {
                    try {
                        orderDto.setProductDto(callAPI.receiverProductDto(orderDto.getProductId()));
                    } catch (Exception e) {
                        log.error("Error fetching product info: {}", e.getMessage());
                    }
                })
                .toList();
        return new PageImpl<>(orderDtos, pageable, orderDtos.size());
    }

    @Override
    public OrderDto findById(Integer orderId) {
        log.info("OrderDto, service; fetch order by id");
        OrderDto orderDto = orderRepository.findById(orderId)
                .map(OrderMappingHelper::map)
                .orElseThrow(() -> new OrderNotFoundException(String.format("Order with id: %d not found", orderId)));
        try {
            orderDto.setProductDto(callAPI.receiverProductDto(orderDto.getProductId()));
        } catch (Exception e) {
            log.error("Error fetching product info: {}", e.getMessage());
        }
        return orderDto;
    }

    @Override
    public Boolean existsByOrderId(Integer orderId) {
        return orderRepository.findById(orderId).isPresent();
    }

    @Override
    public OrderDto save(OrderDto orderDto) {
        log.info("OrderDto, service; save order");
        applyCoupon(orderDto);
        return OrderMappingHelper.map(orderRepository.save(OrderMappingHelper.map(orderDto)));
    }

    @Override
    public OrderDto update(OrderDto orderDto) {
        log.info("OrderDto, service; update order");
        return OrderMappingHelper.map(orderRepository.save(OrderMappingHelper.map(orderDto)));
    }

    @Override
    public OrderDto update(Integer orderId, OrderDto orderDto) {
        log.info("OrderDto, service; update order with orderId");
        OrderDto existingOrderDto = findById(orderId);
        modelMapper.map(orderDto, existingOrderDto);
        return OrderMappingHelper.map(orderRepository.save(OrderMappingHelper.map(existingOrderDto)));
    }

    @Override
    public void deleteById(Integer orderId) {
        log.info("Void, service; delete order by id");
        orderRepository.deleteById(orderId);
    }

    private void applyCoupon(OrderDto orderDto) {
        if (orderDto.getPromotionId() == null) {
            orderDto.setDiscountAmount(null);
            return;
        }
        UnusedPromotionIssueResponse issue = callAPI.unusedPromotionIssue(
                orderDto.getPromotionId(), JwtTokenFilter.getTokenFromRequest());
        BigDecimal fee = money(orderDto.getOrderFee());
        BigDecimal discount = couponDiscount(issue, fee);
        BigDecimal storedFee = discount.compareTo(fee) > 0 ? BigDecimal.ZERO : fee.subtract(discount);
        orderDto.setOrderFee(storedFee.doubleValue());
        orderDto.setDiscountAmount(discount.doubleValue());
        orderDto.setPromotionId(issue.promotionId());
    }

    private static BigDecimal couponDiscount(UnusedPromotionIssueResponse issue, BigDecimal fee) {
        if ("PERCENTAGE".equals(issue.discountType()) && issue.discountPercentage() != null) {
            return fee.multiply(BigDecimal.valueOf(issue.discountPercentage()))
                    .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
        }
        if (issue.discountAmount() == null) {
            return BigDecimal.ZERO;
        }
        return BigDecimal.valueOf(issue.discountAmount());
    }

    private static BigDecimal money(Double amount) {
        return amount == null ? BigDecimal.ZERO : BigDecimal.valueOf(amount);
    }
}
