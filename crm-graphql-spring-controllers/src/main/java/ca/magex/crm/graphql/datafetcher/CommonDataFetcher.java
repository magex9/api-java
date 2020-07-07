package ca.magex.crm.graphql.datafetcher;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import ca.magex.crm.api.common.MailingAddress;
import graphql.schema.DataFetcher;

@Component
public class CommonDataFetcher extends AbstractDataFetcher {

	private static Logger logger = LoggerFactory.getLogger(LocationDataFetcher.class);
	
	public DataFetcher<String> getCountryValue() {
		return (environment) -> {
			logger.debug("Entering getCountryValue@" + AbstractDataFetcher.class.getSimpleName());
			MailingAddress source = environment.getSource();
			if (source.getCountry() == null) {
				return null;
			}
			return source.getCountry().getValue();
		};
	}
	
	public DataFetcher<String> getProvinceValue() {
		return (environment) -> {
			logger.debug("Entering getProvinceValue@" + AbstractDataFetcher.class.getSimpleName());
			MailingAddress source = environment.getSource();
			if (source.getProvince() == null) {
				return null;
			}
			return source.getProvince().getValue();
		};
	}
}
