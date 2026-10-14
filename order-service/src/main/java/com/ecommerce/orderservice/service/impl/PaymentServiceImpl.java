package com.ecommerce.orderservice.service.impl;

import com.ecommerce.commonlib.exception.BusinessException;
import com.ecommerce.commonlib.exception.ErrorCode;
import com.ecommerce.orderservice.dto.inventory.InventoryStockDto;
import com.ecommerce.orderservice.dto.payment.PaymentDto;
import com.ecommerce.orderservice.dto.product.ProductDto;
import com.ecommerce.orderservice.entity.Order;
import com.ecommerce.orderservice.repository.OrderRepository;
import com.ecommerce.orderservice.repository.PaymentRepository;
import com.ecommerce.orderservice.security.JwtTokenFilter;
import com.ecommerce.orderservice.service.CallAPI;
import com.ecommerce.orderservice.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;
    private final PaymentStore paymentStore;
    private final CallAPI callAPI;

    @Override
    public PaymentDto save(PaymentDto paymentDto) {
        if (paymentRepository.existsByOrderIdAndIsPayed(paymentDto.getOrderId())) {
            throw BusinessException.conflict("Order has already been paid.");
        }
        Order order = findOrder(paymentDto.getOrderId());
        if (isSoldOut(order)) {
            paymentStore.saveUnpaid(paymentDto);
            throw BusinessException.of(ErrorCode.STOCK_INSUFFICIENT);
        }
        boolean couponUsed = useCoupon(order);
        try {
            return paymentStore.saveCompleted(paymentDto);
        } catch (RuntimeException ex) {
            if (couponUsed) {
                releaseCoupon(order, ex);
            }
            throw ex;
        }
    }

    private Order findOrder(Integer orderId) {
        if (orderId == null) {
            return null;
        }
        return orderRepository.findById(orderId)
                .orElseThrow(() -> BusinessException.notFound("order.not.found"));
    }

    private boolean isSoldOut(Order order) {
        if (order == null || order.getProductId() == null) {
            return false;
        }
        String authorization = JwtTokenFilter.getTokenFromRequest();
        ProductDto product = callAPI.receiverProductDto(order.getProductId(), authorization);
        if (product == null || product.getProductTitle() == null || product.getProductTitle().isBlank()) {
            return false;
        }
        List<InventoryStockDto> stocks = callAPI.receiverInventory(product.getProductTitle(), authorization);
        return stocks.stream().anyMatch(stock -> !stock.isInStock());
    }

    private boolean useCoupon(Order order) {
        if (order == null || order.getPromotionId() == null) {
            return false;
        }
        callAPI.usePromotion(
                order.getPromotionId(),
                order.getOrderId().longValue(),
                order.getProductId() == null ? null : order.getProductId().longValue(),
                JwtTokenFilter.getTokenFromRequest());
        return true;
    }

    private void releaseCoupon(Order order, RuntimeException paymentFailure) {
        try {
            callAPI.releasePromotion(
                    order.getPromotionId(),
                    order.getOrderId().longValue(),
                    JwtTokenFilter.getTokenFromRequest());
        } catch (RuntimeException releaseFailure) {
            paymentFailure.addSuppressed(releaseFailure);
        }
    }

}
