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
    
    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(authorize -> authorize
                .requestMatchers("/**").permitAll()  // ← TEMPORAL!


                /* COMENTAR temporalmente toda la seguridad 

                .requestMatchers(
                    "/swagger-ui.html",
                    "/swagger-ui/**", 
                    "/v3/api-docs/**",
                    "/swagger-resources/**",
                    "/webjars/**"
                ).permitAll()

                // ✅ PERMITIR endpoints públicos de prueba
                .requestMatchers("/api/v1/solicitudes/publico").permitAll()
                .requestMatchers("/api/v1/solicitudes/health").permitAll()
                
                // Los demás endpoints requieren autenticación
                .requestMatchers("/api/v1/solicitudes/**").authenticated()
                .requestMatchers("/api/v1/contenedores/**").authenticated()
                
                .anyRequest().authenticated()
                */
            )
            .oauth2ResourceServer(oauth2 -> 
                oauth2.jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter()))
            );
        
        return http.build();
    }
    
    // Bean personalizado para convertir un JWT en un token de autenticación de Spring Security
    @Bean
    Converter<Jwt, AbstractAuthenticationToken> jwtAuthenticationConverter() {
        return new Converter<Jwt, AbstractAuthenticationToken>() {
            @SuppressWarnings("null")
            @Override
            public AbstractAuthenticationToken convert(Jwt jwt) {
                // Extrae la información de roles del claim "realm_access" del JWT
                Map<String, List<String>> realmAccess = jwt.getClaim("realm_access");
                
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
}