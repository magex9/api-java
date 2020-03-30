package ca.magex.data.graphql.resolvers;

import org.springframework.stereotype.Component;

import com.coxautodev.graphql.tools.GraphQLResolver;

import ca.magex.crm.api.crm.LocationDetails;
import ca.magex.crm.api.crm.OrganizationDetails;
import ca.magex.crm.api.services.OrganizationService;

@Component
public class OrganizationResolver implements GraphQLResolver<OrganizationDetails> {
	
	private OrganizationService organizations;

	public OrganizationResolver(OrganizationService organizations) {
		this.organizations = organizations;
	}
	
	public String getId(OrganizationDetails organization) {
		return organization.getOrganizationId().toString();
	}
	
	public String getStatus(OrganizationDetails organization) {
		return organization.getStatus().toString();
	}
    
	public LocationDetails getMainLocation(OrganizationDetails organization) {
		try {
			return organizations.findLocation(organization.getMainLocationId());
		} catch (Exception e) {
			return null;
		}
	}
	
}