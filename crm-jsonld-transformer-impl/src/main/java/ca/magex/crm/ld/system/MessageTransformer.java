package ca.magex.crm.ld.system;

import ca.magex.crm.api.system.Identifier;
import ca.magex.crm.api.system.Message;
import ca.magex.crm.ld.AbstractLinkedDataTransformer;
import ca.magex.crm.ld.data.DataObject;

public class MessageTransformer extends AbstractLinkedDataTransformer<Message> {

	@Override
	public String getType() {
		return "message";
	}
	
	@Override
	public DataObject format(Message message) {
		return base()
			.with("identifier", message.getIdentifier().toString())
			.with("type", message.getType())
			.with("path", message.getPath())
			.with("message", message.getMessage());
	}

	@Override
	public Message parse(DataObject data) {
		validateContext(data);
		validateType(data);
		Identifier identifier = new Identifier(data.getString("identifier"));
		String type = data.getString("type");
		String path = data.getString("path");
		String message = data.getString("message");
		return new Message(identifier, type, path, message);
	}
			
}
