package ca.magex.crm.transform.json;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

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

public class PersonDetailsJsonTransformer extends AbstractJsonTransformer<PersonDetails> {

	public PersonDetailsJsonTransformer(CrmServices crm) {
		super(crm);
	}

	@Override
	public Class<PersonDetails> getType() {
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
			pairs.add(new JsonPair("personId", new IdentifierJsonTransformer(crm)
				.format(person.getPersonId(), locale)));
		}
		if (person.getOrganizationId() != null) {
			pairs.add(new JsonPair("organizationId", new IdentifierJsonTransformer(crm)
				.format(person.getOrganizationId(), locale)));
		}
		if (person.getStatus() != null) {
			pairs.add(new JsonPair("status", new StatusJsonTransformer(crm)
				.format(person.getStatus(), locale)));
		}
		formatText(pairs, "displayName", person);
		if (person.getLegalName() != null) {
			pairs.add(new JsonPair("legalName", new PersonNameJsonTransformer(crm)
				.format(person.getLegalName(), locale)));
		}
		if (person.getAddress() != null) {
			pairs.add(new JsonPair("address", new MailingAddressJsonTransformer(crm)
				.format(person.getAddress(), locale)));
		}
		if (person.getCommunication() != null) {
			pairs.add(new JsonPair("communication", new CommunicationJsonTransformer(crm)
				.format(person.getCommunication(), locale)));
		}
		if (person.getPosition() != null) {
			pairs.add(new JsonPair("position", new BusinessPositionJsonTransformer(crm)
				.format(person.getPosition(), locale)));
		}
		return new JsonObject(pairs);
	}

	@Override
	public PersonDetails parseJsonObject(JsonObject json, Locale locale) {
		Identifier personId = parseObject("personId", json, Identifier.class, IdentifierJsonTransformer.class, locale);
		Identifier organizationId = parseObject("organizationId", json, Identifier.class, IdentifierJsonTransformer.class, locale);
		Status status = parseObject("status", json, Status.class, StatusJsonTransformer.class, locale);
		String displayName = parseText("displayName", json);
		PersonName legalName = parseObject("legalName", json, PersonName.class, PersonNameJsonTransformer.class, locale);
		MailingAddress address = parseObject("address", json, MailingAddress.class, MailingAddressJsonTransformer.class, locale);
		Communication communication = parseObject("communication", json, Communication.class, CommunicationJsonTransformer.class, locale);
		BusinessPosition position = parseObject("position", json, BusinessPosition.class, BusinessPositionJsonTransformer.class, locale);
		return new PersonDetails(personId, organizationId, status, displayName, legalName, address, communication, position);
	}

}
