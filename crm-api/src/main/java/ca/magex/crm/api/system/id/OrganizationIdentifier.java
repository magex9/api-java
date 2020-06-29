package ca.magex.crm.api.system.id;

import ca.magex.crm.api.Crm;
import ca.magex.crm.api.system.Identifier;

/**
 * A Specific Identifier used for Organization Identification
 * 
 * @author Jonny
 */
public class OrganizationIdentifier extends Identifier {

	private static final long serialVersionUID = Crm.SERIAL_UID_VERSION;
	
	public OrganizationIdentifier(CharSequence id) {		
		super(id);
	}
	
	@Override
	public String getContext() {
		return super.getContext() + "organizations/";
	}
}