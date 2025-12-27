package com.e_connect.search.configs;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class JWTFilter extends OncePerRequestFilter {

  private final JWTService jwtService;

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {
    Cookie[] cookies = request.getCookies();
    List<Cookie> cookArr = cookies != null ? Arrays.asList(cookies) : null;
    if (cookArr != null && cookArr.size() != 0) {
      String authorization = cookArr.stream()
          .filter(cookie -> "JWT-TOKEN".equals(cookie.getName()) && cookie.getValue() != null)
          .map(cookie -> cookie.getValue()).findFirst().orElse(null);
      String userName = null;
      if (authorization != null) {
        userName = jwtService.extractUsername(authorization);
        if (SecurityContextHolder.getContext().getAuthentication() == null && !jwtService.isTokenExpired(authorization)) {
                  List<SimpleGrantedAuthority> authorities = jwtService.extractUserRoles(authorization).stream()
            .map(SimpleGrantedAuthority::new).toList();  
          UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                userName,
                null, authorities);
            SecurityContextHolder.getContext().setAuthentication(authenticationToken);
        }
      }
    }
    filterChain.doFilter(request, response);
  }

}
