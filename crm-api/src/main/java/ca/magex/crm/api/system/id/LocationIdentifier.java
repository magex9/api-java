package ca.magex.crm.api.system.id;

import ca.magex.crm.api.Crm;
import ca.magex.crm.api.system.Identifier;

/**
 * A Specific Identifier used for Location Identification
 * 
 * @author Jonny
 */
public class LocationIdentifier extends Identifier {

	private static final long serialVersionUID = Crm.SERIAL_UID_VERSION;
	
	public static final String CONTEXT = Identifier.CONTEXT + "locations/";	

	public LocationIdentifier(String id) {
		super(id);
	}
	
	public LocationIdentifier(CharSequence id) {
		super(id);
	}
	
	@Override
	public String getContext() {
		return LocationIdentifier.CONTEXT;
	}
}