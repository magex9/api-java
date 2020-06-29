package ca.magex.crm.api.system.id;

import ca.magex.crm.api.Crm;

/**
 * A Specific Identifier used for Business Group Option Identification
 * 
 * @author Jonny
 */
public class BusinessGroupIdentifier extends OptionIdentifier {

	private static final long serialVersionUID = Crm.SERIAL_UID_VERSION;
	
	public static final String CONTEXT = OptionIdentifier.CONTEXT + "business-groups/";

	public BusinessGroupIdentifier(CharSequence id) {
		super(id);
	}
	
	@Override
	public String getContext() {
		return BusinessGroupIdentifier.CONTEXT;
	}
}
