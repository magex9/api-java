package ca.magex.crm.rest.endpoint.organizations;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ca.magex.crm.api.crm.OrganizationSummary;
import ca.magex.crm.api.services.SecuredOrganizationService;
import ca.magex.crm.ld.crm.OrganizationSummaryTransformer;
import ca.magex.crm.rest.endpoint.AbstractEndpoint;

public class DisableOrganizationsEndpoint extends AbstractEndpoint<OrganizationSummary> {
	
	public DisableOrganizationsEndpoint(SecuredOrganizationService service) {
		super(service, new OrganizationSummaryTransformer());
	}
	
	public boolean isExpectedPath(String path) {
		return path.matches("/api/organizations/" + IDENTIFIER + "/disable");
	}
	
	public String getMethod() {
		return "PUT";
	}

	public String execute(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		return getTransformer().format(getService().disableOrganization(pathIdentifier(req, 3))).stringify(formatter(req));
	}
	
}
