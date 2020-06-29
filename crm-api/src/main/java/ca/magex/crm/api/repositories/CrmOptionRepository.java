package ca.magex.crm.api.repositories;

import ca.magex.crm.api.filters.OptionsFilter;
import ca.magex.crm.api.filters.Paging;
import ca.magex.crm.api.store.CrmStore;
import ca.magex.crm.api.system.FilteredPage;
import ca.magex.crm.api.system.Identifier;
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
	
	default OptionIdentifier generateForType(Type type) {
		switch (type) {
		case AUTHENTICATION_GROUP:
			return generateAuthenticationGroupId();
		case AUTHENTICATION_ROLE:
			return generateAuthenticationRoleId();
		case BUSINESS_GROUP:
			return generateBusinessGroupId();
		case BUSINESS_ROLE:
			return generateBusinessRoleId();
		case COUNTRY:
			return generateCountryId();
		case PROVINCE:
			return generateProvinceId();
		case LANGUAGE:
			return generateLanguageId();
		case LOCALE:
			return generateLocaleId();
		case SALUTATION:
			return generateSalutationId();
		case STATUS:
			return generateStatusId();
		default:
			throw new IllegalArgumentException("Unknown Type: " + type);
		}
	}
	
	default AuthenticationGroupIdentifier generateAuthenticationGroupId() {
		return new AuthenticationGroupIdentifier(CrmStore.generateId());
	}
	
	default AuthenticationRoleIdentifier generateAuthenticationRoleId() {
		return new AuthenticationRoleIdentifier(CrmStore.generateId());
	}
	
	default BusinessGroupIdentifier generateBusinessGroupId() {
		return new BusinessGroupIdentifier(CrmStore.generateId());
	}
	
	default BusinessRoleIdentifier generateBusinessRoleId() {
		return new BusinessRoleIdentifier(CrmStore.generateId());
	}
	
	default StatusIdentifier generateStatusId() {
		return new StatusIdentifier(CrmStore.generateId());
	}
	
	default LocaleIdentifier generateLocaleId() {
		return new LocaleIdentifier(CrmStore.generateId());
	}
	
	default LanguageIdentifier generateLanguageId() {
		return new LanguageIdentifier(CrmStore.generateId());
	}
	
	default SalutationIdentifier generateSalutationId() {
		return new SalutationIdentifier(CrmStore.generateId());
	}
	
	default CountryIdentifier generateCountryId() {
		return new CountryIdentifier(CrmStore.generateId());
	}
	
	default ProvinceIdentifier generateProvinceId() {
		return new ProvinceIdentifier(CrmStore.generateId());
	}
	
	public FilteredPage<Option> findOptions(OptionsFilter filter, Paging paging);
	
	public long countOptions(OptionsFilter filter);
	
	public Option findOption(Identifier lookupId);

	public Option saveOption(Option lookup);

}
