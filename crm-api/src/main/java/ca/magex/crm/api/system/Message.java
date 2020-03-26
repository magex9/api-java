package ca.magex.crm.api.system;

public class Message {

	private Identifier identifier;
	
	private String type;
	
	private String path;
	
	private String message;

	public Message(Identifier identifier, String type, String path, String message) {
		super();
		this.identifier = identifier;
		this.type = type;
		this.path = path;
		this.message = message;
	}

	public Identifier getIdentifier() {
		return identifier;
	}

	public String getType() {
		return type;
	}

	public String getPath() {
		return path;
	}

	public String getMessage() {
		return message;
	}
	
}
