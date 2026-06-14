package com.ecommerce.paymentservice.service.impl;

import com.ecommerce.paymentservice.constant.KafkaConstant;
import com.ecommerce.paymentservice.dto.KafkaPaymentDto;
import com.ecommerce.paymentservice.dto.OrderDto;
import com.ecommerce.paymentservice.dto.PaymentDto;
import com.ecommerce.paymentservice.dto.UserDto;
import com.ecommerce.paymentservice.event.EventProducer;
import com.ecommerce.paymentservice.exception.wrapper.PaymentNotFoundException;
import com.ecommerce.paymentservice.helper.PaymentMappingHelper;
import com.ecommerce.paymentservice.repository.PaymentRepository;
import com.ecommerce.paymentservice.security.JwtTokenFilter;
import com.ecommerce.paymentservice.service.CallAPI;
import com.ecommerce.paymentservice.service.PaymentService;
import com.google.gson.Gson;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private static final Logger log = LoggerFactory.getLogger(PaymentServiceImpl.class);

    private final PaymentRepository paymentRepository;
    private final ModelMapper modelMapper;
    private final CallAPI callAPI;
    private final EventProducer eventProducer;
    private final Gson gson = new Gson();

    @Override
    public List<PaymentDto> findAll() {
        log.info("PaymentDto List, service; fetch all payments");
        return paymentRepository.findAll()
                .stream()
                .map(PaymentMappingHelper::map)
                .peek(paymentDto -> {
                    try {
                        OrderDto orderDto = callAPI.receiverPaymentDto(paymentDto.getOrderId(), JwtTokenFilter.getTokenFromRequest());
                        if (orderDto != null) {
                            paymentDto.setOrderDto(modelMapper.map(orderDto, OrderDto.class));
                        }
                    } catch (Exception e) {
                        log.error("Error fetching order info for payment {}: {}", paymentDto.getPaymentId(), e.getMessage());
                    }
                })
                .toList();
    }

    @Override
    public Page<PaymentDto> findAll(int page, int size, String sortBy, String sortOrder) {
        log.info("PaymentDto List, service; fetch all payments with paging");
        Sort sort = Sort.by(Sort.Direction.fromString(sortOrder), sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);
        List<PaymentDto> paymentDtos = paymentRepository.findAll(pageable)
                .map(PaymentMappingHelper::map)
                .stream()
                .peek(paymentDto -> {
                    try {
                        OrderDto orderDto = callAPI.receiverPaymentDto(paymentDto.getOrderId(), JwtTokenFilter.getTokenFromRequest());
                        if (orderDto != null) {
                            paymentDto.setOrderDto(modelMapper.map(orderDto, OrderDto.class));
                        }
                    } catch (Exception e) {
                        log.error("Error fetching order info: {}", e.getMessage());
                    }
                })
                .toList();
        return new PageImpl<>(paymentDtos, pageable, paymentDtos.size());
    }

    @Override
    public PaymentDto findById(Integer paymentId) {
        log.info("PaymentDto, service; fetch payment by id");
        PaymentDto paymentDto = paymentRepository.findById(paymentId)
                .map(PaymentMappingHelper::map)
                .orElseThrow(() -> new PaymentNotFoundException(String.format("Payment with id: %d not found", paymentId)));
        try {
            String token = JwtTokenFilter.getTokenFromRequest();
            OrderDto orderDto = callAPI.receiverPaymentDto(paymentDto.getOrderDto().getOrderId(), token);
            if (orderDto != null) {
                paymentDto.setOrderDto(modelMapper.map(orderDto, OrderDto.class));
            }
            UserDto userDto = callAPI.receiverUserDto(paymentDto.getUserId(), token);
            if (userDto != null) {
                paymentDto.setUserDto(modelMapper.map(userDto, UserDto.class));
            }
        } catch (Exception e) {
            log.error("Error fetching order or user info: {}", e.getMessage());
        }
        return paymentDto;
    }

    public OrderDto getOrderDto(Integer orderId) {
        return callAPI.receiverPaymentDto(orderId, JwtTokenFilter.getTokenFromRequest());
    }

    @Override
    public PaymentDto save(PaymentDto paymentDto) {
        log.info("PaymentDto, service; save payment");
        if (paymentRepository.existsByOrderIdAndIsPayed(paymentDto.getOrderId())) {
            throw new PaymentNotFoundException("Order has already been paid.");
        }
        PaymentDto savedPaymentDto = PaymentMappingHelper.map(paymentRepository.save(PaymentMappingHelper.map(paymentDto)));
        KafkaPaymentDto kafkaPaymentDto = KafkaPaymentDto.builder()
                .paymentId(savedPaymentDto.getPaymentId())
                .isPayed(savedPaymentDto.getIsPayed())
                .paymentStatus(savedPaymentDto.getPaymentStatus())
                .orderId(savedPaymentDto.getOrderId())
                .userId(savedPaymentDto.getUserId())
                .build();
        eventProducer.send(KafkaConstant.STATUS_PAYMENT_SUCCESSFUL, gson.toJson(kafkaPaymentDto));
        return savedPaymentDto;
    }

    @Override
    public PaymentDto update(PaymentDto paymentDto) {
        log.info("PaymentDto, service; update payment");
        return PaymentMappingHelper.map(paymentRepository.save(PaymentMappingHelper.map(paymentDto)));
    }

    @Override
    public PaymentDto update(Integer paymentId, PaymentDto paymentDto) {
        log.info("PaymentDto, service; update payment with paymentId");
        PaymentDto existingPaymentDto = findById(paymentId);
        modelMapper.map(paymentDto, existingPaymentDto);
        return PaymentMappingHelper.map(paymentRepository.save(PaymentMappingHelper.map(existingPaymentDto)));
    }

    @Override
    public void deleteById(Integer paymentId) {
        log.info("Void, service; delete payment by id");
        paymentRepository.deleteById(paymentId);
    }
}
