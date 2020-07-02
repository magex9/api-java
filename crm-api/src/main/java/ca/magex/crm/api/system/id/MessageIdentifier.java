package ca.magex.crm.api.system.id;

import ca.magex.crm.api.Crm;

/**
 * A Specific Identifier used for Message Option Identification
 * 
 * @author Jonny
 */
public class MessageIdentifier extends OptionIdentifier {

	private static final long serialVersionUID = Crm.SERIAL_UID_VERSION;
	
	public static final String CONTEXT = OptionIdentifier.CONTEXT + "messages/";

	public MessageIdentifier(CharSequence id) {
		super(id);
	}
	
	@Override
	public String getContext() {
		return MessageIdentifier.CONTEXT;
	}
}
