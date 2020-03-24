package ca.magex.crm.graphql.datafetcher;

import ca.magex.crm.api.common.MailingAddress;
import ca.magex.crm.api.crm.Location;
import graphql.schema.DataFetcher;

/**
 * Fetcher for retrieving Address Components
 * @author Jonny
 *
 */
public class AddressDataFetcher {
		
	/**
	 * returns a data fetcher for retrieving a location by it's id
	 * @return
	 */
	public DataFetcher<MailingAddress> byLocation() {
		return (environment) -> {
			Location location = environment.getSource();
			return location.getAddress();
		};
	}
}
