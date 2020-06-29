package ca.magex.crm.api.system.id;

import ca.magex.crm.api.Crm;

/**
 * A Specific Identifier used for Province Option Identification
 * 
 * @author Jonny
 */
public class ProvinceIdentifier extends OptionIdentifier {

	private static final long serialVersionUID = Crm.SERIAL_UID_VERSION;

	public static final String CONTEXT = OptionIdentifier.CONTEXT + "provinces/";

	public ProvinceIdentifier(CharSequence id) {
		super(id);
	}
	
	@Override
	public String getContext() {
		return ProvinceIdentifier.CONTEXT;
	}
}
