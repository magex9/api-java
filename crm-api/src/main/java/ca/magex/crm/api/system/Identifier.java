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
	public boolean equals(Object obj) {
		if (obj instanceof Identifier)
			return id.equals(((Identifier)obj).id);
		return false;
	}
	
	@Override
	public int hashCode() {
		return id.hashCode();
	}
	
	@Override
	public String toString() {
		return id;
	}
	
}
