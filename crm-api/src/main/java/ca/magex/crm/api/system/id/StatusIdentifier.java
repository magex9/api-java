package ca.magex.crm.api.system.id;

import ca.magex.crm.api.Crm;

/**
 * A Specific Identifier used for Status Option Identification
 * 
 * @author Jonny
 */
public class StatusIdentifier extends OptionIdentifier {

	private static final long serialVersionUID = Crm.SERIAL_UID_VERSION;

	public StatusIdentifier(CharSequence id) {
		super(id);
	}
	
	@Override
	public String getContext() {
		return super.getContext() + "statuses/";
	}
}
