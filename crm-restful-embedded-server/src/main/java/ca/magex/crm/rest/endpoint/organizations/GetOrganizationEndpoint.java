package ca.magex.crm.rest.endpoint.organizations;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ca.magex.crm.api.crm.OrganizationDetails;
import ca.magex.crm.api.services.SecuredOrganizationService;
import ca.magex.crm.ld.crm.OrganizationDetailsTransformer;
import ca.magex.crm.rest.endpoint.AbstractEndpoint;

public class GetOrganizationEndpoint extends AbstractEndpoint<OrganizationDetails> {
	
	public GetOrganizationEndpoint(SecuredOrganizationService service) {
		super(service, new OrganizationDetailsTransformer());
	}
	
	public boolean isExpectedPath(String path) {
		return path.matches("/api/organizations/" + IDENTIFIER);
	}
	
	public String getMethod() {
		return "GET";
	}

	public String execute(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		return getTransformer().format(getService().findOrganization(pathIdentifier(req, 3))).stringify(formatter(req));
	}
	
}
