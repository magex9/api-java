package ca.magex.crm.rest.endpoint.organizations;

import java.io.IOException;
import java.util.stream.Collectors;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ca.magex.crm.api.crm.OrganizationSummary;
import ca.magex.crm.api.filters.OrganizationsFilter;
import ca.magex.crm.api.services.SecuredOrganizationService;
import ca.magex.crm.api.system.Status;
import ca.magex.crm.ld.crm.OrganizationSummaryTransformer;
import ca.magex.crm.ld.data.DataArray;
import ca.magex.crm.rest.endpoint.AbstractEndpoint;

public class GetOrganizationsEndpoint extends AbstractEndpoint<OrganizationSummary> {
	
	public GetOrganizationsEndpoint(SecuredOrganizationService service) {
		super(service, new OrganizationSummaryTransformer());
	}
	
	public boolean isExpectedPath(String path) {
		return path.matches("/api/organizations");
	}
	
	public String getMethod() {
		return "GET";
	}

	public String execute(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		return new DataArray(getService().findOrganizationSummaries(parseFilter(req), parsePaging(req)).getContent().stream()
			.map(e -> getTransformer().format(e)).collect(Collectors.toList())).stringify(formatter(req));
	}
	
	public OrganizationsFilter parseFilter(HttpServletRequest req) {
		String displayName = req.getParameter("displayName") == null ? null : req.getParameter("displayName");
		Status status = req.getParameter("status") == null ? null : Status.valueOf(req.getParameter("status").toUpperCase());
		return new OrganizationsFilter(displayName, status);
	}
	
}
