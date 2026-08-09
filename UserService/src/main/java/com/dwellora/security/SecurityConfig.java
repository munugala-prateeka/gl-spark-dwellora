package com.dwellora.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Security configuration for configuring HTTP security, password encoding, and security filter chains.
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private final HeaderAuthFilter headerAuthFilter;

    public SecurityConfig(HeaderAuthFilter headerAuthFilter) {
        this.headerAuthFilter = headerAuthFilter;
    }

    /**
     * Configures the password encoder bean using BCrypt hashing.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Configures the security filter chain for HTTP requests, endpoint authorization, and exception handling.
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http.csrf(csrf -> csrf.disable())
                .sessionManagement(
                        sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(
                        auth ->
                                auth
                                        /*
                                         * These endpoints are public.
                                         * The Gateway also treats them as public.
                                         */
                                        .requestMatchers("/users/login", "/users/activate")
                                        .permitAll()
                                        /*
                                         * Everything else requires authentication.
                                         *
                                         * @PreAuthorize on the controller then
                                         * handles role-specific authorization.
                                         */
                                        .anyRequest()
                                        .authenticated())
                .exceptionHandling(
                        exception ->
                                exception
                                        .authenticationEntryPoint(
                                                (request, response, authException) -> {
                                                    response.setStatus(HttpStatus.UNAUTHORIZED.value());
                                                    response.setContentType("application/json");

                                                    response
                                                            .getWriter()
                                                            .write(
                                                                    """
                                                                    {
                                                                        "message": "Unauthorized",
                                                                        "details": "Authentication is required."
                                                                    }
                                                                    """);
                                                })
                                        .accessDeniedHandler(
                                                (request, response, accessDeniedException) -> {
                                                    response.setStatus(HttpStatus.FORBIDDEN.value());
                                                    response.setContentType("application/json");

                                                    response
                                                            .getWriter()
                                                            .write(
                                                                    """
                                                                    {
                                                                        "message": "Access Denied",
                                                                        "details": "You do not have permission to access this resource."
                                                                    }
                                                                    """);
                                                }))
                .addFilterBefore(
                        headerAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}