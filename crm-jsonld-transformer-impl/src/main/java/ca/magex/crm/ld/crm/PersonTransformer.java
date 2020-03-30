package ca.magex.crm.ld.crm;

import ca.magex.crm.api.common.BusinessPosition;
import ca.magex.crm.api.common.Communication;
import ca.magex.crm.api.common.MailingAddress;
import ca.magex.crm.api.common.PersonName;
import ca.magex.crm.api.common.User;
import ca.magex.crm.api.crm.PersonDetails;
import ca.magex.crm.api.system.Identifier;
import ca.magex.crm.api.system.Status;
import ca.magex.crm.ld.AbstractLinkedDataTransformer;
import ca.magex.crm.ld.common.BusinessPositionTransformer;
import ca.magex.crm.ld.common.CommunicationTransformer;
import ca.magex.crm.ld.common.MailingAddressTransformer;
import ca.magex.crm.ld.common.PersonNameTransformer;
import ca.magex.crm.ld.common.UserTransformer;
import ca.magex.crm.ld.data.DataObject;
import ca.magex.crm.ld.system.StatusTransformer;

public class PersonTransformer extends AbstractLinkedDataTransformer<PersonDetails> {

	private StatusTransformer statusTransformer;
	
	private PersonNameTransformer personNameTransformer;
	
	private MailingAddressTransformer mailingAddressTransformer;
	
	private CommunicationTransformer communicationTransformer;
	
	private BusinessPositionTransformer businessPositionTransformer;
	
	private UserTransformer userTransformer;
	
	public PersonTransformer() {
		this.statusTransformer = new StatusTransformer();
		this.personNameTransformer = new PersonNameTransformer();
		this.mailingAddressTransformer = new MailingAddressTransformer();
		this.communicationTransformer = new CommunicationTransformer();
		this.businessPositionTransformer = new BusinessPositionTransformer();
		this.userTransformer = new UserTransformer();
	}
	
	public Class<?> getType() {
		return PersonDetails.class;
	}
	
	@Override
	public DataObject format(PersonDetails person) {
		return format(person.getPersonId())
			.with("organization", format(person.getOrganizationId()))
			.with("status", statusTransformer.format(person.getStatus()))
			.with("displayName", person.getDisplayName())
			.with("legalName", personNameTransformer.format(person.getLegalName()))
			.with("address", mailingAddressTransformer.format(person.getAddress()))
			.with("communication", communicationTransformer.format(person.getCommunication()))
			.with("unit", businessPositionTransformer.format(person.getPosition()))
			.with("user", userTransformer.format(person.getUser()));
	}

	@Override
	public PersonDetails parse(DataObject data, String parentContext) {
		validateContext(data, parentContext);
		validateType(data);
		Identifier personId = getTopicId(data);
		Identifier organizationId = getTopicId(data.getObject("organization"));
		Status status = statusTransformer.parse(data.get("status"));
		String displayName = data.getString("displayName");
		PersonName legalName = personNameTransformer.parse(data.get("legalName"));
		MailingAddress address = mailingAddressTransformer.parse(data.get("address"));
		Communication communication = communicationTransformer.parse(data.get("communication"));
		BusinessPosition unit = businessPositionTransformer.parse(data.get("unit"));
		User user = userTransformer.parse(data.get("user"));
		return new PersonDetails(personId, organizationId, status, displayName, legalName, address, communication, unit, user);
	}

		
}
