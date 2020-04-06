package ca.magex.crm.rest.controllers;

import static ca.magex.crm.rest.controllers.ContentExtractor.extractBody;
import static ca.magex.crm.rest.controllers.ContentExtractor.extractDisplayName;
import static ca.magex.crm.rest.controllers.ContentExtractor.extractPaging;
import static ca.magex.crm.rest.controllers.ContentExtractor.extractStatus;
import static ca.magex.crm.rest.controllers.ContentExtractor.formatter;
import static ca.magex.crm.rest.controllers.ContentExtractor.getContentType;

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
import ca.magex.crm.api.exceptions.BadRequestException;
import ca.magex.crm.api.filters.PersonsFilter;
import ca.magex.crm.api.services.SecuredCrmServices;
import ca.magex.crm.api.system.Identifier;
import ca.magex.crm.ld.common.BusinessPositionTransformer;
import ca.magex.crm.ld.common.CommunicationTransformer;
import ca.magex.crm.ld.common.MailingAddressTransformer;
import ca.magex.crm.ld.common.PersonNameTransformer;
import ca.magex.crm.ld.common.UserTransformer;
import ca.magex.crm.ld.crm.PersonDetailsTransformer;
import ca.magex.crm.ld.crm.PersonSummaryTransformer;
import ca.magex.crm.ld.data.DataArray;
import ca.magex.crm.ld.data.DataElement;
import ca.magex.crm.ld.data.DataObject;
import ca.magex.crm.ld.data.DataText;
import ca.magex.crm.ld.system.RoleTransformer;
import ca.magex.crm.ld.system.StatusTransformer;

@Controller
public class PersonsController {

	@Autowired
	private SecuredCrmServices crm;
	
	public PersonsFilter extractPersonFilter(HttpServletRequest req) throws BadRequestException {
		return new PersonsFilter(extractDisplayName(req), extractStatus(req));
	}
	
	public PersonName extractLegalName(DataObject body) {
		return new PersonNameTransformer(crm).parse(body.getString("legalName"));
	}

	public MailingAddress extractAddress(DataObject body) {
		return new MailingAddressTransformer(crm).parse(body.getObject("address"));
	}

	public Communication extractCommunication(DataObject body) {
		return new CommunicationTransformer(crm).parse(body.getObject("communication"));
	}

	public BusinessPosition extractPosition(DataObject body) {
		return new BusinessPositionTransformer(crm).parse(body.getObject("position"));
	}
	
	public User extractUser(DataObject body) {
		return new UserTransformer(crm).parse(body.getObject("user"));
	}
	
	@GetMapping("/api/persons")
	public void findPersons(HttpServletRequest req, HttpServletResponse res) throws IOException {
		PersonSummaryTransformer transformer = new PersonSummaryTransformer(crm);
		DataElement data = new DataArray(crm.findPersonSummaries(extractPersonFilter(req), extractPaging(req)).getContent().stream()
				.map(e -> transformer.format(e)).collect(Collectors.toList()));
		res.setStatus(200);
		res.setContentType(getContentType(req));
		res.getWriter().write(data.stringify(formatter(req)));
	}
	
	@PostMapping("/api/persons")
	public void createPerson(HttpServletRequest req, HttpServletResponse res) throws IOException {
		PersonDetailsTransformer transformer = new PersonDetailsTransformer(crm);
		DataObject body = extractBody(req);
		Identifier organizationId = new Identifier(body.getString("organizationId"));
		DataElement data = transformer.format(crm.createPerson(organizationId, 
				extractLegalName(body), extractAddress(body), extractCommunication(body), extractPosition(body)));
		res.setStatus(200);
		res.getWriter().write(data.stringify(formatter(req)));
	}

	@GetMapping("/api/persons/{personId}")
	public void getPerson(HttpServletRequest req, HttpServletResponse res, 
			@PathVariable("personId") String id) throws IOException {
		PersonDetailsTransformer transformer = new PersonDetailsTransformer(crm);
		Identifier personId = new Identifier(id);
		DataElement data = transformer.format(crm.findPersonDetails(personId));
		res.setStatus(200);
		res.getWriter().write(data.stringify(formatter(req)));
	}

	@PatchMapping("/api/persons/{personId}")
	public void updatePerson(HttpServletRequest req, HttpServletResponse res, 
			@PathVariable("personId") String id) throws IOException {
		PersonDetailsTransformer transformer = new PersonDetailsTransformer(crm);
		Identifier personId = new Identifier(id);
		DataObject body = extractBody(req);
		if (body.contains("legalName"))
			crm.updatePersonName(personId, extractLegalName(body));
		if (body.contains("address"))
			crm.updatePersonAddress(personId, extractAddress(body));
		if (body.contains("communication"))
			crm.updatePersonCommunication(personId, extractCommunication(body));
		if (body.contains("position"))
			crm.updatePersonBusinessPosition(personId, extractPosition(body));
		DataElement data = transformer.format(crm.findPersonDetails(personId));
		res.setStatus(200);
		res.getWriter().write(data.stringify(formatter(req)));
	}

	@GetMapping("/api/persons/{personId}/summary")
	public void getPersonSummary(HttpServletRequest req, HttpServletResponse res, 
			@PathVariable("personId") String id) throws IOException {
		PersonSummaryTransformer transformer = new PersonSummaryTransformer(crm);
		Identifier personId = new Identifier(id);
		DataElement data = transformer.format(crm.findPersonDetails(personId));
		res.setStatus(200);
		res.getWriter().write(data.stringify(formatter(req)));
	}

