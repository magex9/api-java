package ca.magex.crm.api.system.id;

import ca.magex.crm.api.Crm;
import ca.magex.crm.api.system.Type;

/**
 * A Specific Identifier used for Status Option Identification
 * 
 * @author Jonny
 */
public class StatusIdentifier extends OptionIdentifier {

	private static final long serialVersionUID = Crm.SERIAL_UID_VERSION;

	public static final String CONTEXT = OptionIdentifier.CONTEXT + "statuses/";

	public StatusIdentifier(CharSequence id) {
		super(id);
	}
	
	@Override
	public String getContext() {
		return StatusIdentifier.CONTEXT;
	}
	
	@Override
	public Type getType() {
		return Type.STATUS;
	}
	
}
