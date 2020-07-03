package ca.magex.crm.api.system.id;

import ca.magex.crm.api.Crm;

/**
 * A Specific Identifier used for Dictionaries Option Identification
 * 
 * @author Jonny
 */
public class DictionaryIdentifier extends OptionIdentifier {

	private static final long serialVersionUID = Crm.SERIAL_UID_VERSION;
	
	public static final String CONTEXT = OptionIdentifier.CONTEXT + "dictionaries/";

	public DictionaryIdentifier(CharSequence id) {
		super(id);
	}
	
	@Override
	public String getContext() {
		return DictionaryIdentifier.CONTEXT;
	}
}
