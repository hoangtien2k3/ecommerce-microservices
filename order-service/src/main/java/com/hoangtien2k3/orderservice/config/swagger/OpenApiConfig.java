package com.hoangtien2k3.orderservice.config.swagger;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Value("${server.port:8080}")
    private String serverPort;

    @Value("${spring.application.name:order-service}")
    private String applicationName;

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(apiInfo())
                .addSecurityItem(new SecurityRequirement().addList("Bearer Authentication"))
                .components(new Components()
                        .addSecuritySchemes("Bearer Authentication", createBearerSecurityScheme()))
                .servers(getServers());
    }

    private Info apiInfo() {
        return new Info()
                .title("ORDER SERVICE API")
                .description("""
                        ## Order Service API Documentation
                        
                        This service handles all order-related operations including:
                        - Order creation and management
                        - Order tracking and status updates
                        - Integration with cart and payment services
                        - Order history and reporting
                        
                        ### Authentication
                        All endpoints require JWT Bearer token authentication.
                        Include the token in the Authorization header as: `Bearer {token}`
                        """)
                .version("1.0.0")
                .contact(new Contact()
                        .name("hoangtien2k3")
                        .url("https://hoangtien2k3qx1.github.io/")
                        .email("hoangtien2k3qx1@gmail.com"))
                .license(new License()
                        .name("MIT License")
                        .url("https://mit-license.org/"))
                .termsOfService("https://hoangtien2k3qx1.github.io/terms");
    }

    private SecurityScheme createBearerSecurityScheme() {
        return new SecurityScheme()
                .name("Bearer Authentication")
                .type(SecurityScheme.Type.HTTP)
                .scheme("bearer")
                .bearerFormat("JWT")
                .description("Enter your JWT token in the format: Bearer {token}")
                .in(SecurityScheme.In.HEADER);
    }

    private List<Server> getServers() {
        Server localServer = new Server()
                .url("http://localhost:" + serverPort)
                .description("Local development server");

        Server productionServer = new Server()
                .url("https://api.yourdomain.com/order-service")
                .description("Production server");

        return List.of(localServer, productionServer);
    }
}