package com.eikona.mata.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.eikona.mata.dto.AuthenticationRequest;
import com.eikona.mata.dto.AuthenticationResponse;
import com.eikona.mata.config.security.JwtUtil;
import com.eikona.mata.service.MyUserDetailsService;


@RestController
public class JwtAuthenticationController {
	@Autowired
	private AuthenticationManager authenticationManager;
	
	@Autowired
	private JwtUtil jwtTokenUtil;
	
	@Autowired
	private MyUserDetailsService userDetailsService;
	
	
	@RequestMapping(value="/rest/authenticate",method=RequestMethod.POST)
	public  ResponseEntity<?> createAuthenticateToken(@RequestBody AuthenticationRequest authenticationRequest) throws Exception{
		try {
			authenticationManager.authenticate(
					new UsernamePasswordAuthenticationToken(authenticationRequest.getUsername(), authenticationRequest.getPassword()));
		}
		catch (BadCredentialsException e) {
			throw new Exception("Incorrect username or password",e);
		}
		final UserDetails userDetails=userDetailsService.loadUserByUsername(authenticationRequest.getUsername());
	    final String jwt=jwtTokenUtil.generateToken(userDetails);
	    return ResponseEntity.ok(new AuthenticationResponse(jwt));
	}
	
}
