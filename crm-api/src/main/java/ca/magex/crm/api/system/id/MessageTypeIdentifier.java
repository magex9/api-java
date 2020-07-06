package ca.magex.crm.api.system.id;

import ca.magex.crm.api.Crm;
import ca.magex.crm.api.system.Type;

/**
 * A Specific Identifier used for Message Type Option Identification
 * 
 * @author Jonny
 */
public class MessageTypeIdentifier extends OptionIdentifier {

	private static final long serialVersionUID = Crm.SERIAL_UID_VERSION;
	
	public static final String CONTEXT = OptionIdentifier.CONTEXT + "message-types/";

	public MessageTypeIdentifier(CharSequence id) {
		super(id);
	}
	
	@Override
	public String getContext() {
		return MessageTypeIdentifier.CONTEXT;
	}
	
	@Override
	public Type getType() {
		return Type.MESSAGE_TYPE;
	}
	
}
