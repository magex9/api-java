package ca.magex.crm.api.system.id;

import ca.magex.crm.api.Crm;
import ca.magex.crm.api.system.Identifier;

/**
 * A Specific Identifier used for User Identification
 * 
 * @author Jonny
 */
public class UserIdentifier extends Identifier {

	private static final long serialVersionUID = Crm.SERIAL_UID_VERSION;
	
	public static final String CONTEXT = Identifier.CONTEXT + "users/";	
	
	public UserIdentifier(String id) {		
		super(id);
	}
	
	public UserIdentifier(CharSequence id) {		
		super(id);
	}
	
	@Override
	public String getContext() {
		return UserIdentifier.CONTEXT;
	}
}