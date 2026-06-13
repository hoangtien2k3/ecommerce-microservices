package com.ecommerce.commonlib.autoconfigure;

import com.ecommerce.commonlib.constants.MdcKey;
import com.ecommerce.commonlib.web.filter.CorrelationIdFilter;
import org.apache.hc.client5.http.config.ConnectionConfig;
import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManager;
import org.apache.hc.core5.util.TimeValue;
import org.apache.hc.core5.util.Timeout;
import org.slf4j.MDC;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.lang.NonNull;
import org.springframework.web.client.RestClient;

import java.io.IOException;

/**
 * Provides a shared {@link RestClient.Builder} backed by Apache HttpClient 5
 * with a tunable connection pool.
 *
 * <p>All knobs are configurable via {@code ecommerce.rest-client.*} properties —
 * no hardcoded values. See {@link RestClientProperties} for defaults and docs.</p>
 *
 * <p>Services that need Eureka load-balancing should declare their own
 * {@code @LoadBalanced RestClient.Builder} bean — this auto-config backs off
 * via {@code @ConditionalOnMissingBean}.</p>
 */
@AutoConfiguration(after = WebAutoConfiguration.class)
@ConditionalOnClass({RestClient.class, CloseableHttpClient.class})
@EnableConfigurationProperties(RestClientProperties.class)
public class RestClientAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public PoolingHttpClientConnectionManager connectionManager(RestClientProperties props) {
        PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager();
        cm.setMaxTotal(props.getPool().getMaxTotal());
        cm.setDefaultMaxPerRoute(props.getPool().getMaxPerRoute());
        cm.setDefaultConnectionConfig(ConnectionConfig.custom()
                .setConnectTimeout(Timeout.of(props.getConnectTimeout()))
                .setSocketTimeout(Timeout.of(props.getReadTimeout()))
                .setTimeToLive(TimeValue.of(props.getPool().getKeepAlive()))
                .build());
        return cm;
    }

    @Bean
    @ConditionalOnMissingBean
    public CloseableHttpClient httpClient(PoolingHttpClientConnectionManager cm,
                                          RestClientProperties props) {
        RequestConfig requestConfig = RequestConfig.custom()
                .setConnectionRequestTimeout(Timeout.of(props.getConnectTimeout()))
                .setResponseTimeout(Timeout.of(props.getReadTimeout()))
                .build();
        return HttpClients.custom()
                .setConnectionManager(cm)
                .setDefaultRequestConfig(requestConfig)
                .evictExpiredConnections()
                .evictIdleConnections(TimeValue.of(props.getPool().getEvictIdleAfter()))
                .build();
    }

    @Bean
    @ConditionalOnMissingBean(RestClient.Builder.class)
    public RestClient.Builder restClientBuilder(CloseableHttpClient httpClient) {
        HttpComponentsClientHttpRequestFactory factory =
                new HttpComponentsClientHttpRequestFactory(httpClient);
        return RestClient.builder()
                .requestFactory(factory)
                .requestInterceptor(new CorrelationIdPropagationInterceptor());
    }

    /**
     * Forwards X-Correlation-Id and X-Request-Id from the current MDC context
     * to outbound HTTP calls so distributed traces stay connected.
     */
    static class CorrelationIdPropagationInterceptor implements ClientHttpRequestInterceptor {
        @Override
        public @NonNull ClientHttpResponse intercept(
                @NonNull HttpRequest request,
                @NonNull byte[] body,
                @NonNull ClientHttpRequestExecution execution) throws IOException {
            String correlationId = MDC.get(MdcKey.CORRELATION_ID);
            String requestId = MDC.get(MdcKey.REQUEST_ID);
            if (correlationId != null) {
                request.getHeaders().set(CorrelationIdFilter.CORRELATION_ID_HEADER, correlationId);
            }
            if (requestId != null) {
                request.getHeaders().set(CorrelationIdFilter.REQUEST_ID_HEADER, requestId);
            }
            return execution.execute(request, body);
        }
    }
}
