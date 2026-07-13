package com.projects.notificationService.config;

import com.projects.notificationService.exception.TokenBlacklistedException;
import com.projects.notificationService.service.CustomUserDetailsService;
import com.projects.notificationService.service.JwtService;
import com.projects.notificationService.service.RedisService;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtService jwtService;
    private final CustomUserDetailsService userService;
    private final RedisService redisService;

    public JwtAuthenticationFilter(JwtService jwtService, CustomUserDetailsService userService, RedisService redisService){
        this.jwtService = jwtService;
        this.userService = userService;
        this.redisService = redisService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String authHeader = request.getHeader("Authorization");

        if(authHeader == null || !authHeader.startsWith("Bearer ")){
            filterChain.doFilter(request, response);
            return;
        }

        String token = authHeader.substring(7);

        try{
            String userEmail = jwtService.extractUserEmail(token);

            if(userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null){
                String key = "blacklist:"+token;
                if(redisService.get(key, String.class) != null){
                    throw new TokenBlacklistedException("Token has been blacklisted");
                }

                // Extract roles from JWT directly without database lookup
                List<String> roles = jwtService.extractRoles(token);
                List<SimpleGrantedAuthority> authorities = (roles != null) ?
                        roles.stream()
                                .map(SimpleGrantedAuthority::new)
                                .toList()
                        : Collections.emptyList();

                UserDetails userDetails = new org.springframework.security.core.userdetails.User(
                        userEmail,
                        "",
                        authorities
                );

                UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        authorities
                );
                SecurityContextHolder.getContext().setAuthentication(auth);
            }
            filterChain.doFilter(request, response);
        }
        catch (ExpiredJwtException | MalformedJwtException | SignatureException | TokenBlacklistedException | IllegalArgumentException e) {
            SecurityContextHolder.clearContext();

            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.getWriter().write("{\"error\": \"Unauthorized\", \"message\": \"Invalid, expired, or tampered authentication token.\"}");
        }
        catch (Exception e) {
            SecurityContextHolder.clearContext();
            e.printStackTrace();

            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.setContentType("application/json");
            response.getWriter().write("{\"message\":\"Internal server error\"}");
        }
    }
}
