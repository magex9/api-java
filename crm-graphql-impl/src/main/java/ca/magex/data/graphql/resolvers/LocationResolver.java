package ca.magex.data.graphql.resolvers;

import org.springframework.stereotype.Component;

import com.coxautodev.graphql.tools.GraphQLResolver;

import ca.magex.crm.api.crm.LocationDetails;
import ca.magex.crm.api.services.OrganizationService;

@Component
public class LocationResolver implements GraphQLResolver<LocationDetails> {
	
	private OrganizationService organizations;

	public LocationResolver(OrganizationService organizations) {
		this.organizations = organizations;
	}
	
	public String getId(LocationDetails location) {
		return location.getLocationId().toString();
	}
	
	public String getStatus(LocationDetails location) {
		return location.getStatus().toString();
	}
    	
}