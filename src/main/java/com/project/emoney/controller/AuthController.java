package com.project.emoney.controller;
import com.project.emoney.entity.User;
import com.project.emoney.payload.ResponseWrapper;
import com.project.emoney.payload.SimpleResponseWrapper;
import com.project.emoney.security.JwtRequest;
import com.project.emoney.security.JwtResponse;
import com.project.emoney.security.JwtTokenUtil;
import com.project.emoney.security.JwtUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin
public class AuthController {

  @Autowired
  private AuthenticationManager authenticationManager;
  @Autowired
  private JwtTokenUtil jwtTokenUtil;
  @Autowired
  private JwtUserDetailsService userDetailsService;

  @RequestMapping(value = "/api/register", method = RequestMethod.POST)
  public ResponseEntity<?> saveUser(@RequestBody User user){
    userDetailsService.save(user);
    return new ResponseEntity<>(new SimpleResponseWrapper(201, "success"), HttpStatus.OK);
  }

  @RequestMapping(value = "/api/login", method = RequestMethod.POST)
  public ResponseEntity<?> createAuthenticationToken(@RequestBody JwtRequest authenticationRequest) {
    try {
      authenticate(authenticationRequest.getEmailOrPhone(), authenticationRequest.getPassword());
    } catch (Exception e) {
      return new ResponseEntity<>(new SimpleResponseWrapper(400, "bad credentials"), HttpStatus.BAD_REQUEST);
    }
    final UserDetails userDetails = userDetailsService
        .loadUserByUsername(authenticationRequest.getEmailOrPhone());
    final String token = jwtTokenUtil.generateToken(userDetails);
    return new ResponseEntity<>(new SimpleResponseWrapper(201, "success"), HttpStatus.OK);
  }

  private void authenticate(String username, String password) throws Exception {
    try {
      authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
    } catch (DisabledException e) {
      throw new Exception("USER_DISABLED", e);
    } catch (BadCredentialsException e) {
      throw new Exception("INVALID_CREDENTIALS", e);
    }
  }
}