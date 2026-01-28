package com.hoangtien2k3.inventoryservice.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Value("${server.port:8080}")
    private String serverPort;

    @Value("${spring.application.name:inventory-service}")
    private String applicationName;

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(apiInfo())
                .servers(getServers());
    }

    private Info apiInfo() {
        return new Info()
                .title("INVENTORY SERVICE API")
                .description("""
                        ## Inventory Service API Documentation
                        
                        This service handles all inventory-related operations including:
                        - Product inventory management
                        - Stock level tracking
                        - Inventory updates and synchronization
                        - Product availability checks
                        
                        ### Features
                        - Real-time inventory tracking
                        - Multi-warehouse support
                        - Stock alerts and notifications
                        - Integration with order and product services
                        """)
                .version("1.0.0")
                .contact(new Contact()
                        .name("hoangtien2k3")
                        .url("https://hoangtien2k3qx1.github.io/")
                        .email("hoangtien2k3qx1@gmail.com"))
                .license(new License()
                        .name("Apache 2.0")
                        .url("http://www.apache.org/licenses/LICENSE-2.0.html"));
    }

    private List<Server> getServers() {
        Server localServer = new Server()
                .url("http://localhost:" + serverPort)
                .description("Local development server");

        Server productionServer = new Server()
                .url("https://api.yourdomain.com/inventory-service")
                .description("Production server");

        return List.of(localServer, productionServer);
    }
}