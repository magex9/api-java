package ca.magex.crm.rest.endpoint;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;

import ca.magex.crm.api.filters.Paging;
import ca.magex.crm.api.services.SecuredOrganizationService;
import ca.magex.crm.api.system.Identifier;
import ca.magex.crm.ld.LinkedDataFormatter;
import ca.magex.crm.ld.Transformer;
import ca.magex.crm.ld.data.DataObject;
import ca.magex.crm.ld.data.DataParser;

public abstract class AbstractEndpoint<T extends Object> implements Endpoint {
	
	public static final String IDENTIFIER = "[A-Za-z0-9]+";
	
	private SecuredOrganizationService organizations;
	
	private Transformer<T> transformer;
	
	private DataObject body;

	public AbstractEndpoint(SecuredOrganizationService organizations, Transformer<T> transfomer) {
		this.organizations = organizations;
		this.transformer = transfomer;
	}
	
	@Override
	public final boolean isInterestedIn(HttpServletRequest req) {
		return getMethod().equals(req.getMethod()) && isExpectedPath(req.getPathInfo());
	}
	
	public abstract boolean isExpectedPath(String path);
	
	public abstract String getMethod();
	
	@Override
	public abstract String execute(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException;
	
	public Transformer<T> getTransformer() {
		return transformer;
	}
	
	public SecuredOrganizationService getService() {
		return organizations;
	}
	
	public Identifier pathIdentifier(HttpServletRequest req, Integer index) {
		return new Identifier(req.getPathInfo().split("/")[3]);
	}
	
	public final Paging parsePaging(HttpServletRequest req) {
		Integer page = req.getParameter("page") == null ? 1 : Integer.parseInt(req.getParameter("page"));
		Integer limit = req.getParameter("limit") == null ? 10 : Integer.parseInt(req.getParameter("limit"));
		String order = req.getParameter("order") == null ? "displayName" : req.getParameter("order");
		String direction = req.getParameter("direction") == null ? "asc" : req.getParameter("direction");
		return new Paging(page, limit, Sort.by(Direction.fromString(direction), order));
	}
	
	public DataObject body(HttpServletRequest req) throws IOException {
		if (body == null) {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			IOUtils.copy(req.getInputStream(), baos);
			body = DataParser.parseObject(new String(baos.toByteArray()));
		}
		return body;
	}
	
	public final LinkedDataFormatter formatter(HttpServletRequest req) {
		return LinkedDataFormatter.basic();
	}
	
}
