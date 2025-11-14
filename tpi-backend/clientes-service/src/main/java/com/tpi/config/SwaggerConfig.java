package com.tpi.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;

@Configuration
public class SwaggerConfig {
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                    .title("Clientes API Rest")
                    .version("1.0")
                    .description("API para gestión de clientes")
                    .contact(new Contact()
                        .name("Grupo")
                        .email("lucascallamullo@hotmail.com"))
                    .license(new License()
                        .name("Apache 2.0")
                        .url("http://springdoc.org")));
    }
}