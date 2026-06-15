package com.pm.apigateway.filters;

import com.pm.apigateway.security.JwtUtil;
import io.jsonwebtoken.Claims;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

@Component
public class JwtValidationGatewayFilterFactory
        extends AbstractGatewayFilterFactory<Object> {

    private final JwtUtil jwtUtil;

    public JwtValidationGatewayFilterFactory(
            JwtUtil jwtUtil) {

        this.jwtUtil = jwtUtil;
    }

    @Override
    public GatewayFilter apply(
            Object config) {

        return (exchange, chain) -> {

            String authHeader =
                    exchange.getRequest()
                            .getHeaders()
                            .getFirst(
                                    HttpHeaders.AUTHORIZATION
                            );

            if (authHeader == null
                    || !authHeader.startsWith("Bearer ")) {

                exchange.getResponse()
                        .setStatusCode(
                                HttpStatus.UNAUTHORIZED
                        );

                return exchange.getResponse()
                        .setComplete();
            }

            String token =
                    authHeader.substring(7);

            if (!jwtUtil.validateToken(token)) {

                exchange.getResponse()
                        .setStatusCode(
                                HttpStatus.UNAUTHORIZED
                        );

                return exchange.getResponse()
                        .setComplete();
            }

            Claims claims =
                    jwtUtil.getClaims(token);

            String userId =
                    claims.get(
                            "userId",
                            String.class
                    );

            String role =
                    claims.get(
                            "role",
                            String.class
                    );

            String email =
                    claims.getSubject();

            var request =
                    exchange.getRequest()
                            .mutate()
                            .header(
                                    "X-User-Id",
                                    userId
                            )
                            .header(
                                    "X-Role",
                                    role
                            )
                            .header(
                                    "X-Email",
                                    email
                            )
                            .build();

            return chain.filter(
                    exchange.mutate()
                            .request(request)
                            .build()
            );
        };
    }
}