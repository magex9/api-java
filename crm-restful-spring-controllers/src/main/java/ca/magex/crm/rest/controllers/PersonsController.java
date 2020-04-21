package ca.magex.crm.rest.controllers;

import static ca.magex.crm.rest.controllers.ContentExtractor.extractBody;
import static ca.magex.crm.rest.controllers.ContentExtractor.extractDisplayName;
import static ca.magex.crm.rest.controllers.ContentExtractor.extractOrganizationId;
import static ca.magex.crm.rest.controllers.ContentExtractor.extractPaging;
import static ca.magex.crm.rest.controllers.ContentExtractor.extractStatus;
import static ca.magex.crm.rest.controllers.ContentExtractor.getContentType;
import static ca.magex.crm.rest.controllers.ContentExtractor.getTransformer;

import java.io.IOException;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;

import ca.magex.crm.api.common.BusinessPosition;
import ca.magex.crm.api.common.Communication;
import ca.magex.crm.api.common.MailingAddress;
import ca.magex.crm.api.common.PersonName;
import ca.magex.crm.api.common.User;
import ca.magex.crm.api.crm.PersonDetails;
import ca.magex.crm.api.exceptions.BadRequestException;
import ca.magex.crm.api.filters.PersonsFilter;
import ca.magex.crm.api.services.SecuredCrmServices;
import ca.magex.crm.api.system.Identifier;
import ca.magex.crm.mapping.data.DataArray;
import ca.magex.crm.mapping.data.DataElement;
import ca.magex.crm.mapping.data.DataFormatter;
import ca.magex.crm.mapping.data.DataObject;
import ca.magex.crm.mapping.data.DataText;
import ca.magex.crm.mapping.json.JsonTransformer;

@Controller
public class PersonsController {

	@Autowired
	private SecuredCrmServices crm;
	
	public PersonsFilter extractPersonFilter(HttpServletRequest req) throws BadRequestException {
		return new PersonsFilter(extractOrganizationId(req), extractDisplayName(req), extractStatus(req));
	}
	
	public PersonName extractLegalName(HttpServletRequest req, DataObject body) {
		return getTransformer(req, crm).parsePersonName("legalName", body);
	}

	public MailingAddress extractAddress(HttpServletRequest req, DataObject body) {
		return getTransformer(req, crm).parseMailingAddress("address", body);
	}

	public Communication extractCommunication(HttpServletRequest req, DataObject body) {
		return getTransformer(req, crm).parseCommunication("communication", body);
	}

	public BusinessPosition extractPosition(HttpServletRequest req, DataObject body) {
		return getTransformer(req, crm).parseBusinessPosition("position", body);
	}
	
	public User extractUser(HttpServletRequest req, DataObject body) {
		return getTransformer(req, crm).parseUser("user", body);
	}
	
	@GetMapping("/api/persons")
	public void findPersons(HttpServletRequest req, HttpServletResponse res) throws IOException {
		JsonTransformer transformer = getTransformer(req, crm);
		DataElement data = new DataArray(crm.findPersonSummaries(extractPersonFilter(req), extractPaging(req)).getContent().stream()
				.map(e -> transformer.formatPersonSummary(e)).collect(Collectors.toList()));
		res.setStatus(200);
		res.setContentType(getContentType(req));
		res.getWriter().write(DataFormatter.formatted(data));
	}
	
	@PostMapping("/api/persons")
	public void createPerson(HttpServletRequest req, HttpServletResponse res) throws IOException {
		JsonTransformer transformer = getTransformer(req, crm);
		DataObject body = extractBody(req);
		Identifier organizationId = new Identifier(body.getString("organizationId"));
		DataElement data = transformer.formatPersonDetails(crm.createPerson(organizationId, 
				extractLegalName(req, body), extractAddress(req, body), extractCommunication(req, body), extractPosition(req, body)));
		res.setStatus(200);
		res.setContentType(getContentType(req));
		res.getWriter().write(DataFormatter.formatted(data));
	}

