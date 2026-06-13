package com.ecommerce.commonlib.autoconfigure;

import com.ecommerce.commonlib.openapi.OpenApiFactory;
import com.ecommerce.commonlib.openapi.OpenApiProperties;
import io.swagger.v3.oas.models.OpenAPI;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

/**
 * Publishes a default {@link OpenAPI} bean (Bearer-JWT scheme + service metadata).
 * Service overrides by simply declaring its own {@code OpenAPI} bean.
 */
@AutoConfiguration
@ConditionalOnClass(OpenAPI.class)
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
@EnableConfigurationProperties(OpenApiProperties.class)
public class OpenApiAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean(OpenAPI.class)
    public OpenAPI commonOpenAPI(OpenApiProperties props) {
        return OpenApiFactory.build(props);
    }
}