	@GetMapping("/api/persons/{personId}/legalName")
	public void getPersonLegalname(HttpServletRequest req, HttpServletResponse res, 
			@PathVariable("personId") String id) throws IOException {
		PersonNameTransformer transformer = new PersonNameTransformer(crm);
		Identifier personId = new Identifier(id);
		DataElement data = transformer.format(crm.findPersonDetails(personId).getLegalName());
		res.setStatus(200);
		res.getWriter().write(data.stringify(formatter(req)));
	}

	@GetMapping("/api/persons/{personId}/address")
	public void getPersonAddress(HttpServletRequest req, HttpServletResponse res, 
			@PathVariable("personId") String id) throws IOException {
		MailingAddressTransformer transformer = new MailingAddressTransformer(crm);
		Identifier personId = new Identifier(id);
		DataElement data = transformer.format(crm.findPersonDetails(personId).getAddress());
		res.setStatus(200);
		res.getWriter().write(data.stringify(formatter(req)));
	}

	@GetMapping("/api/persons/{personId}/communication")
	public void getPersonCommunication(HttpServletRequest req, HttpServletResponse res, 
			@PathVariable("personId") String id) throws IOException {
		CommunicationTransformer transformer = new CommunicationTransformer(crm);
		Identifier personId = new Identifier(id);
		DataElement data = transformer.format(crm.findPersonDetails(personId).getCommunication());
		res.setStatus(200);
		res.getWriter().write(data.stringify(formatter(req)));
	}

	@GetMapping("/api/persons/{personId}/position")
	public void getPersonPosition(HttpServletRequest req, HttpServletResponse res, 
			@PathVariable("personId") String id) throws IOException {
		BusinessPositionTransformer transformer = new BusinessPositionTransformer(crm);
		Identifier personId = new Identifier(id);
		DataElement data = transformer.format(crm.findPersonDetails(personId).getPosition());
		res.setStatus(200);
		res.getWriter().write(data.stringify(formatter(req)));
	}

	@GetMapping("/api/persons/{personId}/user")
	public void getPersonUser(HttpServletRequest req, HttpServletResponse res, 
			@PathVariable("personId") String id) throws IOException {
		UserTransformer transformer = new UserTransformer(crm);
		Identifier personId = new Identifier(id);
		DataElement data = transformer.format(crm.findPersonDetails(personId).getUser());
		res.setStatus(200);
		res.getWriter().write(data.stringify(formatter(req)));
	}

	@PutMapping("/api/persons/{personId}/enable")
	public void enablePerson(HttpServletRequest req, HttpServletResponse res, 
			@PathVariable("personId") String id) throws IOException {
		StatusTransformer transformer = new StatusTransformer(crm);
		Identifier personId = new Identifier(id);
		DataElement data = transformer.format(crm.enablePerson(personId).getStatus());
		res.setStatus(200);
		res.getWriter().write(data.stringify(formatter(req)));
	}

	@PutMapping("/api/persons/{personId}/disable")
	public void disablePerson(HttpServletRequest req, HttpServletResponse res, 
			@PathVariable("personId") String id) throws IOException {
		StatusTransformer transformer = new StatusTransformer(crm);
		Identifier personId = new Identifier(id);
		DataElement data = transformer.format(crm.enablePerson(personId).getStatus());
		res.setStatus(200);
		res.getWriter().write(data.stringify(formatter(req)));
	}

	@GetMapping("/api/persons/{personId}/roles")
	public void getUserRoles(HttpServletRequest req, HttpServletResponse res, 
			@PathVariable("personId") String personId) throws IOException {
		RoleTransformer transformer = new RoleTransformer(crm);
		DataElement data = new DataArray(crm.findPersonDetails(new Identifier(personId)).getUser().getRoles().stream()
			.map(r -> transformer.format(r)).collect(Collectors.toList()));
		res.setStatus(200);
		res.getWriter().write(data.stringify(formatter(req)));
	}
	
	@PostMapping("/api/persons/{personId}/roles")
	public void setUserRoles(HttpServletRequest req, HttpServletResponse res, 
			@PathVariable("personId") String personId) throws IOException {
		RoleTransformer transformer = new RoleTransformer(crm);
		DataElement data = new DataArray(crm.setUserRoles(
				new Identifier(personId), 
				extractBody(req).getArray("roles").stream().map(r -> crm.findRoleByCode(((DataText)r).value())).collect(Collectors.toList())
			).getUser().getRoles().stream()
			.map(r -> transformer.format(r)).collect(Collectors.toList()));
		;
		res.setStatus(200);
		res.getWriter().write(data.stringify(formatter(req)));
	}
	
	@PostMapping("/api/persons/{personId}/roles/{roleId}")
	public void addUserRole(HttpServletRequest req, HttpServletResponse res, 
			@PathVariable("personId") String personId, @PathVariable("roleId") String roleId) throws IOException {
		RoleTransformer transformer = new RoleTransformer(crm);
		DataElement data = new DataArray(crm.addUserRole(new Identifier(personId), crm.findRoleByCode(roleId)).getUser().getRoles().stream()
			.map(r -> transformer.format(r)).collect(Collectors.toList()));
		res.setStatus(200);
		res.getWriter().write(data.stringify(formatter(req)));
	}
	
	@DeleteMapping("/api/persons/{personId}/roles/{roleId}")
	public void removeUserRole(HttpServletRequest req, HttpServletResponse res, 
			@PathVariable("personId") String personId, @PathVariable("roleId") String roleId) throws IOException {
		RoleTransformer transformer = new RoleTransformer(crm);
		DataElement data = new DataArray(crm.removeUserRole(new Identifier(personId), crm.findRoleByCode(roleId)).getUser().getRoles().stream()
			.map(r -> transformer.format(r)).collect(Collectors.toList()));
		res.setStatus(200);
		res.getWriter().write(data.stringify(formatter(req)));
	}

}