package com.tpi.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
// import org.springframework.http.MediaType;
import org.springframework.web.client.RestClient;

@Configuration
public class RestClientConfig {
    private static final String LOGISTICA_URL = "http://logistica-service:8084";
    private static final String CLIENTES_URL = "http://clientes-service:8082";
    
    @Bean 
    public RestClient logisticaRestClient() {   // Bean llamado "logisticaRestClient"
        return RestClient.builder()
            .baseUrl(LOGISTICA_URL)
            // .defaultHeader("Accept", MediaType.APPLICATION_JSON_VALUE) // HEADER POR DEFECTO -> AGREGADO
            .build();
    }

    @Bean 
    public RestClient clientesRestClient() {   // Bean llamado "clientesRestClient"
        return RestClient.builder()
            .baseUrl(CLIENTES_URL)
            // .defaultHeader("Accept", MediaType.APPLICATION_JSON_VALUE) // HEADER POR DEFECTO -> AGREGADO
            .build();
    }
}
