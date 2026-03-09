package com.zyloerp.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
public class CorsConfig {

    // Em application-local.yml: cors.allowed-origins=http://localhost:3000,http://localhost:5500
    // Em application-prod.yml:  cors.allowed-origins=https://app.zyloerp.com
    @Value("${cors.allowed-origins}")
    private List<String> allowedOrigins;

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();

        // Origins permitidas (vem do application.yml por profile)
        config.setAllowedOrigins(allowedOrigins);

        // Métodos HTTP liberados
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));

        // Headers que o frontend pode enviar
        config.setAllowedHeaders(List.of(
                "Authorization",       // Bearer token
                "Content-Type",        // application/json
                "Accept",
                "X-Requested-With",
                "Cache-Control"
        ));

        // Headers que o frontend pode ler na resposta
        config.setExposedHeaders(List.of(
                "Authorization",
                "X-Total-Count"        // útil para paginação
        ));

        // Permite envio de cookies/credentials (necessário para alguns casos)
        config.setAllowCredentials(true);

        // Cache do preflight OPTIONS por 1 hora (evita chamada dupla a cada request)
        config.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/api/**", config);
        return source;
    }
}