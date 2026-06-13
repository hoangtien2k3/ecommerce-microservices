package com.ecommerce.commonlib.autoconfigure;

import com.ecommerce.commonlib.web.cors.CorsAutoConfigurer;
import com.ecommerce.commonlib.web.cors.CorsProperties;
import com.ecommerce.commonlib.web.exception.ApiExceptionHandler;
import com.ecommerce.commonlib.web.filter.CorrelationIdFilter;
import jakarta.servlet.Servlet;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Wires every web-tier cross-cutter for servlet-stack services:
 * correlation id filter, CORS, the global exception handler.
 *
 * <p>Skipped automatically for reactive runtimes (api-gateway) because of
 * {@code @ConditionalOnWebApplication(type = SERVLET)}.</p>
 */
@AutoConfiguration(after = I18nAutoConfiguration.class)
@ConditionalOnClass({Servlet.class, WebMvcConfigurer.class})
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
@EnableConfigurationProperties(CorsProperties.class)
public class WebAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty(prefix = "ecommerce.web.correlation", name = "enabled", havingValue = "true", matchIfMissing = true)
    public CorrelationIdFilter correlationIdFilter() {
        return new CorrelationIdFilter();
    }

    @Bean
    @ConditionalOnMissingBean(ApiExceptionHandler.class)
    @ConditionalOnProperty(prefix = "ecommerce.web.exception-handler", name = "enabled", havingValue = "true", matchIfMissing = true)
    public ApiExceptionHandler apiExceptionHandler() {
        return new ApiExceptionHandler();
    }

    @Bean
    @ConditionalOnMissingBean(CorsAutoConfigurer.class)
    public CorsAutoConfigurer commonCorsConfigurer(CorsProperties props) {
        return new CorsAutoConfigurer(props);
    }
}
