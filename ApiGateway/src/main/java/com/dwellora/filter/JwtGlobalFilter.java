package com.dwellora.filter;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import reactor.core.publisher.Mono;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Component
public class JwtGlobalFilter implements GlobalFilter, Ordered {

    @Value("${jwt.secret}")
    private String secret;

    private static final List<String> OPEN_PATHS = List.of(
            "/users/login",
            "/users/activate",
            "/onboarding/request"
    );

    @Override
    public Mono<Void> filter(
            ServerWebExchange exchange,
            GatewayFilterChain chain) {

        ServerHttpRequest request = exchange.getRequest();

        /*
         * CORS preflight should not be authenticated.
         */
        if (request.getMethod() == HttpMethod.OPTIONS) {
            return chain.filter(exchange);
        }

        String path = request.getURI().getPath();

        /*
         * Public endpoints.
         */
        if (OPEN_PATHS.contains(path)) {
            return chain.filter(exchange);
        }

        /*
         * Every other request must have a Bearer token.
         */
        String authHeader = request.getHeaders().getFirst("Authorization");
        if (authHeader == null ||
                !authHeader.startsWith("Bearer ")) {
            return unauthorized(exchange);
        }

        String token = authHeader.substring(7);
        try {
            SecretKey key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));

            Claims claims = Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

            Long userId = claims.get("userId", Long.class);

            String role = claims.get("role", String.class);

            Long apartmentId = claims.get("apartmentId", Long.class);

            /*
             * Basic claim validation.
             */
            if (userId == null || role == null) {
                return unauthorized(exchange);
            }

            /*
             * IMPORTANT:
             *
             * Remove any client-supplied internal headers first.
             *
             * Then Gateway writes the trusted values from
             * the verified JWT.
             */
            ServerHttpRequest.Builder mutatedBuilder =
                    request.mutate()
                            .headers(headers -> {
                                headers.remove("X-User-Id");
                                headers.remove("X-User-Role");
                                headers.remove("X-Apartment-Id");
                            })
                            .header("X-User-Id", String.valueOf(userId))
                            .header("X-User-Role", role);

            /*
             * PLATFORM_ADMIN has no apartment.
             *
             * Managers/residents should have one.
             */
            if (apartmentId != null) {
                mutatedBuilder.header("X-Apartment-Id", String.valueOf(apartmentId)
                );
            }

            ServerHttpRequest mutated = mutatedBuilder.build();
            return chain.filter(exchange.mutate().request(mutated).build()
            );

        } catch (Exception e) {
            return unauthorized(exchange);
        }
    }

    private Mono<Void> unauthorized(ServerWebExchange exchange) {

        exchange.getResponse()
                .setStatusCode(HttpStatus.UNAUTHORIZED);
        return exchange.getResponse().setComplete();
    }

    @Override
    public int getOrder() {
        return -1;
    }
}