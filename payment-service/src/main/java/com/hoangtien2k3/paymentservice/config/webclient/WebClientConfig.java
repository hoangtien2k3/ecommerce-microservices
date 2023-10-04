package com.hoangtien2k3.paymentservice.config.webclient;

import org.springframework.context.annotation.Bean;
import org.springframework.web.reactive.function.client.WebClient;

public class WebClientConfig {

//    @Bean
//    public WebClient.Builder webClientBuilder() {
//        return WebClient.builder()
//                .uriBuilderFactory(new DefaultUriBuilderFactory(baseUrl))
//                .codecs(configurer -> configurer.defaultCodecs().maxInMemorySize(16 * 1024 * 1024)); // Điều chỉnh kích thước bộ nhớ đệm mặc định nếu cần
//    }

//    @Bean
//    public WebClient webClient() {
//        return WebClient.builder().build();
//    }

    @Bean
    public WebClient.Builder webClientBuilder() {
        return WebClient.builder();
    }

}
