package ca.magex.crm.api.system.id;

import ca.magex.crm.api.Crm;
import ca.magex.crm.api.system.Identifier;

/**
 * A Specific Identifier used for Option Identification
 * 
 * @author Jonny
 */
public class OptionIdentifier extends Identifier {

	private static final long serialVersionUID = Crm.SERIAL_UID_VERSION;
	
	public static String CONTEXT = Identifier.CONTEXT +  "options/";
	
	protected OptionIdentifier(CharSequence id) {
		super(id);
	}
	
	@Override
	public String getContext() {		
		return CONTEXT;
	}
	
}