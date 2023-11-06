package com.hoangtien2k3.paymentservice.service.impl;

import com.hoangtien2k3.paymentservice.constant.AppConstant;
import com.hoangtien2k3.paymentservice.dto.OrderDto;
import com.hoangtien2k3.paymentservice.dto.PaymentDto;
import com.hoangtien2k3.paymentservice.entity.Payment;
import com.hoangtien2k3.paymentservice.exception.wrapper.PaymentNotFoundException;
import com.hoangtien2k3.paymentservice.helper.PaymentMappingHelper;
import com.hoangtien2k3.paymentservice.repository.PaymentRepository;
import com.hoangtien2k3.paymentservice.service.PaymentService;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.transaction.Transactional;

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
                .peek(p -> p.setOrderDto(restTemplate.getForObject(AppConstant.DiscoveredDomainsApi.ORDER_SERVICE_API_URL + "/" + p.getOrderDto().getOrderId(), OrderDto.class)))
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

    // set list payment
/*    @Override
    public List<PaymentDto> findAll() {
        List<Payment> payments = paymentRepository.findAll();

        // Sử dụng Flux để tạo một luồng các Mono<OrderDto> từ WebClient
        Flux<Mono<OrderDto>> orderDtos = Flux.fromIterable(payments)
                .map(payment -> webClientBuilder.get()
                        .uri("/{orderId}", payment.getOrderId())
                        .retrieve()
                        .bodyToMono(OrderDto.class));

        // Sử dụng flatMapSequential để chạy các yêu cầu đồng thời và chờ tất cả hoàn thành
        Flux<OrderDto> orderDtoFlux = Flux.mergeSequential(orderDtos);

        // Chuyển đổi Flux<OrderDto> thành danh sách
        List<OrderDto> orderDtoList = orderDtoFlux.collectList().block();

        // Map thông tin từ Payment và OrderDto vào PaymentDto
        return payments.stream()
                .map(payment -> {
                    PaymentDto paymentDto = PaymentMappingHelper.map(payment);
                    assert orderDtoList != null;
                    OrderDto orderDto = orderDtoList.stream()
                            .filter(o -> o.getOrderId().equals(payment.getOrderId()))
                            .findFirst()
                            .orElse(null);
                    paymentDto.setOrderDto(orderDto);
                    return paymentDto;
                })
                .collect(Collectors.toList());
    }*/

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


//    @Override
//    public PaymentDto findById(Integer paymentId) {
//        log.info("PaymentDto, service; fetch payment by id");
//        return this.paymentRepository.findById(paymentId)
//                .map(PaymentMappingHelper::map)
//                .map(p -> {
//                    OrderDto orderDto = retrieveOrderDetails(p.getOrderDto().getOrderId());
//                    p.setOrderDto(orderDto);
//                    return p;
//                })
//                .orElseThrow(() -> new PaymentNotFoundException(String.format("Payment with id[%d] not found", paymentId)));
//    }
//
//
//    private OrderDto retrieveOrderDetails(Integer orderId) {
//        String orderServiceUrl = Objects.requireNonNull(discoveryClient.getInstances("ORDER-SERVICE"))
//                .stream()
//                .findFirst()
//                .map(serviceInstance -> serviceInstance.getUri().toString())
//                .orElseThrow(() -> new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "Order service not available"));
//
//        WebClient orderServiceClient = webClientBuilder.baseUrl(orderServiceUrl).build();
//
//        Mono<OrderDto> orderDtoMono = orderServiceClient
//                .method(HttpMethod.GET)
//                .uri("/" + orderId)
//                .accept(MediaType.APPLICATION_JSON)
//                .retrieve()
//                .onStatus(
//                        HttpStatus::is4xxClientError,
//                        response -> Mono.error(new ResponseStatusException(response.statusCode(), "Order service responded with error")))
//                .onStatus(
//                        HttpStatus::is5xxServerError,
//                        response -> Mono.error(new ResponseStatusException(response.statusCode(), "Order service encountered an error")))
//                .bodyToMono(OrderDto.class);
//
//        return orderDtoMono.block(); // Blocking, you may want to use reactive patterns throughout your application
//    }

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
