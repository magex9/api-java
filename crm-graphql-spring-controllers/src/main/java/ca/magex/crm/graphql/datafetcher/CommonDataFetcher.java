package ca.magex.crm.graphql.datafetcher;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import ca.magex.crm.api.common.Communication;
import ca.magex.crm.api.common.MailingAddress;
import ca.magex.crm.api.common.PersonName;
import ca.magex.crm.api.system.Localized;
import ca.magex.crm.api.system.Option;
import graphql.schema.DataFetcher;

@Component
public class CommonDataFetcher extends AbstractDataFetcher {

	private static Logger logger = LoggerFactory.getLogger(LocationDataFetcher.class);
	
	public DataFetcher<Map<String,String>> getCountryChoice() {
		return (environment) -> {
			logger.debug("Entering getCountryValue@" + CommonDataFetcher.class.getSimpleName());
			MailingAddress source = environment.getSource();
			if (source.getCountry() == null) {
				return null;
			}
			Map<String,String> mapped = new HashMap<>();
			if (source.getCountry().getIdentifier() != null) {
				mapped.put("code", source.getCountry().getIdentifier().getCode());
			}			
			if (source.getCountry().getOther() != null) {
				mapped.put("other", source.getCountry().getOther());
			}
			return mapped;
		};
	}
	
	public DataFetcher<Map<String,String>> getProvinceChoice() {
		return (environment) -> {
			logger.debug("Entering getProvinceValue@" + CommonDataFetcher.class.getSimpleName());
			MailingAddress source = environment.getSource();
			if (source.getProvince() == null) {
				return null;
			}
			Map<String,String> mapped = new HashMap<>();
			if (source.getProvince().getIdentifier() != null) {
				mapped.put("code", source.getProvince().getIdentifier().getCode());
			}			
			if (source.getProvince().getOther() != null) {
				mapped.put("other", source.getProvince().getOther());
			}
			return mapped;
		};
	}
	
	public DataFetcher<Map<String,String>> getSalutationValue() {
		return (environment) -> {
			logger.debug("Entering getSalutationValue@" + CommonDataFetcher.class.getSimpleName());
			PersonName source = environment.getSource();
			if (source.getSalutation() == null) {
				return null;
			}
			Map<String,String> mapped = new HashMap<>();
			if (source.getSalutation().getIdentifier() != null) {
				mapped.put("code", source.getSalutation().getIdentifier().getCode());
			}			
			if (source.getSalutation().getOther() != null) {
				mapped.put("other", source.getSalutation().getOther());
			}
			return mapped;
		};
	}
	
	public DataFetcher<Map<String,String>> getLanguageValue() {
		return (environment) -> {
			logger.debug("Entering getLanguageValue@" + CommonDataFetcher.class.getSimpleName());
			Communication source = environment.getSource();
			if (source.getLanguage() == null) {
				return null;
			}
			Map<String,String> mapped = new HashMap<>();
			if (source.getLanguage().getIdentifier() != null) {
				mapped.put("code", source.getLanguage().getIdentifier().getCode());
			}			
			if (source.getLanguage().getOther() != null) {
				mapped.put("other", source.getLanguage().getOther());
			}
			return mapped;
		};
	}
	
	public DataFetcher<String> getEnglishValue() {
		return (environment) -> {
			logger.debug("Entering getEnglishValue@" + CommonDataFetcher.class.getSimpleName());
			Localized source = environment.getSource();
			return source.getEnglishName();
		};
	}
	
	public DataFetcher<String> getFrenchValue() {
		return (environment) -> {
			logger.debug("Entering getFrenchValue@" + CommonDataFetcher.class.getSimpleName());
			Localized source = environment.getSource();
			return source.getFrenchName();
		};
	}
	
	public DataFetcher<String> getOptionTypeValue() {
		return (environment) -> {
			logger.debug("Entering getOptionTypeValue@" + CommonDataFetcher.class.getSimpleName());
			Option source = environment.getSource();
			return source.getType().getCode();
		};
	}
}
