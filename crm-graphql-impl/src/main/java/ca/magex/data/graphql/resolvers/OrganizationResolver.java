package ca.magex.data.graphql.resolvers;

import org.springframework.stereotype.Component;

import com.coxautodev.graphql.tools.GraphQLResolver;

import ca.magex.crm.api.crm.Location;
import ca.magex.crm.api.crm.Organization;
import ca.magex.crm.api.services.OrganizationService;

@Component
public class OrganizationResolver implements GraphQLResolver<Organization> {
	
	private OrganizationService organizations;

	public OrganizationResolver(OrganizationService organizations) {
		this.organizations = organizations;
	}
	
	public String getId(Organization organization) {
		return organization.getOrganizationId().toString();
	}
	
	public String getStatus(Organization organization) {
		return organization.getStatus().toString();
	}
    
	public Location getMainLocation(Organization organization) {
		try {
			return organizations.findLocation(organization.getMainLocationId());
		} catch (Exception e) {
			return null;
		}
	}
	
}