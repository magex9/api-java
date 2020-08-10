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
import ca.magex.crm.api.system.id.DictionaryIdentifier;
import ca.magex.crm.api.system.id.LanguageIdentifier;
import ca.magex.crm.api.system.id.LocaleIdentifier;
import ca.magex.crm.api.system.id.MessageTypeIdentifier;
import ca.magex.crm.api.system.id.OptionIdentifier;
import ca.magex.crm.api.system.id.PhraseIdentifier;
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
		case DICTIONARY:
			return generateDictionaryId(lookup);
		case PHRASE:
			return generatePhraseId(lookup);
		case MESSAGE_TYPE:
			return generateMessageTypeId(lookup);
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
	
	default AuthenticationGroupIdentifier generateAuthenticationGroupId(String optionId) {
		return new AuthenticationGroupIdentifier(optionId);
	}
	
	default AuthenticationRoleIdentifier generateAuthenticationRoleId(String optionId) {
		return new AuthenticationRoleIdentifier(optionId);
	}
	
	default BusinessGroupIdentifier generateBusinessGroupId(String optionId) {
		return new BusinessGroupIdentifier(optionId);
	}
	
	default BusinessRoleIdentifier generateBusinessRoleId(String optionId) {
		return new BusinessRoleIdentifier(optionId);
	}
	
	default DictionaryIdentifier generateDictionaryId(String optionId) {
		return new DictionaryIdentifier(optionId);
	}
	
	default PhraseIdentifier generatePhraseId(String optionId) {
		return new PhraseIdentifier(optionId);
	}
	
	default MessageTypeIdentifier generateMessageTypeId(String optionId) {
		return new MessageTypeIdentifier(optionId);
	}
	
	default StatusIdentifier generateStatusId(String optionId) {
		return new StatusIdentifier(optionId);
	}
	
	default LocaleIdentifier generateLocaleId(String optionId) {
		return new LocaleIdentifier(optionId);
	}
	
	default LanguageIdentifier generateLanguageId(String optionId) {
		return new LanguageIdentifier(optionId);
	}
	
	default SalutationIdentifier generateSalutationId(String optionId) {
		return new SalutationIdentifier(optionId);
	}
	
	default CountryIdentifier generateCountryId(String option) {
		return new CountryIdentifier(option);
	}
	
	default ProvinceIdentifier generateProvinceId(String option) {
		return new ProvinceIdentifier(option);
	}
	
	public FilteredPage<Option> findOptions(OptionsFilter filter, Paging paging);
	
	public long countOptions(OptionsFilter filter);
	
	public Option findOption(OptionIdentifier optionId);

	public Option saveOption(Option option);

}
