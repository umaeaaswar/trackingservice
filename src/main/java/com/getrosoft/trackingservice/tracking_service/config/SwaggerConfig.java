package com.getrosoft.trackingservice.tracking_service.config;

import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.OpenAPI;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    private static final String SERVICE_DESCRIPTION = "The Tracking Service is a backend application designed to generate and manage tracking numbers for shipments. It provides REST APIs to handle tracking number generation, retrieval of tracking details, and validation of request parameters. The service is designed to be scalable, efficient, and extensible for logistics and e-commerce platforms.";

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Tracking Service API")
                        .version("1.0")
                        .description(SERVICE_DESCRIPTION)
                        .license(new License().name("Apache 2.0").url("http://springdoc.org")));
    }
}
