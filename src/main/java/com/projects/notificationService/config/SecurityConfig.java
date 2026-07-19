package com.projects.notificationService.config;

import com.projects.notificationService.dto.ErrorResponse;
import com.projects.notificationService.service.CustomUserDetailsService;
import com.projects.notificationService.service.JwtService;
import com.projects.notificationService.service.RedisService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.time.LocalDateTime;

@Configuration
public class SecurityConfig {
    @Bean
    public BCryptPasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider daoAuthenticationProvider(CustomUserDetailsService userDetailsService){
        DaoAuthenticationProvider auth = new DaoAuthenticationProvider(userDetailsService);
        auth.setPasswordEncoder(passwordEncoder());
        return auth;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    private void writeErrorResponse(
            HttpServletResponse response,
            HttpStatus status,
            String message) throws IOException {

        ErrorResponse error = new ErrorResponse(
                status.value(),
                message,
                LocalDateTime.now()
        );

        response.setStatus(status.value());
        response.setContentType("application/json");

        new ObjectMapper().writeValue(
                response.getWriter(),
                error
        );
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, JwtService jwtService,
                                                   CustomUserDetailsService userService,
                                                   RedisService redisService) throws Exception{
        http.authorizeHttpRequests(config ->
                config.requestMatchers("/api/auth/**", "/actuator/**").permitAll()
                        .requestMatchers("/api/admin/**").hasRole("ADMIN")
                        .anyRequest().authenticated());

        http.exceptionHandling(ex -> ex
                // 1. Handles completely unauthenticated/missing tokens (401 Unauthorized)
                .authenticationEntryPoint((request, response, accessDeniedException) ->
                        writeErrorResponse(
                                response,
                                HttpStatus.UNAUTHORIZED,
                                "Invalid or missing authentication token."
                        )
                )
                // 2. Handles logged-in users with incorrect roles (403 Forbidden)
                .accessDeniedHandler((request, response, authException) ->
                        writeErrorResponse(
                                response,
                                HttpStatus.FORBIDDEN,
                                "You do not have permission to access this resource."
                        )
                )
        );

        http.csrf(csrf -> csrf.disable());

        http.sessionManagement(session -> session
                .sessionCreationPolicy(org.springframework.security.config.http.SessionCreationPolicy.STATELESS));

        http.addFilterBefore(new JwtAuthenticationFilter(jwtService, userService, redisService),
                UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
