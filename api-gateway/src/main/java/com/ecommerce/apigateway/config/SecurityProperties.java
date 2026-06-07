package com.ecommerce.apigateway.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

@Setter
@Getter
@Component
@ConfigurationProperties(prefix = "security")
public class SecurityProperties {

    private PublicPaths publicPaths = new PublicPaths();

    @Setter
    public static class PublicPaths {
        private List<String> auth = List.of();
        private List<String> storefront = List.of();
        private List<String> swagger = List.of();

        public String[] getAuth()       { return auth.toArray(String[]::new); }
        public String[] getStorefront() { return storefront.toArray(String[]::new); }
        public String[] getSwagger()    { return swagger.toArray(String[]::new); }
    }
}
