package ca.magex.crm.api.system.id;

import ca.magex.crm.api.Crm;
import ca.magex.crm.api.system.Status;
import ca.magex.crm.api.system.Type;

/**
 * A Specific Identifier used for Status Option Identification
 * 
 * @author Jonny
 */
public class StatusIdentifier extends OptionIdentifier {

	private static final long serialVersionUID = Crm.SERIAL_UID_VERSION;

	public static final String CONTEXT = OptionIdentifier.CONTEXT + "statuses/";
	
	public static final StatusIdentifier ACTIVE = new StatusIdentifier(Status.ACTIVE.toString());

	public static final StatusIdentifier INACTIVE = new StatusIdentifier(Status.INACTIVE.toString());
	
	public static final StatusIdentifier PENDING = new StatusIdentifier(Status.PENDING.toString());

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
