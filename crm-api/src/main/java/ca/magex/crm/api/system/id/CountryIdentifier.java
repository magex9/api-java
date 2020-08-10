package ca.magex.crm.api.system.id;

import ca.magex.crm.api.Crm;
import ca.magex.crm.api.system.Type;

/**
 * A Specific Identifier used for Country Option Identification
 * 
 * @author Jonny
 */
public class CountryIdentifier extends OptionIdentifier {

	private static final long serialVersionUID = Crm.SERIAL_UID_VERSION;
	
	public static final String CONTEXT = OptionIdentifier.CONTEXT + "countries/";

	public CountryIdentifier(CharSequence id) {
		super(id);
	}
	
	@Override
	public String getContext() {
		return CountryIdentifier.CONTEXT;
	}
	
	@Override
	public Type getType() {
		return Type.COUNTRY;
	}
	
}
