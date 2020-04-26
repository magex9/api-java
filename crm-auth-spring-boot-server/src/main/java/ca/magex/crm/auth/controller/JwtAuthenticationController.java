package ca.magex.crm.auth.controller;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import ca.magex.crm.auth.model.JwtRequest;
import ca.magex.crm.auth.model.JwtResponse;
import ca.magex.crm.auth.model.TokenDetailsRequest;
import ca.magex.crm.auth.model.TokenDetailsResponse;
import ca.magex.crm.auth.model.UserDetailsRequest;
import ca.magex.crm.auth.model.UserDetailsResponse;
import ca.magex.crm.spring.security.jwt.JwtTokenService;

@RestController
@CrossOrigin
public class JwtAuthenticationController {

	@Autowired private UserDetailsService userDetailsService;
	@Autowired private AuthenticationManager authenticationManager;
	@Autowired private JwtTokenService jwtTokenService;
	
	@PostMapping(value = "/authenticate")
	public ResponseEntity<?> createAuthenticationToken(@RequestBody JwtRequest jwtRequest) throws Exception {
		Authentication authentication = authenticationManager
				.authenticate(new UsernamePasswordAuthenticationToken(
						jwtRequest.getUsername(), 
						jwtRequest.getPassword()));
		return ResponseEntity.ok(new JwtResponse(jwtTokenService.generateToken(authentication)));
	}
	
	@PostMapping(value = "/userDetails")
	public UserDetailsResponse getUserDetails(@RequestBody UserDetailsRequest userDetailsRequest) {
		UserDetails userDetails = userDetailsService.loadUserByUsername(userDetailsRequest.getUsername());		
		return new UserDetailsResponse(userDetails);
	}
	
	@PostMapping(value = "/validateToken")
	public TokenDetailsResponse validateToken(@RequestBody TokenDetailsRequest tokenDetailsRequest) {
		String username = jwtTokenService.validateToken(tokenDetailsRequest.getToken());
		Date expiration = jwtTokenService.getExpiration(tokenDetailsRequest.getToken());
		return new TokenDetailsResponse(username, expiration);
	}
}