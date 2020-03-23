package ca.magex.crm.api.exceptions;

import java.util.Arrays;
import java.util.List;

import ca.magex.crm.api.system.Message;

public class BadRequestException extends ApiException {

	private static final long serialVersionUID = 1L;

	public BadRequestException(List<Message> messages) {
		super("Bad Request: " + messages);
	}
	
	public BadRequestException(Object base, String type, String path, String message) {
		this(Arrays.asList(new Message(base, type, path, message)));
	}

	@Override
	public int getErrorCode() {
		return 400;
	}
	
}
