package ca.magex.crm.amnesia.generator;

import ca.magex.crm.api.system.Identifier;

public class AmnesiaSequenceIdGenerator implements IdGenerator {

	private static int sequence = 1;

	public Identifier generate() {
		return new Identifier(Integer.toString(sequence++));
	}
	
}
