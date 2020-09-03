package ca.magex.crm.restful.client.sample;

import java.util.List;

import ca.magex.crm.api.crm.OrganizationDetails;
import ca.magex.crm.api.filters.OrganizationsFilter;
import ca.magex.crm.api.services.CrmServices;
import ca.magex.crm.api.system.id.AuthenticationGroupIdentifier;
import ca.magex.crm.api.system.id.BusinessGroupIdentifier;
import ca.magex.crm.restful.client.RestTemplateClient;

public class RestfulServerTest {

	public static void main(String[] args) {
		CrmServices crm = new RestTemplateClient("http://localhost:9002/crm", null, "admin", "admin").getServices();
		crm.findOrganizationDetails(new OrganizationsFilter()).getContent().forEach(o -> System.out.println(o.getDisplayName() + " (" + o.getOrganizationId() + ")"));
		OrganizationDetails org = crm.createOrganization("Scotts Org", List.of(AuthenticationGroupIdentifier.ORG), List.of(BusinessGroupIdentifier.EXTERNAL));
		System.out.println(org.getDisplayName() + " (" + org.getOrganizationId() + ")");
	}
	
}
