package ca.magex.crm.api.system.id;

import ca.magex.crm.api.Crm;

/**
 * A Specific Identifier used for Business Role Option Identification
 * 
 * @author Jonny
 */
public class BusinessRoleIdentifier extends OptionIdentifier {

	private static final long serialVersionUID = Crm.SERIAL_UID_VERSION;

	public BusinessRoleIdentifier(CharSequence id) {
		super(id);
	}
	
	@Override
	public String getContext() {
		return super.getContext() + "businessRoles/";
	}
}
