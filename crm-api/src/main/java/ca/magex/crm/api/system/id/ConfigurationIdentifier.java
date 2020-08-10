package ca.magex.crm.api.system.id;

import ca.magex.crm.api.Crm;
import ca.magex.crm.api.system.Identifier;

/**
 * A Specific Identifier used for Configuration Identification
 * 
 * @author Jonny
 */
public class ConfigurationIdentifier extends Identifier {

	private static final long serialVersionUID = Crm.SERIAL_UID_VERSION;
	
	public static final String CONTEXT = Identifier.CONTEXT + "configurations/";

	public ConfigurationIdentifier(CharSequence id) {
		super(id);
	}
	
	@Override
	public String getContext() {
		return ConfigurationIdentifier.CONTEXT;
	}
}