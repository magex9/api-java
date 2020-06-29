package ca.magex.crm.api.system.id;

import ca.magex.crm.api.Crm;

/**
 * A Specific Identifier used for Authentication Group Option Identification
 * 
 * @author Jonny
 */
public class AuthenticationGroupIdentifier extends OptionIdentifier {

	private static final long serialVersionUID = Crm.SERIAL_UID_VERSION;
	
	public static final String CONTEXT = OptionIdentifier.CONTEXT + "authentication-groups/";

	public AuthenticationGroupIdentifier(CharSequence id) {
		super(id);
	}
	
	@Override
	public String getContext() {
		return AuthenticationGroupIdentifier.CONTEXT;
	}
}
