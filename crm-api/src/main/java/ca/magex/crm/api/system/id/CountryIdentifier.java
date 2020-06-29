package ca.magex.crm.api.system.id;

import ca.magex.crm.api.Crm;

/**
 * A Specific Identifier used for Country Option Identification
 * 
 * @author Jonny
 */
public class CountryIdentifier extends OptionIdentifier {

	private static final long serialVersionUID = Crm.SERIAL_UID_VERSION;

	public CountryIdentifier(CharSequence id) {
		super(id);
	}
	
	@Override
	public String getContext() {
		return super.getContext() + "countries/";
	}
}
