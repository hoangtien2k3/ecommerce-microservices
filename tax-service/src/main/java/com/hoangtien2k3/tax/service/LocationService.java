package com.hoangtien2k3.tax.service;

import com.hoangtien2k3.tax.config.ServiceUrlConfig;
import com.hoangtien2k3.tax.viewmodel.location.StateOrProvinceAndCountryGetNameVm;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class LocationService {
    private final RestTemplate restTemplate;
    private final ServiceUrlConfig serviceUrlConfig;

    @Retry(name = "restApi")
    @CircuitBreaker(name = "restCircuitBreaker", fallbackMethod = "handleLocationNameListFallback")
    public List<StateOrProvinceAndCountryGetNameVm> getStateOrProvinceAndCountryNames(List<Long> stateOrProvinceIds) {
        // Convert List to array for query params
        Long[] idsArray = stateOrProvinceIds.toArray(new Long[0]);
        
        final URI url = UriComponentsBuilder.fromHttpUrl(serviceUrlConfig.location())
                .path("/backoffice/state-or-provinces/state-country-names")
                .queryParam("stateOrProvinceIds", (Object[]) idsArray)
                .build()
                .toUri();
        
        final Jwt jwt = (Jwt) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(jwt.getTokenValue());
        HttpEntity<?> entity = new HttpEntity<>(headers);
        
        return restTemplate.exchange(
                url,
                HttpMethod.GET,
                entity,
                new ParameterizedTypeReference<List<StateOrProvinceAndCountryGetNameVm>>() {}
        ).getBody();
    }

    protected List<StateOrProvinceAndCountryGetNameVm> handleLocationNameListFallback(Throwable throwable) {
        // Log error (use proper logger in production)
        System.err.println("Circuit breaker fallback triggered: " + throwable.getMessage());
        // Return empty list as fallback
        return Collections.emptyList();
    }
}