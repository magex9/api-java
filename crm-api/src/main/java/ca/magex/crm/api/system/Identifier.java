package ca.magex.crm.api.system;

public class Identifier {

	private String id;
	
	public Identifier(String id) {
		this.id = id;
	}
	
	public Identifier(Identifier identifier) {
		this.id = identifier.id;
	}
	
	@Override
	public String toString() {
		return id;
	}
	
}
