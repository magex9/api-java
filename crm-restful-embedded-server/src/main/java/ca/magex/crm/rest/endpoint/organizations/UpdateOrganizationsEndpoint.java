package ca.magex.crm.rest.endpoint.organizations;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ca.magex.crm.api.crm.OrganizationSummary;
import ca.magex.crm.api.services.SecuredOrganizationService;
import ca.magex.crm.api.system.Identifier;
import ca.magex.crm.ld.crm.OrganizationDetailsTransformer;
import ca.magex.crm.ld.crm.OrganizationSummaryTransformer;
import ca.magex.crm.ld.data.DataObject;
import ca.magex.crm.rest.endpoint.AbstractEndpoint;

public class UpdateOrganizationsEndpoint extends AbstractEndpoint<OrganizationSummary> {
	
	public UpdateOrganizationsEndpoint(SecuredOrganizationService service) {
		super(service, new OrganizationSummaryTransformer());
	}
	
	public boolean isExpectedPath(String path) {
		return path.matches("/api/organizations/" + IDENTIFIER);
	}
	
	public String getMethod() {
		return "PATCH";
	}

	public String execute(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		Identifier organizationId = pathIdentifier(req, 3);
		DataObject params = body(req);
		if (params.contains("displayName"))
			getService().updateOrganizationName(organizationId, params.getString("displayName"));
		if (params.contains("mainLocationId"))
			getService().updateOrganizationMainLocation(organizationId, new Identifier(params.getString("mainLocationId")));
		return new OrganizationDetailsTransformer().format(getService().findOrganization(organizationId)).stringify(formatter(req));
	}
	
}
