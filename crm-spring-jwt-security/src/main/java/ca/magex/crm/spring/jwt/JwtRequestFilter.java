package ca.magex.crm.spring.jwt;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import ca.magex.crm.spring.jwt.service.JwtUserDetailsService;

@Component
public class JwtRequestFilter extends OncePerRequestFilter {
	
	@Autowired
	private JwtUserDetailsService jwtUserDetailsService;

	@Autowired
	private JwtTokenUtil jwtTokenUtil;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {
		/* only continue if we don't have an authentication */
		if (SecurityContextHolder.getContext().getAuthentication() == null) {
			/* check for an Authorization Header */
			final String requestTokenHeader = request.getHeader("Authorization");
	
			String username = null;
			String jwtToken = null;
			
			/*
			 * JWT Token uses format "Bearer <token>" so we need to extract the token from the header
			 */
			if (requestTokenHeader != null && requestTokenHeader.startsWith("Bearer ")) {
				jwtToken = requestTokenHeader.substring(7);
				try {
					username = jwtTokenUtil.validateToken(jwtToken);
					if (username != null) {
						/* get the user details for the username provided */
						UserDetails userDetails = jwtUserDetailsService.loadUserByUsername(username);
						
						UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(
								userDetails, 
								null, 
								userDetails.getAuthorities());
						usernamePasswordAuthenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
						/* set the authentication for spring */
						SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);						
					}
				} catch (Exception e) {
					logger.debug("Cannot parse jwt token");
				}
			} 
			else {
				logger.debug("Authorization is not JWT");
			}	
		}
		chain.doFilter(request, response);
	}
}