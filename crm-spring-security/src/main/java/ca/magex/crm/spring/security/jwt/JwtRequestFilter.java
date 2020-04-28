package ca.magex.crm.spring.security.jwt;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class JwtRequestFilter extends OncePerRequestFilter {

	@Autowired private JwtAuthDetailsService jwtAuthDetailsService;

	@Value("${jwt.request.filter.ignore:}") private String ignoreFilter;

	@Override
	protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
		/* don't apply this filter to the authenticate method */
		if (StringUtils.isNotBlank(ignoreFilter)) {
			return new AntPathRequestMatcher(ignoreFilter).matcher(request).isMatch();
		}
		else {
			return false;
		}
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {
		/* only continue if we don't have an authentication */
		if (SecurityContextHolder.getContext().getAuthentication() == null) {
			/* check for an Authorization Header */
			final String requestTokenHeader = request.getHeader("Authorization");

			String jwtToken = null;

			/* check if we have a Bearer token in the Authorization Header */
			if (requestTokenHeader != null && requestTokenHeader.startsWith("Bearer ")) {
				jwtToken = requestTokenHeader.substring(7);
				try {
					JwtAuthenticationToken authenticationToken = jwtAuthDetailsService.getJwtAuthenticationTokenForUsername(jwtToken);
					/* set the authentication for spring */
					SecurityContextHolder.getContext().setAuthentication(authenticationToken);					
				} catch (Exception e) {
					logger.warn("Cannot parse jwt token: " + jwtToken, e);
				}
			} else {
				logger.debug("Authorization is not JWT");
			}
		}
		chain.doFilter(request, response);
	}
}