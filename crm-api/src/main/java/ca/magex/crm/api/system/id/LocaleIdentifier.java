package ca.magex.crm.api.system.id;

import ca.magex.crm.api.Crm;
import ca.magex.crm.api.system.Type;

/**
 * A Specific Identifier used for Locale Option Identification
 * 
 * @author Jonny
 */
public class LocaleIdentifier extends OptionIdentifier {

	private static final long serialVersionUID = Crm.SERIAL_UID_VERSION;
	
	public static final String CONTEXT = OptionIdentifier.CONTEXT + "locales/";

	public LocaleIdentifier(CharSequence id) {
		super(id);
	}
	
	@Override
	public String getContext() {
		return LocaleIdentifier.CONTEXT;
	}
	
	@Override
	public Type getType() {
		return Type.LOCALE;
	}
	
}
