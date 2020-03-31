package ca.magex.crm.rest.endpoint.organizations;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ca.magex.crm.api.crm.OrganizationDetails;
import ca.magex.crm.api.exceptions.ItemNotFoundException;
import ca.magex.crm.api.exceptions.PermissionDeniedException;
import ca.magex.crm.api.services.SecuredOrganizationService;
import ca.magex.crm.ld.crm.OrganizationDetailsTransformer;
import ca.magex.crm.rest.endpoint.AbstractEndpoint;

public class CreateOrganizationEndpoint extends AbstractEndpoint<OrganizationDetails> {
	
	public CreateOrganizationEndpoint(SecuredOrganizationService service) {
		super(service, new OrganizationDetailsTransformer());
	}
	
	public boolean isExpectedPath(String path) {
		return path.matches("/api/organizations");
	}
	
	public String getMethod() {
		return "POST";
	}

	public String execute(HttpServletRequest req, HttpServletResponse res) throws ItemNotFoundException, PermissionDeniedException, IOException {
		String displayName = body(req).getString("displayName");
		return getTransformer().format(getService().createOrganization(displayName)).stringify(formatter(req));
	}
	
}
