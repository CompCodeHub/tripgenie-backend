package com.tripgenie.security;

import com.tripgenie.service.JwtService;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    public JwtAuthenticationFilter(JwtService jwtService, UserDetailsService userDetailsService) {
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        // Get auth header
        String authHeader = request.getHeader("Authorization");

        // Get token from header
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            String username = jwtService.getUsername(token);

            try {

                // Check if any user is not authenticated
                if (username != null && SecurityContextHolder.getContext().getAuthentication() != null) {

                    // Get user details from username
                    UserDetails userDetails = userDetailsService.loadUserByUsername(username);

                    // If userdetails aren't null and token isn't expired
                    if (userDetails != null && !jwtService.isTokenExpired(token)) {
                        UsernamePasswordAuthenticationToken authenticationToken =
                                new UsernamePasswordAuthenticationToken(userDetails,
                                        userDetails.getPassword(),
                                        userDetails.getAuthorities()
                                );

                        // Set authentication
                        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                    }

                }

                // Forward the request
                filterChain.doFilter(request, response);

            } catch (ExpiredJwtException expiredJwtException) {
                // Forward the request to your controller for handling the expired token
                request.getRequestDispatcher("/api/users/expired-token").forward(request, response);
            }
        }
    }
}
