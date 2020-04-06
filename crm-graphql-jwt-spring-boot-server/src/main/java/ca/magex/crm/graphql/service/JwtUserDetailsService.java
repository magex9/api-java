package ca.magex.crm.graphql.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class JwtUserDetailsService implements UserDetailsService {

	private Map<String, String> users = new HashMap<>();

	@PostConstruct
	private void loadUsers() throws IOException {
		URL usersUrl = getClass().getResource("/users.properties");
		try (InputStream is = usersUrl.openStream()) {
			BufferedReader br = new BufferedReader(new InputStreamReader(is));
			String line = null;
			while ((line = br.readLine()) != null) {
				if (line.trim().startsWith("#")) {
					continue;
				}
				if (line.matches("[^=]+=[^=]+")) {
					String[] components = line.split("=");
					users.put(components[0], components[1]);
				}
			}
		}
	}

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		if (users.containsKey(username)) {
			return new User(
					username, 
					users.get(username),
					new ArrayList<>());
		} else {
			throw new UsernameNotFoundException("User not found with username: " + username);
		}
	}
}