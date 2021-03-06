package ca.magex.crm.api.system.id;

import ca.magex.crm.api.Crm;
import ca.magex.crm.api.system.Type;

/**
 * A Specific Identifier used for Salutation Identification
 * 
 * @author Jonny
 */
public class SalutationIdentifier extends OptionIdentifier {

	private static final long serialVersionUID = Crm.SERIAL_UID_VERSION;

	public static final String CONTEXT = OptionIdentifier.CONTEXT + "salutations/";

	public SalutationIdentifier(CharSequence id) {
		super(id);
	}
	
	@Override
	public String getContext() {
		return SalutationIdentifier.CONTEXT;
	}
	
	@Override
	public Type getType() {
		return Type.SALUTATION;
	}
	
}