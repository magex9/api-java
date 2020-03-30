package ca.magex.crm.rest;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.data.domain.Sort;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import ca.magex.crm.amnesia.services.OrganizationServiceAmnesiaImpl;
import ca.magex.crm.amnesia.services.OrganizationServiceTestDataPopulator;
import ca.magex.crm.api.filters.OrganizationsFilter;
import ca.magex.crm.api.filters.Paging;
import ca.magex.crm.api.services.OrganizationPolicyBasicImpl;
import ca.magex.crm.api.services.SecuredOrganizationService;
import ca.magex.crm.ld.LinkedDataFormatter;
import ca.magex.crm.ld.crm.OrganizationSummaryTransformer;
import ca.magex.crm.ld.crm.OrganizationDetailsTransformer;
import ca.magex.crm.ld.data.DataObject;
import ca.magex.crm.ld.data.DataParser;

public class ApiServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;
	
	private SecuredOrganizationService organizations;
	
	private DataObject config;
	
	@Override
	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		this.organizations = buildService();
		this.config = buildConfig();
	}
	
	protected DataObject buildConfig() throws ServletException {
		try {
			InputStream is = getClass().getClassLoader().getResourceAsStream("crm.yaml");
			ObjectMapper yamlReader = new ObjectMapper(new YAMLFactory());
			Object obj = yamlReader.readValue(is, Object.class);
			ObjectMapper jsonWriter = new ObjectMapper();
			String json = jsonWriter.writeValueAsString(obj);
			return (DataObject) DataParser.parse(json);
		} catch (Exception e) {
			throw new ServletException(e);
		}
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
		res.setContentType("application/json");
		String path = req.getPathInfo();
		if (path.startsWith("/api")) {
			if (path.equals("/api/organizations")) {
				if (req.getMethod().equals("GET")) {
					res.getWriter().print(findOrganizations(req, res));
				} else if (req.getMethod().equals("POST")) {
					res.getWriter().print(createOrganization(req, res));
				} else {
					res.getWriter().print("{\"type\":\"Method not found: " + req.getMethod() + "\"}");
				}
			} else if (path.matches("/api/organizations/.*")) {
				res.getWriter().print("...");
			} else {
				res.getWriter().print("{\"type\":\"Path not found: " + path + "\"}");
			}
		} else if (path.startsWith("/config")) {
			res.getWriter().print(config.formatted());
		} else {
			res.getWriter().print("/toc");
		}
	}

	private String findOrganizations(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		OrganizationSummaryTransformer transformer = new OrganizationSummaryTransformer();
		Paging paging = new Paging(1, 10, Sort.by("displayName"));
		return transformer.format(organizations.findOrganizationSummaries(new OrganizationsFilter(), paging).getContent()).stringify(formatter(req));
	}

	private String createOrganization(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		DataObject params = body(req);
		return new OrganizationDetailsTransformer().format(organizations.createOrganization(params.getString("displayName"))).stringify(formatter(req));
	}
	
	private DataObject body(HttpServletRequest req) throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		IOUtils.copy(req.getInputStream(), baos);
		return DataParser.parseObject(new String(baos.toByteArray()));
	}
	
	private LinkedDataFormatter formatter(HttpServletRequest req) {
		return LinkedDataFormatter.basic();
	}
	
}
