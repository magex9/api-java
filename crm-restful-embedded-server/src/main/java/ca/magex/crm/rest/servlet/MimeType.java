package ca.magex.crm.rest.servlet;

public enum MimeType {

	HTML("text.html"),
	JAVASCRIPT("text/javascript"),
	CSS("text/css"),
	JSON_LD("application/ld+json"),
	JSON("application/json"),
	YAML("application/yaml"),
	TURTLE("text/turtle");
	
	private String type;
	
	private MimeType(String type) {
		this.type = type;
	}
	
	@Override
	public String toString() {
		return type;
	}
	
}
