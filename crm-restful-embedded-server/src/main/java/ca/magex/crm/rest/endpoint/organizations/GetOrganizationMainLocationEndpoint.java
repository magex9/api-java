package ca.magex.crm.rest.endpoint.organizations;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ca.magex.crm.api.crm.LocationDetails;
import ca.magex.crm.api.services.SecuredOrganizationService;
import ca.magex.crm.ld.crm.LocationDetailsTransformer;
import ca.magex.crm.rest.endpoint.AbstractEndpoint;

public class GetOrganizationMainLocationEndpoint extends AbstractEndpoint<LocationDetails> {
	
	public GetOrganizationMainLocationEndpoint(SecuredOrganizationService service) {
		super(service, new LocationDetailsTransformer());
	}
	
	public boolean isExpectedPath(String path) {
		return path.matches("/api/organizations/" + IDENTIFIER + "/mainLocation");
	}
	
	public String getMethod() {
		return "GET";
	}

	public String execute(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		return getTransformer().format(getService().findLocation(getService().findOrganization(pathIdentifier(req, 3)).getMainLocationId())).stringify(formatter(req));
	}
	
}
