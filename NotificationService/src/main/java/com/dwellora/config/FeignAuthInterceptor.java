package com.dwellora.config;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * Feign request interceptor that forwards authentication and context headers from the incoming HTTP request.
 */
@Component
public class FeignAuthInterceptor implements RequestInterceptor {

    /**
     * Intercepts Feign client requests to propagate security and user context headers.
     */
    @Override
    public void apply(RequestTemplate template) {

        ServletRequestAttributes attributes =
                (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();

        if (attributes == null) {
            template.header("X-User-Id", "0");
            template.header("X-User-Role", "MANAGER");
            template.header("X-Apartment-Id", "0");
            return;
        }

        HttpServletRequest request = attributes.getRequest();

        // Authorization
        String authorization = request.getHeader("Authorization");

        if (authorization != null && !authorization.isBlank()) {
            template.header("Authorization", authorization);
        }

        // User ID
        String userId = request.getHeader("X-User-Id");

        if (userId != null && !userId.isBlank()) {
            template.header("X-User-Id", userId);
        }

        // User Role
        String role = request.getHeader("X-User-Role");

        if (role != null && !role.isBlank()) {
            template.header("X-User-Role", role);
        }

        // Apartment ID
        String apartmentId = request.getHeader("X-Apartment-Id");

        if (apartmentId != null && !apartmentId.isBlank()) {
            template.header("X-Apartment-Id", apartmentId);
        }
    }
}