	@GetMapping("/api/persons/{personId}")
	public void getPerson(HttpServletRequest req, HttpServletResponse res, 
			@PathVariable("personId") String id) throws IOException {
		JsonTransformer transformer = getTransformer(req, crm);
		Identifier personId = new Identifier(id);
		DataElement data = transformer.formatPersonDetails(crm.findPersonDetails(personId));
		res.setStatus(200);
		res.setContentType(getContentType(req));
		res.getWriter().write(DataFormatter.formatted(data));
	}

	@PatchMapping("/api/persons/{personId}")
	public void updatePerson(HttpServletRequest req, HttpServletResponse res, 
			@PathVariable("personId") String id) throws IOException {
		JsonTransformer transformer = getTransformer(req, crm);
		Identifier personId = new Identifier(id);
		DataObject body = extractBody(req);
		if (body.contains("legalName"))
			crm.updatePersonName(personId, extractLegalName(req, body));
		if (body.contains("address"))
			crm.updatePersonAddress(personId, extractAddress(req, body));
		if (body.contains("communication"))
			crm.updatePersonCommunication(personId, extractCommunication(req, body));
		if (body.contains("position"))
			crm.updatePersonBusinessPosition(personId, extractPosition(req, body));
		DataElement data = transformer.formatPersonDetails(crm.findPersonDetails(personId));
		res.setStatus(200);
		res.setContentType(getContentType(req));
		res.getWriter().write(DataFormatter.formatted(data));
	}

	@GetMapping("/api/persons/{personId}/summary")
	public void getPersonSummary(HttpServletRequest req, HttpServletResponse res, 
			@PathVariable("personId") String id) throws IOException {
		JsonTransformer transformer = getTransformer(req, crm);
		Identifier personId = new Identifier(id);
		DataElement data = transformer.formatPersonSummary(crm.findPersonDetails(personId));
		res.setStatus(200);
		res.setContentType(getContentType(req));
		res.getWriter().write(DataFormatter.formatted(data));
	}

	@GetMapping("/api/persons/{personId}/legalName")
	public void getPersonLegalname(HttpServletRequest req, HttpServletResponse res, 
			@PathVariable("personId") String id) throws IOException {
		JsonTransformer transformer = getTransformer(req, crm);
		Identifier personId = new Identifier(id);
		DataElement data = transformer.formatPersonDetails(crm.findPersonDetails(personId)).getObject("legalName");
		res.setStatus(200);
		res.setContentType(getContentType(req));
		res.getWriter().write(DataFormatter.formatted(data));
	}

	@GetMapping("/api/persons/{personId}/address")
	public void getPersonAddress(HttpServletRequest req, HttpServletResponse res, 
			@PathVariable("personId") String id) throws IOException {
		JsonTransformer transformer = getTransformer(req, crm);
		Identifier personId = new Identifier(id);
		DataElement data = transformer.formatPersonDetails(crm.findPersonDetails(personId)).getObject("address");
		res.setStatus(200);
		res.setContentType(getContentType(req));
		res.getWriter().write(DataFormatter.formatted(data));
	}

	@GetMapping("/api/persons/{personId}/communication")
	public void getPersonCommunication(HttpServletRequest req, HttpServletResponse res, 
			@PathVariable("personId") String id) throws IOException {
		JsonTransformer transformer = getTransformer(req, crm);
		Identifier personId = new Identifier(id);
		DataElement data = transformer.formatPersonDetails(crm.findPersonDetails(personId)).getObject("communication");
		res.setStatus(200);
		res.setContentType(getContentType(req));
		res.getWriter().write(DataFormatter.formatted(data));
	}

	@GetMapping("/api/persons/{personId}/position")
	public void getPersonPosition(HttpServletRequest req, HttpServletResponse res, 
			@PathVariable("personId") String id) throws IOException {
		JsonTransformer transformer = getTransformer(req, crm);
		Identifier personId = new Identifier(id);
		DataElement data = transformer.formatPersonDetails(crm.findPersonDetails(personId)).getObject("position");
		res.setStatus(200);
		res.setContentType(getContentType(req));
		res.getWriter().write(DataFormatter.formatted(data));
	}

