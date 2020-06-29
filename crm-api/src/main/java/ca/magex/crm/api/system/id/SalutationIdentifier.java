package ca.magex.crm.api.system.id;

import ca.magex.crm.api.Crm;

/**
 * A Specific Identifier used for Salutation Identification
 * 
 * @author Jonny
 */
public class SalutationIdentifier extends OptionIdentifier {

	private static final long serialVersionUID = Crm.SERIAL_UID_VERSION;

	public SalutationIdentifier(CharSequence id) {
		super(id);
	}

	@Override
	public String getContext() {
		return super.getContext() + "salutations/";
	}
}