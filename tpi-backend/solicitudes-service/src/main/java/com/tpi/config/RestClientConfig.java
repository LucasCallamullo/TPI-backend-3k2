package com.tpi.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class RestClientConfig {
    private static final String LOGISTICA_URL = "http://logistica-service:8084";
    // private static final String LOGISTICA_URL = "http://logistica-service:8084"
    
    @Bean 
    public RestClient logisticaRestClient() {   // Bean llamado "logisticaRestClient"
        return RestClient.builder()
            .baseUrl(LOGISTICA_URL)
            .build();
    }
}