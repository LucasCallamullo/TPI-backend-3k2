package com.tpi.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;

@Configuration
public class SwaggerConfig {
    
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                    .title("Solicitudes API Rest")
                    .version("1.0")
                    .description("API para gestión de Solicitudes y Contenedores")
                    .contact(new Contact()
                        .name("Grupo")
                        .email("lucascallamullo@hotmail.com"))
                    .license(new License()
                        .name("Apache 2.0")
                        .url("http://springdoc.org")))
                .addServersItem(new Server()
                    .url("http://localhost:8080/solicitudes-service")  // ← URL del Gateway
                    .description("API Gateway"))
                .addServersItem(new Server()
                    .url("http://localhost:8083")  // ← URL directa (backup)
                    .description("Direct access"));
    }
}