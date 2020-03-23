package ca.magex.data.graphql.resolvers;

import org.springframework.stereotype.Component;

import com.coxautodev.graphql.tools.GraphQLResolver;

import ca.magex.crm.api.crm.Location;
import ca.magex.crm.api.services.OrganizationService;

@Component
public class LocationResolver implements GraphQLResolver<Location> {
	
	private OrganizationService organizations;

	public LocationResolver(OrganizationService organizations) {
		this.organizations = organizations;
	}
	
	public String getId(Location location) {
		return location.getLocationId().toString();
	}
	
	public String getStatus(Location location) {
		return location.getStatus().toString();
	}
    	
}