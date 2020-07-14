package ca.magex.crm.api.system.id;

import ca.magex.crm.api.Crm;
import ca.magex.crm.api.system.Type;

/**
 * A Specific Identifier used for Authentication Role Option Identification
 * 
 * @author Jonny
 */
public class AuthenticationRoleIdentifier extends OptionIdentifier {

	private static final long serialVersionUID = Crm.SERIAL_UID_VERSION;
	
	public static final String CONTEXT = OptionIdentifier.CONTEXT + "authentication-roles/";
	
	public static final AuthenticationRoleIdentifier SYS_ADMIN = new AuthenticationRoleIdentifier("SYS/ADMIN");

	public static final AuthenticationRoleIdentifier SYS_ACTUATOR = new AuthenticationRoleIdentifier("SYS/ACTUATOR");

	public static final AuthenticationRoleIdentifier SYS_ACCESS = new AuthenticationRoleIdentifier("SYS/ACCESS");

	public static final AuthenticationRoleIdentifier APP_AUTHENTICATOR = new AuthenticationRoleIdentifier("APP/AUTHENTICATOR");

	public static final AuthenticationRoleIdentifier CRM_ADMIN = new AuthenticationRoleIdentifier("CRM/ADMIN");

	public static final AuthenticationRoleIdentifier CRM_USER = new AuthenticationRoleIdentifier("CRM/USER");

	public static final AuthenticationRoleIdentifier ORG_ADMIN = new AuthenticationRoleIdentifier("ORG/ADMIN");

	public static final AuthenticationRoleIdentifier ORG_USER = new AuthenticationRoleIdentifier("ORG/USER");

	public AuthenticationRoleIdentifier(CharSequence id) {
		super(id);
	}
	
	@Override
	public String getContext() {
		return AuthenticationRoleIdentifier.CONTEXT;
	}
	
	@Override
	public Type getType() {
		return Type.AUTHENTICATION_ROLE;
	}
	
}
