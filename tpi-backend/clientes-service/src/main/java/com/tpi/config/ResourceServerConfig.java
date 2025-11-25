package com.tpi.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.web.SecurityFilterChain;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

// Marca esta clase como una clase de configuración de Spring
@Configuration
// Habilita la configuración de seguridad web de Spring Security
@EnableWebSecurity
public class ResourceServerConfig {
    
    // Define el filtro de seguridad principal que procesa todas las requests HTTP
    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(authorize -> authorize
                // =============================================
                // TEMPORAL: PERMITIR SIEMPRE para pruebas
                // =============================================
                // .requestMatchers("/**").permitAll()  // ← TEMPORAL!

                /* COMENTAR temporalmente toda la seguridad  */
                // Permitir acceso público a Swagger/OpenAPI
                .requestMatchers(
                    "/swagger-ui.html",
                    "/swagger-ui/**", 
                    "/v3/api-docs/**",
                    "/swagger-resources/**",
                    "/webjars/**"
                ).permitAll()

                // endpoints de pruebas ver si esta actuator en el pom.xml
                .requestMatchers("/actuator/health").permitAll()
                .requestMatchers("/actuator/info").permitAll()
                .requestMatchers("/clientes/sincronizar").permitAll()
                
                // Endpoints PÚBLICOS (sin autenticación)
                .requestMatchers("/api/v1/clientes/publico").permitAll()

                // Endpoints con ROL CLIENTE
                .requestMatchers("/api/v1/clientes/hola-clientes").hasRole("CLIENTE")

                // Endpoints con ROL ADMIN
                .requestMatchers("/api/v1/clientes/hola-admin").hasRole("ADMIN")

                // Endpoints con MÚLTIPLES ROLES
                .requestMatchers("/api/v1/clientes/multi-rol").hasAnyRole("CLIENTE", "OPERADOR", "ADMIN")

                // Endpoints AUTENTICADOS (cualquier usuario logueado)
                .requestMatchers("/api/v1/clientes/mi-perfil").authenticated()

                // Endpoints PÚBLICOS (sin autenticación) sincroniza kleycloackId en la base de datos de clientes
                .requestMatchers("/api/v1/clientes/sincronizar").permitAll()

                // Endpoints para CLIENTE (usuario autenticado)
                .requestMatchers("/api/v1/clientes/me").hasRole("CLIENTE")

                // Endpoints para ADMIN (solo administradores)
                .requestMatchers("/api/v1/clientes/{id}").hasAnyRole("CLIENTE", "ADMIN")
                .requestMatchers("/api/v1/clientes").hasRole("ADMIN")

                .anyRequest().authenticated() 
            )
            //  DESHABILITAR seguridad OAuth2
            .oauth2ResourceServer(oauth2 -> 
                oauth2.jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter()))
            )
            ;
        
        return http.build();
    }
    
    // =============================================
    // CONVERSOR PERSONALIZADO JWT -> AUTHENTICATION
    // =============================================
    // Bean personalizado para convertir un JWT en un token de autenticación de Spring Security
    // NOTA: Este conversor extrae los roles del claim "realm_access" de Keycloak
    @Bean
    Converter<Jwt, AbstractAuthenticationToken> jwtAuthenticationConverter() {
        return new Converter<Jwt, AbstractAuthenticationToken>() {
            @SuppressWarnings("null")
            @Override
            public AbstractAuthenticationToken convert(Jwt jwt) {
                // Extrae la información de roles del claim "realm_access" del JWT
                Map<String, List<String>> realmAccess = jwt.getClaim("realm_access");

                // ✅ DEBUG: Ver los roles originales del token
                System.out.println("Roles del token: " + realmAccess.get("roles"));
                
                // Convierte los roles de Keycloak en autoridades de Spring Security
                List<GrantedAuthority> authorities = realmAccess.get("roles")
                    .stream()
                    // Convierte cada rol al formato ROLE_NOMBREROL (requerido por Spring Security)
                    .map(r -> String.format("ROLE_%s", r.toUpperCase()))
                    // Crea una instancia de GrantedAuthority para cada rol
                    .map(SimpleGrantedAuthority::new)
                    // Recoge todos los authorities en una lista
                    .collect(Collectors.toList());
                
                // Crea y retorna un token de autenticación con el JWT y los authorities
                return new JwtAuthenticationToken(jwt, authorities);
            }
        };
    }
    
    // =============================================
    // CONFIGURACIÓN ADICIONAL PARA CORRECCIÓN DE TESTS
    // =============================================
    // COMENTARIO: Los siguientes métodos son SOLO para hacer pasar los tests
    // En un ambiente real, Spring Boot crea automáticamente el JwtDecoder
    // usando la propiedad: spring.security.oauth2.resourceserver.jwt.issuer-uri
    
    /*
    // DESCOMENTAR ESTE MÉTODO SI LOS TESTS SIGUEN FALLANDO:
    @Bean
    public JwtDecoder jwtDecoder() {
        // Para tests, retornamos un JwtDecoder mockeado
        // En producción, Spring Boot crea automáticamente este bean
        // cuando la propiedad issuer-uri está configurada
        return token -> {
            // Retorna un JWT básico para pruebas
            return Jwt.withTokenValue("mock-token")
                    .header("alg", "none")
                    .claim("sub", "test-user")
                    .claim("realm_access", Map.of("roles", List.of("CLIENTE")))
                    .build();
        };
    }
    */
}