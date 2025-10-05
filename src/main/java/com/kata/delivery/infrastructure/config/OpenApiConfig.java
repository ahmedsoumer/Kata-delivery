package com.kata.delivery.infrastructure.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * OpenAPI/Swagger configuration for WebFlux
 */
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI deliveryOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Delivery Kata API - Advanced Architecture")
                        .description("Reactive REST API with Hexagonal Architecture, DDD, and Event-Driven Design\n\n" +
                                "**Architecture Highlights:**\n" +
                                "- Hexagonal Architecture (Ports & Adapters)\n" +
                                "- Domain-Driven Design (Aggregates, Value Objects, Domain Events)\n" +
                                "- Event-Driven Architecture with Kafka\n" +
                                "- Reactive Programming with WebFlux (Project Reactor)\n" +
                                "- R2DBC for reactive database access")
                        .version("2.0.0")
                        .contact(new Contact()
                                .name("Kata Delivery Team")
                                .email("contact@kata-delivery.com"))
                        .license(new License()
                                .name("MIT License")
                                .url("https://opensource.org/licenses/MIT")));
    }
}
