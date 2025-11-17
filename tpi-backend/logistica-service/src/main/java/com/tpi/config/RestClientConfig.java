package com.tpi.config;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class RestClientConfig {
    private static final String SOLICITUDES_URL = "http://solicitudes-service:8082";
    // private static final String SOLICITUDES_URL = "http://localhost:8082"

    private static final String ROUTING_URL = "http://routing-service:8088";
    // private static final String ROUTING_URL = "http://localhost:8088"  // <- Corregí el comentario
    
    @Bean
    public RestClient contenedorRestClient() {     // Bean llamado "solicitudesRestClient"
        return RestClient.builder()
            .baseUrl(SOLICITUDES_URL)
            .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .build();
    }

    @Bean
    public RestClient solicitudesRestClient() {  // Bean llamado "solicitudRestClient"
        return RestClient.builder()
            .baseUrl(SOLICITUDES_URL)
            .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
            .build();
    }

    @Bean
    public RestClient routingRestClient() {     // Bean llamado "routingRestClient"  // <- Corregí el comentario
        return RestClient.builder()
            .baseUrl(ROUTING_URL)
            .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .build();
    }
}