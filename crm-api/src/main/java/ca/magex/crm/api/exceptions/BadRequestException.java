package ca.magex.crm.api.exceptions;

import java.util.Arrays;
import java.util.List;

import ca.magex.crm.api.services.Crm;
import ca.magex.crm.api.system.Identifier;
import ca.magex.crm.api.system.Localized;
import ca.magex.crm.api.system.Message;

public class BadRequestException extends ApiException {

	private static final long serialVersionUID = Crm.SERIAL_UID_VERSION;
	
	private List<Message> messages;

	public BadRequestException(String message, List<Message> messages) {
		super("Bad Request: " + message);
		this.messages = messages;
	}
	
	public BadRequestException(String message, Identifier base, String type, String path, Localized reason) {
		this(message, Arrays.asList(new Message(base, type, path, reason)));
	}

	@Override
	public Integer getErrorCode() {
		return 400;
	}
	
	public List<Message> getMessages() {
		return messages;
	}
	
}
