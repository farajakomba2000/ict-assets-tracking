package com.example.ictassetstracking.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * This filter is no longer needed as Spring Security's oauth2ResourceServer
 * handles Bearer token authentication natively with JwtDecoder.
 * Kept for reference but not registered in the filter chain.
 */
@Component
public class BearerTokenAuthenticationFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        // Bearer token handling is now handled by oauth2ResourceServer
        filterChain.doFilter(request, response);
    }
}
