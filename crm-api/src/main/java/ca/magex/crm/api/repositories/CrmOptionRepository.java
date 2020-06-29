package ca.magex.crm.api.repositories;

import ca.magex.crm.api.filters.OptionsFilter;
import ca.magex.crm.api.filters.Paging;
import ca.magex.crm.api.system.FilteredPage;
import ca.magex.crm.api.system.Option;
import ca.magex.crm.api.system.Type;
import ca.magex.crm.api.system.id.AuthenticationGroupIdentifier;
import ca.magex.crm.api.system.id.AuthenticationRoleIdentifier;
import ca.magex.crm.api.system.id.BusinessGroupIdentifier;
import ca.magex.crm.api.system.id.BusinessRoleIdentifier;
import ca.magex.crm.api.system.id.CountryIdentifier;
import ca.magex.crm.api.system.id.LanguageIdentifier;
import ca.magex.crm.api.system.id.LocaleIdentifier;
import ca.magex.crm.api.system.id.OptionIdentifier;
import ca.magex.crm.api.system.id.ProvinceIdentifier;
import ca.magex.crm.api.system.id.SalutationIdentifier;
import ca.magex.crm.api.system.id.StatusIdentifier;

public interface CrmOptionRepository {
	
	default OptionIdentifier generateForType(Type type, String lookup) {
		switch (type) {
		case AUTHENTICATION_GROUP:
			return generateAuthenticationGroupId(lookup);
		case AUTHENTICATION_ROLE:
			return generateAuthenticationRoleId(lookup);
		case BUSINESS_GROUP:
			return generateBusinessGroupId(lookup);
		case BUSINESS_ROLE:
			return generateBusinessRoleId(lookup);
		case COUNTRY:
			return generateCountryId(lookup);
		case PROVINCE:
			return generateProvinceId(lookup);
		case LANGUAGE:
			return generateLanguageId(lookup);
		case LOCALE:
			return generateLocaleId(lookup);
		case SALUTATION:
			return generateSalutationId(lookup);
		case STATUS:
			return generateStatusId(lookup);
		default:
			throw new IllegalArgumentException("Unknown Type: " + type);
		}
	}
	
	default AuthenticationGroupIdentifier generateAuthenticationGroupId(String lookup) {
		return new AuthenticationGroupIdentifier(lookup);
	}
	
	default AuthenticationRoleIdentifier generateAuthenticationRoleId(String lookup) {
		return new AuthenticationRoleIdentifier(lookup);
	}
	
	default BusinessGroupIdentifier generateBusinessGroupId(String lookup) {
		return new BusinessGroupIdentifier(lookup);
	}
	
	default BusinessRoleIdentifier generateBusinessRoleId(String lookup) {
		return new BusinessRoleIdentifier(lookup);
	}
	
	default StatusIdentifier generateStatusId(String lookup) {
		return new StatusIdentifier(lookup);
	}
	
	default LocaleIdentifier generateLocaleId(String lookup) {
		return new LocaleIdentifier(lookup);
	}
	
	default LanguageIdentifier generateLanguageId(String lookup) {
		return new LanguageIdentifier(lookup);
	}
	
	default SalutationIdentifier generateSalutationId(String lookup) {
		return new SalutationIdentifier(lookup);
	}
	
	default CountryIdentifier generateCountryId(String lookup) {
		return new CountryIdentifier(lookup);
	}
	
	default ProvinceIdentifier generateProvinceId(String lookup) {
		return new ProvinceIdentifier(lookup);
	}
	
	public FilteredPage<Option> findOptions(OptionsFilter filter, Paging paging);
	
	public long countOptions(OptionsFilter filter);
	
	public Option findOption(OptionIdentifier lookupId);

	public Option saveOption(Option lookup);

}
