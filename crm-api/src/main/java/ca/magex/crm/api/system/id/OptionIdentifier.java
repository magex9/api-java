package ca.magex.crm.api.system.id;

import org.apache.commons.lang3.StringUtils;

import ca.magex.crm.api.Crm;
import ca.magex.crm.api.system.Identifier;

/**
 * A Specific Identifier used for Option Identification
 * 
 * @author Jonny
 */
public class OptionIdentifier extends Identifier {

	private static final long serialVersionUID = Crm.SERIAL_UID_VERSION;
	
	protected OptionIdentifier(CharSequence id) {
		super(id);
	}
	
	@Override
	public String getContext() {		
		return super.getContext() + "options/";
	}
	
	public static OptionIdentifier forId(CharSequence id) {	
		if (StringUtils.startsWith(id, "/options/authenticationGroups/")) {		
			return new AuthenticationGroupIdentifier(id);
		} else if (StringUtils.startsWith(id, "/options/authenticationRoles/")) {
			return new AuthenticationRoleIdentifier(id);
		} else if (StringUtils.startsWith(id, "/options/businessGroups/")) {
			return new BusinessGroupIdentifier(id);
		} else if (StringUtils.startsWith(id, "/options/businessRoles/")) {
			return new BusinessRoleIdentifier(id);
		} else if (StringUtils.startsWith(id, "/options/countries/")) {
			return new CountryIdentifier(id);
		} else if (StringUtils.startsWith(id, "/options/provinces/")) {
			return new ProvinceIdentifier(id);
		} else if (StringUtils.startsWith(id, "/options/languages/")) {
			return new LanguageIdentifier(id);
		} else if (StringUtils.startsWith(id, "/options/locales/")) {
			return new LocaleIdentifier(id);
		} else if (StringUtils.startsWith(id, "/options/salutations/")) {
			return new SalutationIdentifier(id);
		} else if (StringUtils.startsWith(id, "/options/statuses/")) {
			return new StatusIdentifier(id);
		} else {
			throw new IllegalArgumentException("Unidentifiable id");
		}
	}
}