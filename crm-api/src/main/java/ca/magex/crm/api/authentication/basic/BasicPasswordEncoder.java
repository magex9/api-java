package ca.magex.crm.api.authentication.basic;

import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.security.crypto.password.PasswordEncoder;

public class BasicPasswordEncoder implements PasswordEncoder {

	@Override
	public String encode(CharSequence rawPassword) {
		return DigestUtils.md5Hex(rawPassword.toString());
	}

	@Override
	public boolean matches(CharSequence rawPassword, String encodedPassword) {
		return DigestUtils.md5Hex(rawPassword.toString()).equals(encodedPassword);
	}

}
