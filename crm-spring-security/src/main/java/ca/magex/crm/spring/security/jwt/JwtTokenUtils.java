package ca.magex.crm.spring.security.jwt;

import java.util.Base64;

import ca.magex.json.model.JsonObject;

public class JwtTokenUtils {

	/**
	 * returns a json object representing the claims of a given token without validating the token
	 * @param token
	 * @return
	 */
	public static JsonObject getClaims(String token) {
		if (!token.matches("[^\\,]+\\.[^\\.]+\\.[^\\.]+")) {
			throw new IllegalArgumentException("Invalid JWT Token Format");
		}
		String claims = token.split("\\.")[1];
		try {
			return new JsonObject(new String(Base64.getDecoder().decode(claims)));
		}
		catch(Exception e) {
			throw new IllegalArgumentException("Invalid JWT Token Contents", e);
		}
	}
}
