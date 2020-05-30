package ca.magex.crm.transform.json;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.springframework.stereotype.Component;

import ca.magex.crm.api.common.BusinessPosition;
import ca.magex.crm.api.common.Communication;
import ca.magex.crm.api.common.MailingAddress;
import ca.magex.crm.api.common.PersonName;
import ca.magex.crm.api.crm.PersonDetails;
import ca.magex.crm.api.services.CrmServices;
import ca.magex.crm.api.system.Identifier;
import ca.magex.crm.api.system.Status;
import ca.magex.json.model.JsonObject;
import ca.magex.json.model.JsonPair;

@Component
public class PersonDetailsJsonTransformer extends AbstractJsonTransformer<PersonDetails> {

	private IdentifierJsonTransformer identifierJsonTransformer;
	
	private StatusJsonTransformer statusJsonTransformer;
	
	private PersonNameJsonTransformer personNameJsonTransformer;
	
	private MailingAddressJsonTransformer mailingAddressJsonTransformer;
	
	private CommunicationJsonTransformer communicationJsonTransformer;
	
	private BusinessPositionJsonTransformer businessPositionJsonTransformer;

	public PersonDetailsJsonTransformer(CrmServices crm, IdentifierJsonTransformer identifierJsonTransformer,
			StatusJsonTransformer statusJsonTransformer, PersonNameJsonTransformer personNameJsonTransformer,
			MailingAddressJsonTransformer mailingAddressJsonTransformer,
			CommunicationJsonTransformer communicationJsonTransformer,
			BusinessPositionJsonTransformer businessPositionJsonTransformer) {
		super(crm);
		this.identifierJsonTransformer = identifierJsonTransformer;
		this.statusJsonTransformer = statusJsonTransformer;
		this.personNameJsonTransformer = personNameJsonTransformer;
		this.mailingAddressJsonTransformer = mailingAddressJsonTransformer;
		this.communicationJsonTransformer = communicationJsonTransformer;
		this.businessPositionJsonTransformer = businessPositionJsonTransformer;
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
		formatType(pairs);
		if (person.getPersonId() != null) {
			pairs.add(new JsonPair("personId", identifierJsonTransformer
				.format(person.getPersonId(), locale)));
		}
		if (person.getOrganizationId() != null) {
			pairs.add(new JsonPair("organizationId", identifierJsonTransformer
				.format(person.getOrganizationId(), locale)));
		}
		if (person.getStatus() != null) {
			pairs.add(new JsonPair("status", statusJsonTransformer
				.format(person.getStatus(), locale)));
		}
		formatText(pairs, "displayName", person);
		if (person.getLegalName() != null) {
			pairs.add(new JsonPair("legalName", personNameJsonTransformer
				.format(person.getLegalName(), locale)));
		}
		if (person.getAddress() != null) {
			pairs.add(new JsonPair("address", mailingAddressJsonTransformer
				.format(person.getAddress(), locale)));
		}
		if (person.getCommunication() != null) {
			pairs.add(new JsonPair("communication", communicationJsonTransformer
				.format(person.getCommunication(), locale)));
		}
		if (person.getPosition() != null) {
			pairs.add(new JsonPair("position", businessPositionJsonTransformer
				.format(person.getPosition(), locale)));
		}
		return new JsonObject(pairs);
	}

	@Override
	public PersonDetails parseJsonObject(JsonObject json, Locale locale) {
		Identifier personId = parseObject("personId", json, identifierJsonTransformer, locale);
		Identifier organizationId = parseObject("organizationId", json, identifierJsonTransformer, locale);
		Status status = parseObject("status", json, statusJsonTransformer, locale);
		String displayName = parseText("displayName", json);
		PersonName legalName = parseObject("legalName", json, personNameJsonTransformer, locale);
		MailingAddress address = parseObject("address", json, mailingAddressJsonTransformer, locale);
		Communication communication = parseObject("communication", json, communicationJsonTransformer, locale);
		BusinessPosition position = parseObject("position", json, businessPositionJsonTransformer, locale);
		return new PersonDetails(personId, organizationId, status, displayName, legalName, address, communication, position);
	}

}
