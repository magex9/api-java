package ca.magex.crm.api.system.id;

import ca.magex.crm.api.Crm;

/**
 * A Specific Identifier used for Phrase Identification
 * 
 * @author Jonny
 */
public class PhraseIdentifier extends OptionIdentifier {

	private static final long serialVersionUID = Crm.SERIAL_UID_VERSION;
	
	public static final String CONTEXT = OptionIdentifier.CONTEXT + "phrases/";

	public PhraseIdentifier(CharSequence id) {
		super(id);
	}
	
	@Override
	public String getContext() {
		return PhraseIdentifier.CONTEXT;
	}
}
