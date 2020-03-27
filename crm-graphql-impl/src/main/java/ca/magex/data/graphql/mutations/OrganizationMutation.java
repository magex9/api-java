package ca.magex.data.graphql.mutations;

import org.springframework.stereotype.Component;

import com.coxautodev.graphql.tools.GraphQLMutationResolver;

import ca.magex.crm.api.crm.Organization;
import ca.magex.crm.api.services.OrganizationService;
import ca.magex.crm.api.system.Identifier;

@Component
public class OrganizationMutation implements GraphQLMutationResolver {

	private OrganizationService organizations;

	public OrganizationMutation(OrganizationService organizations) {
		this.organizations = organizations;
	}

	public Organization createOrganization(String organizationName) {
		return organizations.createOrganization(organizationName);
	}

	public Organization enableOrganization(String organizationId) {
		return organizations.enableOrganization(new Identifier(organizationId));
	}

	public Organization disableOrganization(String organizationId) {
		return organizations.disableOrganization(new Identifier(organizationId));
	}

	public Organization updateOrganizationName(String organizationId, String name) {
		return organizations.updateOrganizationName(new Identifier(organizationId), name);
	}

	public Organization updateMainLocation(String organizationId, String locationId) {
		return organizations.updateOrganizationMainLocation(new Identifier(organizationId), new Identifier(locationId));
	}

}