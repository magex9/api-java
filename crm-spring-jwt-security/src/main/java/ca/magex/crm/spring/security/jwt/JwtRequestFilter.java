package ca.magex.crm.spring.security.jwt;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class JwtRequestFilter extends OncePerRequestFilter {
	
	@Autowired private UserDetailsService userDetailsService;
	@Autowired private JwtTokenService jwtTokenService;

	@Value("${jwt.request.filter.ignore}")
	private String ignoreFilter;

	@Override
	protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
		/* don't apply this filter to the authenticate method */
		return new AntPathRequestMatcher(ignoreFilter).matcher(request).isMatch();
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {
		/* only continue if we don't have an authentication */
		if (SecurityContextHolder.getContext().getAuthentication() == null) {
			/* check for an Authorization Header */
			final String requestTokenHeader = request.getHeader("Authorization");

			String username = null;
			String jwtToken = null;

			/*
			 * JWT Token uses format "Bearer <token>" so we need to extract the token from
			 * the header
			 */
			if (requestTokenHeader != null && requestTokenHeader.startsWith("Bearer ")) {
				jwtToken = requestTokenHeader.substring(7);
				try {
					username = jwtTokenService.validateToken(jwtToken);
					if (username != null) {
						/* get the user details for the username provided */
						UserDetails userDetails = userDetailsService.loadUserByUsername(username);

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
			} else {
				logger.debug("Authorization is not JWT");
			}
		}
		chain.doFilter(request, response);
	}
}