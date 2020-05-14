package com.project.emoney.security;
import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.emoney.payload.response.SimpleResponseWrapper;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import io.jsonwebtoken.ExpiredJwtException;

@Component
public class JwtRequestFilter extends OncePerRequestFilter {

  @Autowired
  private JwtUserDetailsService jwtUserDetailsService;
  @Autowired
  private JwtTokenUtil jwtTokenUtil;
  ObjectMapper objectMapper = new ObjectMapper();

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
      } catch (SignatureException | MalformedJwtException e) {
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
      if (!request.getRequestURI().equals("/api/login")&&!request.getRequestURI().equals("/api/register")&&!request.getRequestURI().equals("/api/otp")
      &&!request.getRequestURI().equals("/api/login/")&&!request.getRequestURI().equals("/api/register/")&&!request.getRequestURI().equals("/api/otp/")){
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
      } catch (Exception e) {
        response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
        response.getWriter().write(objectMapper.writeValueAsString(new SimpleResponseWrapper(HttpStatus.TOO_MANY_REQUESTS.value(),"too many connections")));
      }
    }
    chain.doFilter(request, response);
  }
}
