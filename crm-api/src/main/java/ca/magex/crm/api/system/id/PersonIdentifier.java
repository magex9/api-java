package ca.magex.crm.api.system.id;

import ca.magex.crm.api.Crm;
import ca.magex.crm.api.system.Identifier;

/**
 * A Specific Identifier used for Person Identification
 * 
 * @author Jonny
 */
public class PersonIdentifier extends Identifier {

	private static final long serialVersionUID = Crm.SERIAL_UID_VERSION;
	
	public static final String CONTEXT = Identifier.CONTEXT + "persons/";	
	
	public PersonIdentifier(String id) {
		super(id);
	}
	
	public PersonIdentifier(CharSequence id) {
		super(id);
	}
	
	@Override
	public String getContext() {
		return PersonIdentifier.CONTEXT;
	}
}