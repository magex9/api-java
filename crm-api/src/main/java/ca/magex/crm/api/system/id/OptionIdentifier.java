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
	
	public static String CONTEXT = Identifier.CONTEXT +  "options/";
	
	protected OptionIdentifier(CharSequence id) {
		super(id);
	}
	
	@Override
	public String getContext() {		
		return CONTEXT;
	}
	
	public static OptionIdentifier forId(CharSequence id) {	
		if (StringUtils.startsWith(id, AuthenticationGroupIdentifier.CONTEXT)) {		
			return new AuthenticationGroupIdentifier(id);
		} else if (StringUtils.startsWith(id, AuthenticationRoleIdentifier.CONTEXT)) {
			return new AuthenticationRoleIdentifier(id);
		} else if (StringUtils.startsWith(id, BusinessGroupIdentifier.CONTEXT)) {
			return new BusinessGroupIdentifier(id);
		} else if (StringUtils.startsWith(id, BusinessRoleIdentifier.CONTEXT)) {
			return new BusinessRoleIdentifier(id);
		} else if (StringUtils.startsWith(id, CountryIdentifier.CONTEXT)) {
			return new CountryIdentifier(id);
		} else if (StringUtils.startsWith(id, ProvinceIdentifier.CONTEXT)) {
			return new ProvinceIdentifier(id);
		} else if (StringUtils.startsWith(id, LanguageIdentifier.CONTEXT)) {
			return new LanguageIdentifier(id);
		} else if (StringUtils.startsWith(id, LocaleIdentifier.CONTEXT)) {
			return new LocaleIdentifier(id);
		} else if (StringUtils.startsWith(id, SalutationIdentifier.CONTEXT)) {
			return new SalutationIdentifier(id);
		} else if (StringUtils.startsWith(id, StatusIdentifier.CONTEXT)) {
			return new StatusIdentifier(id);
		} else {
			throw new IllegalArgumentException("Unidentifiable id");
		}
	}
}