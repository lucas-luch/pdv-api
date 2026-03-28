package com.store.pdvapi.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI pdvOpenApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("PDV API")
                        .description("Documentação dos endpoints expostos pelo sistema de ponto de venda.")
                        .version("0.0.1"));
    }
}
