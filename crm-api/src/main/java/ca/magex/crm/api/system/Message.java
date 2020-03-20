package ca.magex.crm.api.system;

public class Message {

	private Object base;
	
	private String type;
	
	private String path;
	
	private String message;

	public Message(Object base, String type, String path, String message) {
		super();
		this.base = base;
		this.type = type;
		this.path = path;
		this.message = message;
	}

	public Object getBase() {
		return base;
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
