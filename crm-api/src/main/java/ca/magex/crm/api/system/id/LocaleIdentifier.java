package ca.magex.crm.api.system.id;

import ca.magex.crm.api.Crm;

/**
 * A Specific Identifier used for Locale Option Identification
 * 
 * @author Jonny
 */
public class LocaleIdentifier extends OptionIdentifier {

	private static final long serialVersionUID = Crm.SERIAL_UID_VERSION;

	public LocaleIdentifier(CharSequence id) {
		super(id);
	}
	
	@Override
	public String getContext() {
		return super.getContext() + "locales/";
	}
}