package ca.magex.crm.spring.security.jwt.controller;

import java.util.Date;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import ca.magex.crm.spring.security.MagexSecurityProfile;
import ca.magex.crm.spring.security.auth.AuthDetails;
import ca.magex.crm.spring.security.jwt.JwtRequest;
import ca.magex.crm.spring.security.jwt.JwtToken;
import ca.magex.crm.spring.security.jwt.JwtTokenService;

@RestController
@CrossOrigin
@Profile(MagexSecurityProfile.EMBEDDED_JWT)
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
		return ResponseEntity.ok(new JwtToken(jwtTokenService.generateToken(authentication)));
	}
	
	@PostMapping(value = "/validate")
	public ResponseEntity<?> validateToken(@RequestBody JwtToken jwtToken) {
		String username = jwtTokenService.validateToken(jwtToken.getToken());
		Date expiration = jwtTokenService.getExpiration(jwtToken.getToken());
		UserDetails userDetails = userDetailsService.loadUserByUsername(username);
		return ResponseEntity.ok(new AuthDetails(username, expiration, userDetails.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList())));
	}	
}