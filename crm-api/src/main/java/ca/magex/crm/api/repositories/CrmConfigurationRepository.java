package ca.magex.crm.api.repositories;

import org.apache.commons.lang3.RandomStringUtils;

import ca.magex.crm.api.system.Identifier;

public interface CrmConfigurationRepository {

	public static final String BASE_58 = "123456789abcdefghijkmnopqrstuvwxyzABCDEFGHJKLMNPQRSTUVWXYZ";

	default public Identifier generateId() {
		return new Identifier(RandomStringUtils.random(10, BASE_58));
	}
	
	boolean isInitialized();

}
