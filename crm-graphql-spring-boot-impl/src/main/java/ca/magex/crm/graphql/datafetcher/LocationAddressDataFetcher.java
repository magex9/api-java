package ca.magex.crm.graphql.datafetcher;

import ca.magex.crm.api.common.MailingAddress;
import ca.magex.crm.api.crm.Location;
import graphql.schema.DataFetcher;
import graphql.schema.DataFetchingEnvironment;

public class LocationAddressDataFetcher implements DataFetcher<MailingAddress> {
		
	@Override
	public MailingAddress get(DataFetchingEnvironment environment) {
		Location location = environment.getSource();
		return location.getAddress();
	}
}
