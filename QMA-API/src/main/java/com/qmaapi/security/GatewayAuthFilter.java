package com.qmaapi.security;

import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpCookie;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.List;

@Component
public class GatewayAuthFilter implements GlobalFilter, Ordered {
    private static final String AUTH_COOKIE_NAME = "QMA_AUTH_TOKEN";
    private static final String USER_EMAIL_HEADER = "X-User-Email";
    
    private static final List<String> PUBLIC_PATHS = List.of(
            "/api/v1/auth",
            "/api/v1/quantities",
            "/login/oauth2",
            "/oauth2",
            "/swagger",
            "/v3/api-docs",
            "/api-docs"
    );

    private final GatewayJwtService gatewayJwtService;

    public GatewayAuthFilter(GatewayJwtService gatewayJwtService) {
        this.gatewayJwtService = gatewayJwtService;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String path = exchange.getRequest().getURI().getPath();
        String token = resolveToken(exchange.getRequest());

        String email = null;
        if (token != null && !token.isBlank()) {
            email = gatewayJwtService.extractEmailIfValid(token);
        }

        // Block unauthenticated requests for secured paths
        if (requiresAuthentication(path, exchange.getRequest().getMethod())) {
            if (token == null || token.isBlank()) {
                return unauthorized(exchange.getResponse(), "Missing authentication token");
            }
            if (email == null) {
                return unauthorized(exchange.getResponse(), "Invalid or expired authentication token");
            }
        }

        // Inject user identity for downstream services
        if (email != null) {
            ServerHttpRequest request = exchange.getRequest().mutate()
                    .header(USER_EMAIL_HEADER, email)
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                    .build();
            
            return chain.filter(exchange.mutate().request(request).build());
        }

        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        return -100;
    }

    private boolean requiresAuthentication(String path, HttpMethod method) {
        if (HttpMethod.OPTIONS.equals(method)) {
            return false;
        }

        boolean isPublicPath = PUBLIC_PATHS.stream().anyMatch(path::startsWith);
        if (isPublicPath) {
            return false;
        }
        
        return path.startsWith("/api/v1/users") || path.startsWith("/api/v1/history");
    }

    private String resolveToken(ServerHttpRequest request) {
        String authorization = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        if (authorization != null && authorization.startsWith("Bearer ")) {
            return authorization.substring(7).trim();
        }

        HttpCookie authCookie = request.getCookies().getFirst(AUTH_COOKIE_NAME);
        if (authCookie != null) {
            return authCookie.getValue();
        }
        return null;
    }

    private Mono<Void> unauthorized(ServerHttpResponse response, String message) {
        response.setStatusCode(HttpStatus.UNAUTHORIZED);
        response.getHeaders().add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
        
        String errorJson = "{\"message\":\"" + message + "\"}";
        DataBuffer buffer = response.bufferFactory().wrap(errorJson.getBytes(StandardCharsets.UTF_8));
        
        return response.writeWith(Mono.just(buffer));
    }
}
