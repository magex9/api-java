package ca.magex.crm.graphql.datafetcher;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import ca.magex.crm.api.common.Communication;
import ca.magex.crm.api.common.MailingAddress;
import ca.magex.crm.api.common.PersonName;
import ca.magex.crm.api.system.Localized;
import graphql.schema.DataFetcher;

@Component
public class CommonDataFetcher extends AbstractDataFetcher {

	private static Logger logger = LoggerFactory.getLogger(LocationDataFetcher.class);
	
	public DataFetcher<String> getCountryValue() {
		return (environment) -> {
			logger.debug("Entering getCountryValue@" + CommonDataFetcher.class.getSimpleName());
			MailingAddress source = environment.getSource();
			if (source.getCountry() == null) {
				return null;
			}
			return source.getCountry().getValue();
		};
	}
	
	public DataFetcher<String> getProvinceValue() {
		return (environment) -> {
			logger.debug("Entering getProvinceValue@" + CommonDataFetcher.class.getSimpleName());
			MailingAddress source = environment.getSource();
			if (source.getProvince() == null) {
				return null;
			}
			return source.getProvince().getValue();
		};
	}
	
	public DataFetcher<String> getSalutationValue() {
		return (environment) -> {
			logger.debug("Entering getSalutationValue@" + CommonDataFetcher.class.getSimpleName());
			PersonName source = environment.getSource();
			if (source.getSalutation() == null) {
				return null;
			}
			return source.getSalutation().getValue();
		};
	}
	
	public DataFetcher<String> getLanguageValue() {
		return (environment) -> {
			logger.debug("Entering getLanguageValue@" + CommonDataFetcher.class.getSimpleName());
			Communication source = environment.getSource();
			if (source.getLanguage() == null) {
				return null;
			}
			return source.getLanguage().getValue();
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
}