	@GetMapping("/api/persons/{personId}/user")
	public void getPersonUser(HttpServletRequest req, HttpServletResponse res, 
			@PathVariable("personId") String id) throws IOException {
		JsonTransformer transformer = getTransformer(req, crm);
		Identifier personId = new Identifier(id);
		DataElement data = transformer.formatPersonDetails(crm.findPersonDetails(personId)).getObject("user");
		res.setStatus(200);
		res.setContentType(getContentType(req));
		res.getWriter().write(DataFormatter.formatted(data));
	}

	@PutMapping("/api/persons/{personId}/enable")
	public void enablePerson(HttpServletRequest req, HttpServletResponse res, 
			@PathVariable("personId") String id) throws IOException {
		JsonTransformer transformer = getTransformer(req, crm);
		Identifier personId = new Identifier(id);
		DataElement data = transformer.formatPersonSummary(crm.enablePerson(personId));
		res.setStatus(200);
		res.setContentType(getContentType(req));
		res.getWriter().write(DataFormatter.formatted(data));
	}

	@PutMapping("/api/persons/{personId}/disable")
	public void disablePerson(HttpServletRequest req, HttpServletResponse res, 
			@PathVariable("personId") String id) throws IOException {
		JsonTransformer transformer = getTransformer(req, crm);
		Identifier personId = new Identifier(id);
		DataElement data = transformer.formatPersonSummary(crm.disablePerson(personId));
		res.setStatus(200);
		res.setContentType(getContentType(req));
		res.getWriter().write(DataFormatter.formatted(data));
	}

	@GetMapping("/api/persons/{personId}/roles")
	public void getUserRoles(HttpServletRequest req, HttpServletResponse res, 
			@PathVariable("personId") String personId) throws IOException {
		JsonTransformer transformer = getTransformer(req, crm);
		DataElement data = transformer.formatPersonDetails(crm.findPersonDetails(new Identifier(personId))).getObject("user").getArray("roles");
		res.setStatus(200);
		res.setContentType(getContentType(req));
		res.getWriter().write(DataFormatter.formatted(data));
	}
	
	@PostMapping("/api/persons/{personId}/roles")
	public void setUserRoles(HttpServletRequest req, HttpServletResponse res, 
			@PathVariable("personId") String personId) throws IOException {
		JsonTransformer transformer = getTransformer(req, crm);
		PersonDetails details = crm.setUserRoles(new Identifier(personId), extractBody(req).getArray("roles").stream().map(r -> crm.findRoleByCode(((DataText)r).value()).getCode()).collect(Collectors.toList()));
		DataElement data = transformer.formatPersonDetails(details).getObject("user").getArray("roles");
		res.setStatus(200);
		res.setContentType(getContentType(req));
		res.getWriter().write(DataFormatter.formatted(data));
	}
	
	@PostMapping("/api/persons/{personId}/roles/{roleId}")
	public void addUserRole(HttpServletRequest req, HttpServletResponse res, 
			@PathVariable("personId") String personId, @PathVariable("roleId") String roleId) throws IOException {
		JsonTransformer transformer = getTransformer(req, crm);
		PersonDetails details = crm.addUserRole(new Identifier(personId), crm.findRoleByCode(roleId).getCode());
		DataElement data = transformer.formatPersonDetails(details).getObject("user").getArray("roles");
		res.setStatus(200);
		res.setContentType(getContentType(req));
		res.getWriter().write(DataFormatter.formatted(data));
	}
	
	@DeleteMapping("/api/persons/{personId}/roles/{roleId}")
	public void removeUserRole(HttpServletRequest req, HttpServletResponse res, 
			@PathVariable("personId") String personId, @PathVariable("roleId") String roleId) throws IOException {
		JsonTransformer transformer = getTransformer(req, crm);
		PersonDetails details = crm.removeUserRole(new Identifier(personId), crm.findRoleByCode(roleId).getCode());
		DataElement data = transformer.formatPersonDetails(details).getObject("user").getArray("roles");
		res.setStatus(200);
		res.setContentType(getContentType(req));
		res.getWriter().write(DataFormatter.formatted(data));
	}

}