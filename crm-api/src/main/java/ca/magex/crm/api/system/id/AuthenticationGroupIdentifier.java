package ca.magex.crm.api.system.id;

import ca.magex.crm.api.Crm;
import ca.magex.crm.api.system.Type;

/**
 * A Specific Identifier used for Authentication Group Option Identification
 * 
 * @author Jonny
 */
public class AuthenticationGroupIdentifier extends OptionIdentifier {

	private static final long serialVersionUID = Crm.SERIAL_UID_VERSION;
	
	public static final String CONTEXT = OptionIdentifier.CONTEXT + "authentication-groups/";
	
	public static final AuthenticationGroupIdentifier SYS = new AuthenticationGroupIdentifier("SYS");

	public static final AuthenticationGroupIdentifier APP = new AuthenticationGroupIdentifier("APP");

	public static final AuthenticationGroupIdentifier CRM = new AuthenticationGroupIdentifier("CRM");

	public static final AuthenticationGroupIdentifier ORG = new AuthenticationGroupIdentifier("ORG");

	public AuthenticationGroupIdentifier(CharSequence id) {
		super(id);
	}
	
	@Override
	public String getContext() {
		return AuthenticationGroupIdentifier.CONTEXT;
	}
	
	@Override
	public Type getType() {
		return Type.AUTHENTICATION_GROUP;
	}
	
}
