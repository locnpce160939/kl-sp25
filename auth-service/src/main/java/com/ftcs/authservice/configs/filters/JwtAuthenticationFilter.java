package com.ftcs.authservice.configs.filters;


import com.ftcs.authservice.AuthAccountURL;
import com.ftcs.authservice.features.auth.service.JwtService;
import com.ftcs.common.exception.UnauthorizedException;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.token.TokenService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;
    private final ParseToken parseToken;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws IOException {
        final String authHeader = request.getHeader("Authorization");
        try {
            if (shouldBypassAuthentication(request)) {
                filterChain.doFilter(request, response);
                return;
            }

            if (isTokenInvalid(authHeader)) {
                filterChain.doFilter(request, response);
                return;
            }

            Integer accountId = parseToken.getId(authHeader);
            String username = parseToken.getUsername(authHeader);
            String role = parseToken.getRole(authHeader);
            request.setAttribute("accountId", accountId);
            request.setAttribute("username", username);
            request.setAttribute("role", role);

            processAuthentication(authHeader, request);

            filterChain.doFilter(request, response);
        } catch (ExpiredJwtException | UnauthorizedException e) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write(e.getMessage());
        }

    }


    private boolean shouldBypassAuthentication(HttpServletRequest request) {
        return request.getServletPath().contains(AuthAccountURL.AUTH);
    }

    public boolean isTokenInvalid(String authHeader) {
        return authHeader == null || !authHeader.startsWith("Bearer ");
    }

    private void processAuthentication(String authHeader, HttpServletRequest servletRequest) {
        final String jwt = authHeader.substring(7);
        final String username = jwtService.extractUsername(jwt);

        if (StringUtils.hasText(jwt) && username != null) {
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);

            if (jwtService.isTokenValid(jwt, userDetails)) {
                authenticateUser(userDetails, servletRequest);
            } else {
                throw new UnauthorizedException("Token is invalid");
            }
        }
    }

    private void authenticateUser(UserDetails userDetails, HttpServletRequest request) {
        String role = (String) request.getAttribute("role");
        List<GrantedAuthority> authorities = Collections.singletonList(new SimpleGrantedAuthority(role));

        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(userDetails, null, authorities);
        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

}