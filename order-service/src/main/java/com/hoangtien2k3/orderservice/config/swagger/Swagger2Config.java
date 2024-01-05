//package com.hoangtien2k3.orderservice.config.swagger;
//
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.web.servlet.config.annotation.EnableWebMvc;
//import springfox.documentation.builders.ApiInfoBuilder;
//import springfox.documentation.builders.PathSelectors;
//import springfox.documentation.builders.RequestHandlerSelectors;
//import springfox.documentation.service.ApiInfo;
//import springfox.documentation.service.Contact;
//import springfox.documentation.spi.DocumentationType;
//import springfox.documentation.spring.web.plugins.Docket;
//import springfox.documentation.swagger2.annotations.EnableSwagger2;
//
//@Configuration
//@EnableSwagger2
//public class Swagger2Config {
//    @Bean
//    public Docket api() {
//        return new Docket(DocumentationType.SWAGGER_2).select()
//                .apis(RequestHandlerSelectors.basePackage("com.hoangtien2k3.orderservice.api"))
//                .paths(PathSelectors.any())
//                .build()
//                .apiInfo(apiEndPointsInfo());
//    }
//
//    private ApiInfo apiEndPointsInfo() {
//        return new ApiInfoBuilder()
//                .title("ORDER-SERVICE API")
//                .description("API Documentation's ORDER-SERVICE")
//                .contact(new Contact("hoangtien2k3", "https://hoangtien2k3qx1.github.io/", "hoangtien2k3qx1@gmail.com"))
//                .license("Apache 2.0")
//                .licenseUrl("http://www.apache.org/licenses/LICENSE-2.0.html")
//                .version("1.0.0")
//                .build();
//    }
//}