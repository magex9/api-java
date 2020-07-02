package ca.magex.crm.transform.json;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import ca.magex.crm.api.common.Communication;
import ca.magex.crm.api.common.MailingAddress;
import ca.magex.crm.api.common.PersonName;
import ca.magex.crm.api.crm.PersonDetails;
import ca.magex.crm.api.services.CrmServices;
import ca.magex.crm.api.system.Status;
import ca.magex.crm.api.system.Type;
import ca.magex.crm.api.system.id.BusinessRoleIdentifier;
import ca.magex.crm.api.system.id.OrganizationIdentifier;
import ca.magex.crm.api.system.id.PersonIdentifier;
import ca.magex.json.model.JsonObject;
import ca.magex.json.model.JsonPair;
import ca.magex.json.model.JsonText;

@Component
public class PersonDetailsJsonTransformer extends AbstractJsonTransformer<PersonDetails> {

	public PersonDetailsJsonTransformer(CrmServices crm) {
		super(crm);
	}

	@Override
	public Class<PersonDetails> getSourceType() {
		return PersonDetails.class;
	}
	
	@Override
	public JsonObject formatRoot(PersonDetails person) {
		return formatLocalized(person, null);
	}
	
	@Override
	public JsonObject formatLocalized(PersonDetails person, Locale locale) {
		List<JsonPair> pairs = new ArrayList<JsonPair>();
		formatType(pairs, locale);
		formatIdentifier(pairs, "personId", person, PersonIdentifier.class, locale);
		formatIdentifier(pairs, "organizationId", person, OrganizationIdentifier.class, locale);
		formatStatus(pairs, "status", person, locale);
		formatText(pairs, "displayName", person);
		formatTransformer(pairs, "legalName", person, new PersonNameJsonTransformer(crm), locale);
		formatTransformer(pairs, "address", person, new MailingAddressJsonTransformer(crm), locale);
		formatTransformer(pairs, "communication", person, new CommunicationJsonTransformer(crm), locale);
		formatOptions(pairs, "roleIds", person, Type.BUSINESS_ROLE, locale);
		return new JsonObject(pairs);
	}

	@Override
	public PersonDetails parseJsonObject(JsonObject json, Locale locale) {
		PersonIdentifier personId = parseIdentifier("personId", json, PersonIdentifier.class, locale);
		OrganizationIdentifier organizationId = parseIdentifier("organizationId", json, OrganizationIdentifier.class, locale);
		Status status = parseObject("status", json, new StatusJsonTransformer(crm), locale);
		String displayName = parseText("displayName", json);
		PersonName legalName = parseObject("legalName", json, new PersonNameJsonTransformer(crm), locale);
		MailingAddress address = parseObject("address", json, new MailingAddressJsonTransformer(crm), locale);
		Communication communication = parseObject("communication", json, new CommunicationJsonTransformer(crm), locale);
		List<BusinessRoleIdentifier> roles = json.getArray("roleIds").stream().map(e -> new BusinessRoleIdentifier(((JsonText)e).value())).collect(Collectors.toList());
		return new PersonDetails(personId, organizationId, status, displayName, legalName, address, communication, roles);
	}

}
