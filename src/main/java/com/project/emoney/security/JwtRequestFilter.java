package com.project.emoney.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.emoney.payload.response.SimpleResponseWrapper;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class JwtRequestFilter extends OncePerRequestFilter {

  @Autowired
  private JwtUserDetailsService jwtUserDetailsService;
  @Autowired
  private JwtTokenUtil jwtTokenUtil;
  private final ObjectMapper objectMapper = new ObjectMapper();

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
      throws ServletException, IOException {
    final String requestTokenHeader = request.getHeader("Authorization");
    String username = null;
    String jwtToken = null;
    if (requestTokenHeader != null && requestTokenHeader.startsWith("Bearer ")) {
      jwtToken = requestTokenHeader.substring(7);
      try {
        username = jwtTokenUtil.getUsernameFromToken(jwtToken);
      } catch (SignatureException | MalformedJwtException | UsernameNotFoundException e) {
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.getWriter().write(objectMapper.writeValueAsString(new SimpleResponseWrapper(HttpStatus.UNAUTHORIZED.value(),"invalid token")));
      } catch (IllegalArgumentException e) {
        System.out.println("Unable to get JWT Token");
      } catch (ExpiredJwtException e) {
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.getWriter().write(objectMapper.writeValueAsString(new SimpleResponseWrapper(HttpStatus.UNAUTHORIZED.value(),"expired token")));
      }
    } else {
      logger.warn("JWT Token does not begin with Bearer String");
      if (!request.getRequestURI().equals("/api/login")&&!request.getRequestURI().equals("/api/register")&&
          !request.getRequestURI().equals("/api/otp")&&!request.getRequestURI().equals("/api/login/")&&
          !request.getRequestURI().equals("/api/register/")&&!request.getRequestURI().equals("/api/otp/")&&
          !request.getRequestURI().startsWith("/api/verify")){
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.getWriter().write(objectMapper.writeValueAsString(new SimpleResponseWrapper(HttpStatus.UNAUTHORIZED.value(),"no token")));
      }
    }
// Once we get the token validate it.
    if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
      try {
        UserDetails userDetails = this.jwtUserDetailsService.loadUserByUsername(username);
        if (jwtTokenUtil.validateToken(jwtToken, userDetails)) {
          UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(
              userDetails, null, userDetails.getAuthorities());
          usernamePasswordAuthenticationToken
              .setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
          SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
        }
      } catch (UsernameNotFoundException e) {
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.getWriter().write(objectMapper.writeValueAsString(new SimpleResponseWrapper(HttpStatus.UNAUTHORIZED.value(),"invalid token")));
      }
    }
    chain.doFilter(request, response);
  }
}
