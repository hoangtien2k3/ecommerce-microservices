package com.hoangtien2k3.paymentservice.service.impl;

import com.hoangtien2k3.paymentservice.dto.PaymentDto;
import com.hoangtien2k3.paymentservice.exception.wrapper.PaymentNotFoundException;
import com.hoangtien2k3.paymentservice.helper.PaymentMappingHelper;
import com.hoangtien2k3.paymentservice.repository.PaymentRepository;
import com.hoangtien2k3.paymentservice.security.JwtTokenFilter;
import com.hoangtien2k3.paymentservice.service.CallAPI;
import com.hoangtien2k3.paymentservice.service.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.transaction.Transactional;
import java.util.List;

@Service
@Transactional
@Slf4j
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    @Autowired
    private final PaymentRepository paymentRepository;

    @Autowired
    private final ModelMapper modelMapper;

    @Autowired
    private final CallAPI callAPI;

    @Override
    public Mono<List<PaymentDto>> findAll() {
        log.info("*** PaymentDto List, service; fetch all payments *");
        return Mono.fromSupplier(() -> paymentRepository.findAll()
                        .stream()
                        .map(PaymentMappingHelper::map)
                        .toList())
                .flatMap(listPaymentDtos -> Flux.fromIterable(listPaymentDtos)
                        .flatMap(paymentDto ->
                                callAPI.receiverPaymentDto(paymentDto.getOrderId(), JwtTokenFilter.getTokenFromRequest())
                                        .map(orderDto -> {
                                            paymentDto.setOrderDto(orderDto);
                                            return paymentDto;
                                        })
                                        .onErrorResume(throwable -> {
                                            log.error("Error fetching order info: {}", throwable.getMessage());
                                            return Mono.just(paymentDto);
                                        })
                        ).collectList()
                );
    }

    @Override
    public Mono<Page<PaymentDto>> findAll(int page, int size, String sortBy, String sortOrder) {
        log.info("PaymentDto List, service; fetch all carts with paging and sorting");
        Sort sort = Sort.by(Sort.Direction.fromString(sortOrder), sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);

        return Mono.fromSupplier(() -> paymentRepository.findAll(pageable)
                        .map(PaymentMappingHelper::map)
                )
                .flatMap(paymentDtos -> Flux.fromIterable(paymentDtos)
                        .flatMap(paymentDto ->
                                callAPI.receiverPaymentDto(paymentDto.getOrderId(), JwtTokenFilter.getTokenFromRequest())
                                        .map(orderDto -> {
                                            paymentDto.setOrderDto(orderDto);
                                            return paymentDto;
                                        })
                                        .onErrorResume(throwable -> {
                                            log.error("Error fetching order info: {}", throwable.getMessage());
                                            return Mono.just(paymentDto);
                                        })
                        )
                        .collectList()
                        .map(resultList -> new PageImpl<>(resultList, pageable, resultList.size()))
                );
    }

    @Override
    public Mono<PaymentDto> findById(Integer paymentId) {
        log.info("*** PaymentDto, service; fetch payment by id *");
        return Mono.fromSupplier(() -> paymentRepository.findById(paymentId)
                        .map(PaymentMappingHelper::map)
                        .orElseThrow(() -> new PaymentNotFoundException(String.format("Order with id: %d not found", paymentId)))
                )
                .flatMap(paymentDto ->
                        callAPI.receiverPaymentDto(paymentDto.getOrderId(), JwtTokenFilter.getTokenFromRequest())
                                .map(orderDto -> {
                                    paymentDto.setOrderDto(orderDto);
                                    return paymentDto;
                                })
                                .onErrorResume(throwable -> {
                                    log.error("Error fetching order info: {}", throwable.getMessage());
                                    return Mono.just(paymentDto);
                                })
                );
    }

    @Override
    public Mono<PaymentDto> save(PaymentDto paymentDto) {
        log.info("PaymentDto, service; save order");
        return Mono.just(paymentDto)
                .filter(dto -> !paymentRepository.existsByOrderIdAndIsPayed(dto.getOrderId()))
                .switchIfEmpty(Mono.error(new PaymentNotFoundException("Order has already been paid.")))
                .flatMap(dto -> Mono.fromSupplier(() -> PaymentMappingHelper.map(paymentRepository.save(PaymentMappingHelper.map(dto)))))
                .onErrorResume(throwable -> {
                    log.error("Error saving payment: {}", throwable.getMessage());
                    return Mono.error(throwable);
                });
    }

    @Override
    public Mono<PaymentDto> update(PaymentDto paymentDto) {
        log.info("PaymentDto, service; update order");
        return Mono.fromSupplier(() -> paymentRepository.save(PaymentMappingHelper.map(paymentDto)))
                .map(PaymentMappingHelper::map);
    }

    @Override
    public Mono<PaymentDto> update(Integer paymentId, PaymentDto paymentDto) {
        log.info("OrderDto, service; update order with orderId");
        return findById(paymentId).flatMap(existingPaymentDto -> {
                    modelMapper.map(paymentDto, existingPaymentDto);
                    return Mono.fromSupplier(() -> paymentRepository.save(PaymentMappingHelper.map(existingPaymentDto)))
                            .map(PaymentMappingHelper::map);
                })
                .switchIfEmpty(Mono.error(new PaymentNotFoundException("Payment with id " + paymentId + " not found")));
    }

    @Override
    public Mono<Void> deleteById(Integer paymentId) {
        log.info("Void, service; delete payment by id");
        return Mono.fromRunnable(() -> paymentRepository.deleteById(paymentId));
    }

}