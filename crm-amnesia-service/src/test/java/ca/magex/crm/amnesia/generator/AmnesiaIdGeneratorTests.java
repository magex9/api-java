package ca.magex.crm.amnesia.generator;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class AmnesiaIdGeneratorTests {

	@Test
	public void testBase58Generator() throws Exception {
		AmnesiaBase58IdGenerator generator = new AmnesiaBase58IdGenerator();
		for (int i = 0; i < 10; i++) {
			assertTrue(generator.generate().toString().matches("[123456789abcdefghijkmnopqrstuvwxyzABCDEFGHJKLMNPQRSTUVWXYZ]+"));
		}
	}
	
	@Test
	public void testSequenceGenerator() throws Exception {
		AmnesiaSequenceIdGenerator generator = new AmnesiaSequenceIdGenerator();
		for (int i = 0; i < 10; i++) {
			assertTrue(generator.generate().toString().contentEquals(Integer.toString(i + 1)));
		}
	}
	
}
