package com.hoangtien2k3.paymentservice.service.impl;

import com.hoangtien2k3.paymentservice.constant.AppConstant;
import com.hoangtien2k3.paymentservice.dto.OrderDto;
import com.hoangtien2k3.paymentservice.dto.PaymentDto;
import com.hoangtien2k3.paymentservice.exception.wrapper.PaymentNotFoundException;
import com.hoangtien2k3.paymentservice.helper.PaymentMappingHelper;
import com.hoangtien2k3.paymentservice.repository.PaymentRepository;
import com.hoangtien2k3.paymentservice.service.PaymentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.transaction.Transactional;
import java.util.Collections;
import java.util.List;

@Transactional
@Slf4j
@Service
public class PaymentServiceImpl implements PaymentService {
    @Autowired
    private RestTemplate restTemplate;
//    @Autowired
//    private WebClient webClient;

    private final PaymentRepository paymentRepository;
    private final DiscoveryClient discoveryClient;
    private final WebClient.Builder webClientBuilder;

    @Autowired
    public PaymentServiceImpl(PaymentRepository paymentRepository,
                              DiscoveryClient discoveryClient,
                              WebClient.Builder webClientBuilder) {
        this.paymentRepository = paymentRepository;
        this.discoveryClient = discoveryClient;
        this.webClientBuilder = webClientBuilder;
    }

    @Override
    public List<PaymentDto> findAll() {
        log.info("PaymentDto List, service; fetch all payments");
        return this.paymentRepository.findAll()
                .stream()
                .map(PaymentMappingHelper::map) // Map Payment -> PaymentDto
                .peek(p -> p.setOrderDto(
                        restTemplate.getForObject(
                                AppConstant.DiscoveredDomainsApi.ORDER_SERVICE_API_URL + "/" + p.getOrderDto().getOrderId(), OrderDto.class)
                        )
                )
                .distinct()
                .toList();
    }

    @Override
    public Mono<List<String>> callServiceB(String serviceBUrl) {
        WebClient webClient = webClientBuilder.baseUrl(serviceBUrl).build();

        Flux<String> responseFlux = webClient
                .get()
                .retrieve()
                .bodyToFlux(String.class);

        return responseFlux
                .collectList()
                .map(responseList -> responseList != null ? responseList : Collections.emptyList());
    }

    @Override
    public PaymentDto findById(Integer paymentId) {
        log.info("PaymentDto, service; fetch payment by id");
        return this.paymentRepository.findById(paymentId)
                .map(PaymentMappingHelper::map)
                .map(p -> {
                    p.setOrderDto(restTemplate.getForObject(AppConstant.DiscoveredDomainsApi
                            .ORDER_SERVICE_API_URL + "/" + p.getOrderDto().getOrderId(), OrderDto.class));
                    return p;
                })
                .orElseThrow(() -> new PaymentNotFoundException(String.format("Payment with id[%d] not found", paymentId)));
    }

    @Override
    public PaymentDto save(PaymentDto paymentDto) {
        log.info("PaymentDto, service; save payment");
        return PaymentMappingHelper.map(this.paymentRepository
                .save(PaymentMappingHelper.map(paymentDto)));
    }

    @Override
    public PaymentDto update(PaymentDto paymentDto) {
        log.info("PaymentDto, service; update payment");
        return PaymentMappingHelper.map(this.paymentRepository
                .save(PaymentMappingHelper.map(paymentDto)));
    }

    @Override
    public void deleteById(Integer paymentId) {
        log.info("Void, service; delete payment by id");
        this.paymentRepository.deleteById(paymentId);
    }

}
