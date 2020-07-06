package ca.magex.crm.api.common;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import ca.magex.crm.api.Crm;
import ca.magex.crm.api.system.Identifier;

/**
 * A Specific List for holding Identifiers which has a proper JSON Style toString
 * 
 * @author Jonny
 * @param <I>
 */
public class IdentifierList<I extends Identifier> extends ArrayList<I> {

	private static final long serialVersionUID = Crm.SERIAL_UID_VERSION;
	
	public IdentifierList() {}

	public IdentifierList(List<I> values) {
		super(values);
	}
	
	@Override
	public int hashCode() {
		return super.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		return super.equals(obj);
	}
	
	@Override
	public String toString() {
		return stream().map((identifier) -> "\"" + identifier.getId() + "\"").collect(Collectors.joining(",", "[", "]"));
	}
}