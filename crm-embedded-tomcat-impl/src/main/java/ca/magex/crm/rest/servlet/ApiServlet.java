package ca.magex.crm.rest.servlet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ca.magex.crm.amnesia.services.OrganizationServiceAmnesiaImpl;
import ca.magex.crm.amnesia.services.OrganizationServiceTestDataPopulator;
import ca.magex.crm.api.services.OrganizationPolicyBasicImpl;
import ca.magex.crm.api.services.SecuredOrganizationService;
import ca.magex.crm.rest.endpoint.Endpoint;
import ca.magex.crm.rest.endpoint.ItemNotFoundEndpoint;
import ca.magex.crm.rest.endpoint.OpenApiConfigEndpoint;
import ca.magex.crm.rest.endpoint.organizations.CreateOrganizationsEndpoint;
import ca.magex.crm.rest.endpoint.organizations.DisableOrganizationsEndpoint;
import ca.magex.crm.rest.endpoint.organizations.EnableOrganizationsEndpoint;
import ca.magex.crm.rest.endpoint.organizations.GetOrganizationEndpoint;
import ca.magex.crm.rest.endpoint.organizations.GetOrganizationMainLocationEndpoint;
import ca.magex.crm.rest.endpoint.organizations.GetOrganizationSummaryEndpoint;
import ca.magex.crm.rest.endpoint.organizations.GetOrganizationsEndpoint;
import ca.magex.crm.rest.endpoint.organizations.UpdateOrganizationsEndpoint;

public class ApiServlet extends HttpServlet {
	
	private static final Logger logger = LoggerFactory.getLogger(ApiServlet.class);

	private static final long serialVersionUID = 1L;
	
	private SecuredOrganizationService service;
	
	private List<Endpoint> endpoints;
	
	@Override
	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		this.service = buildService();
		this.endpoints = new ArrayList<Endpoint>();
		this.endpoints.add(new OpenApiConfigEndpoint());
		this.endpoints.add(new GetOrganizationsEndpoint(service));
		this.endpoints.add(new CreateOrganizationsEndpoint(service));
		this.endpoints.add(new GetOrganizationEndpoint(service));
		this.endpoints.add(new GetOrganizationSummaryEndpoint(service));
		this.endpoints.add(new GetOrganizationMainLocationEndpoint(service));
		this.endpoints.add(new UpdateOrganizationsEndpoint(service));
		this.endpoints.add(new EnableOrganizationsEndpoint(service));
		this.endpoints.add(new DisableOrganizationsEndpoint(service));
	}

	public SecuredOrganizationService buildService() {
		OrganizationServiceAmnesiaImpl service = new OrganizationServiceAmnesiaImpl();
		OrganizationServiceTestDataPopulator.populate(service);
		//OrganizationPolicyAmnesiaImpl policy = new OrganizationPolicyAmnesiaImpl(service);
		OrganizationPolicyBasicImpl policy = new OrganizationPolicyBasicImpl();
		return new SecuredOrganizationService(service, policy);
	}
	
	@Override
	protected void service(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		logger.info(req.getMethod() + " " + req.getPathInfo() + "?" + req.getQueryString());
		res.setContentType("application/json");
		for (Endpoint endpoint : endpoints) {
			if (endpoint.isInterestedIn(req)) {
				res.getWriter().print(endpoint.execute(req, res));
				return;
			}
		}
		res.getWriter().print(new ItemNotFoundEndpoint().execute(req, res));
	}
	
}
