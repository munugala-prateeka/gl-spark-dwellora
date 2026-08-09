package com.dwellora.config;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/** Interceptor that forwards authentication headers across Feign HTTP requests. */
@Component
public class FeignAuthInterceptor implements RequestInterceptor {

    @Override
    public void apply(RequestTemplate template) {
        ServletRequestAttributes attributes =
                (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();

        if (attributes == null) {
            return;
        }

        HttpServletRequest request = attributes.getRequest();

        String authorization = request.getHeader("Authorization");
        if (authorization != null && !authorization.isBlank()) {
            template.header("Authorization", authorization);
        }

        String userId = request.getHeader("X-User-Id");
        if (userId != null && !userId.isBlank()) {
            template.header("X-User-Id", userId);
        }

        String role = request.getHeader("X-User-Role");
        if (role != null && !role.isBlank()) {
            template.header("X-User-Role", role);
        }

        String apartmentId = request.getHeader("X-Apartment-Id");
        if (apartmentId != null && !apartmentId.isBlank()) {
            template.header("X-Apartment-Id", apartmentId);
        }
    }
}