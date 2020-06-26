package ca.magex.crm.test.generator;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import ca.magex.crm.api.store.CrmStore;

public class CrmStoreTests {

	@Test
	public void testBase58Generator() throws Exception {
		for (int i = 0; i < 10; i++) {
			assertTrue(CrmStore.generateId("/objects").toString().matches("[123456789abcdefghijkmnopqrstuvwxyzABCDEFGHJKLMNPQRSTUVWXYZ]+"));
		}
	}
	
}
