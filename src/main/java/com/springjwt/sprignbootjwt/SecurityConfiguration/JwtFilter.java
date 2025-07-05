package com.springjwt.sprignbootjwt.SecurityConfiguration;


import com.springjwt.sprignbootjwt.Repo.UserRepo;
import com.springjwt.sprignbootjwt.Service.JWTService;
import com.springjwt.sprignbootjwt.Service.MyUserDetailsService;
import com.springjwt.sprignbootjwt.model.Users;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;

@Component
public class JwtFilter extends OncePerRequestFilter {

    @Autowired
    private JWTService jwtService;

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private MyUserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");
        String token = null;
        String username = null;

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            token = authHeader.substring(7);
            try {
                username = jwtService.extractUserName(token);
            } catch (Exception e) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write("{\"error\": \"Invalid token\"}");
                return;
            }
        }

        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);

            if (jwtService.validateToken(token, userDetails)) {
                Users user = userRepo.findByUsername(username);
                LocalDateTime now = LocalDateTime.now();

                if (user.getLastActiveAt() != null &&
                        Duration.between(user.getLastActiveAt(), now).toMinutes() <= 5) {

                    // Update activity time
                    user.setLastActiveAt(now);
                    userRepo.save(user);

                    // Generate and send new token (sliding expiration)
                    String newToken = jwtService.generateToken(username);
                    response.setHeader("Authorization", "Bearer " + newToken); // Better than writing JSON

                    // Set user authentication
                    UsernamePasswordAuthenticationToken authToken =
                            new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);

                } else {
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    response.setContentType("application/json");
                    response.setCharacterEncoding("UTF-8");
                    response.getWriter().write("{\"error\": \"Token expired due to inactivity\"}");
                    return;
                }
            }
        }

        filterChain.doFilter(request, response);
    }
}