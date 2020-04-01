package ca.magex.crm.amnesia.generator;

import org.apache.commons.lang3.RandomStringUtils;

import ca.magex.crm.api.system.Identifier;

public class AmnesiaBase58IdGenerator implements IdGenerator {

	private static final String BASE_58 = "123456789abcdefghijkmnopqrstuvwxyzABCDEFGHJKLMNPQRSTUVWXYZ";

	public Identifier generate() {
		return new Identifier(RandomStringUtils.random(10, BASE_58));
	}
	
}
