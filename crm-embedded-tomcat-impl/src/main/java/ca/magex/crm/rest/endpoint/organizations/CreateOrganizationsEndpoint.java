package ca.magex.crm.rest.endpoint.organizations;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ca.magex.crm.api.crm.OrganizationDetails;
import ca.magex.crm.api.exceptions.ItemNotFoundException;
import ca.magex.crm.api.exceptions.PermissionDeniedException;
import ca.magex.crm.api.filters.OrganizationsFilter;
import ca.magex.crm.api.services.SecuredOrganizationService;
import ca.magex.crm.api.system.Status;
import ca.magex.crm.ld.crm.OrganizationDetailsTransformer;
import ca.magex.crm.rest.endpoint.AbstractEndpoint;

public class CreateOrganizationsEndpoint extends AbstractEndpoint<OrganizationDetails> {
	
	public CreateOrganizationsEndpoint(SecuredOrganizationService service) {
		super(service, new OrganizationDetailsTransformer());
	}
	
	public boolean isExpectedPath(String path) {
		return path.matches("/api/organizations");
	}
	
	public String getMethod() {
		return "POST";
	}

	public String execute(HttpServletRequest req, HttpServletResponse res) throws ItemNotFoundException, PermissionDeniedException, IOException {
		return getTransformer().format(getService().createOrganization(body(req).getString("displayName"))).stringify(formatter(req));
	}
	
	public OrganizationsFilter parseFilter(HttpServletRequest req) {
		String displayName = req.getParameter("displayName") == null ? null : req.getParameter("displayName");
		Status status = req.getParameter("status") == null ? null : Status.valueOf(req.getParameter("status").toUpperCase());
		return new OrganizationsFilter(displayName, status);
	}
	
}
