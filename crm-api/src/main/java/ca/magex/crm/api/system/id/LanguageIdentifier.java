package ca.magex.crm.api.system.id;

import ca.magex.crm.api.Crm;
import ca.magex.crm.api.system.Type;

/**
 * A Specific Identifier used for Language Option Identification
 * 
 * @author Jonny
 */
public class LanguageIdentifier extends OptionIdentifier {

	private static final long serialVersionUID = Crm.SERIAL_UID_VERSION;
	
	public static final String CONTEXT = OptionIdentifier.CONTEXT + "languages/";
	
	public static final LanguageIdentifier ENGLISH = new LanguageIdentifier("EN");

	public static final LanguageIdentifier FRENCH = new LanguageIdentifier("FR");

	public LanguageIdentifier(CharSequence id) {
		super(id);
	}
	
	@Override
	public String getContext() {
		return LanguageIdentifier.CONTEXT;
	}
	
	@Override
	public Type getType() {
		return Type.LANGUAGE;
	}
	
}
