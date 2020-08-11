package ca.magex.crm.api.system.id;

import ca.magex.crm.api.Crm;
import ca.magex.crm.api.system.Type;

/**
 * A Specific Identifier used for Business Group Option Identification
 * 
 * @author Jonny
 */
public class BusinessGroupIdentifier extends OptionIdentifier {

	private static final long serialVersionUID = Crm.SERIAL_UID_VERSION;
	
	public static final String CONTEXT = OptionIdentifier.CONTEXT + "business-groups/";
	
	public static final BusinessGroupIdentifier EXECS = new BusinessGroupIdentifier("EXECS");

	public static final BusinessGroupIdentifier IMIT = new BusinessGroupIdentifier("IMIT");

	public static final BusinessGroupIdentifier EXTERNAL = new BusinessGroupIdentifier("EXTERNAL");

	public BusinessGroupIdentifier(CharSequence id) {
		super(id);
	}
	
	@Override
	public String getContext() {
		return BusinessGroupIdentifier.CONTEXT;
	}
	
	@Override
	public Type getType() {
		return Type.BUSINESS_GROUP;
	}
	
}
