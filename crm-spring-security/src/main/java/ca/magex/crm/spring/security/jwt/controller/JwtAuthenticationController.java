package ca.magex.crm.spring.security.jwt.controller;

import java.util.NoSuchElementException;
import java.util.stream.Collectors;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import ca.magex.crm.spring.security.auth.AuthDetails;
import ca.magex.crm.spring.security.auth.AuthProfiles;
import ca.magex.crm.spring.security.jwt.JwtToken;
import ca.magex.crm.spring.security.jwt.JwtTokenDetails;
import ca.magex.crm.spring.security.jwt.JwtTokenGenerator;
import ca.magex.crm.spring.security.jwt.JwtTokenValidator;
import ca.magex.json.ParserException;
import ca.magex.json.model.JsonObject;
import io.jsonwebtoken.JwtException;

@RestController
@CrossOrigin
@Profile({AuthProfiles.EMBEDDED_HMAC, AuthProfiles.EMBEDDED_RSA})
public class JwtAuthenticationController {

	@Autowired private UserDetailsService userDetailsService;
	@Autowired private AuthenticationManager authenticationManager;
	@Autowired private JwtTokenGenerator jwtTokenGenerator;
	@Autowired private JwtTokenValidator jwtTokenValidator;

	@PostMapping(value = "/authenticate")
	public ResponseEntity<JwtToken> createAuthenticationToken(@RequestBody String jwtRequest) throws Exception {
		try {
			JsonObject json = new JsonObject(jwtRequest);
			Authentication authentication = authenticationManager
					.authenticate(new UsernamePasswordAuthenticationToken(
							json.getString("username"),
							json.getString("password")));
			return ResponseEntity.ok(jwtTokenGenerator.generateToken(authentication));
		}
		catch(AuthenticationException e) {
			LoggerFactory.getLogger(getClass()).info("Authentication Failure: " + e.getMessage());
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		}
		catch(NoSuchElementException e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
		}
		catch(Exception e) {
			LoggerFactory.getLogger(getClass()).error("Error processing authentication request", e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
	}

	@PostMapping(value = "/validate")
	public ResponseEntity<?> validateToken(@RequestBody String jwtToken) {
		try {
			JsonObject json = new JsonObject(jwtToken);
			JwtTokenDetails tokenDetails = jwtTokenValidator.validateToken(json.getString("token"));
			UserDetails userDetails = userDetailsService.loadUserByUsername(tokenDetails.getUsername());
			return ResponseEntity.ok(new AuthDetails(tokenDetails.getUsername(), tokenDetails.getExpiration(), userDetails.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList())));
		}		
		catch(NoSuchElementException | ParserException ex) {
			LoggerFactory.getLogger(getClass()).warn("Error validating token: " + ex.getMessage());
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
		}
		catch(JwtException jwt) {
			return ResponseEntity.ok().body(new AuthDetails(jwt.getMessage()));
		}
	}
}