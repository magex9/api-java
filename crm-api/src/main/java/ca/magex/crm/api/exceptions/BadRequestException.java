package ca.magex.crm.api.exceptions;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import ca.magex.crm.api.Crm;
import ca.magex.crm.api.system.Choice;
import ca.magex.crm.api.system.Identifier;
import ca.magex.crm.api.system.Message;
import ca.magex.crm.api.system.id.MessageIdentifier;
import ca.magex.crm.api.system.id.MessageTypeIdentifier;

public class BadRequestException extends ApiException {

	private static final long serialVersionUID = Crm.SERIAL_UID_VERSION;
	
	private List<Message> messages;

	public BadRequestException(String message, List<Message> messages) {
		super("Bad Request: " + message);
		this.messages = messages;
	}
	
	public BadRequestException(String message, Identifier base, MessageTypeIdentifier type, String path, Choice<MessageIdentifier> reason) {
		this(message, Arrays.asList(new Message(base, type, path, reason)));
	}

	@Override
	public Integer getErrorCode() {
		return 400;
	}
	
	public List<Message> getMessages() {
		return messages;
	}
	
	public void printMessages(OutputStream os) throws IOException {
		for (Message message : messages) {
			os.write(message.toString().getBytes());
		}
	}
	
	@Override
	public String toString() {
		return getMessage() + "\n\t" + StringUtils.join(messages, "\n\t");
	}
	
}
