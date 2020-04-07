package ca.magex.crm.ld.crm;

import ca.magex.crm.api.crm.PersonSummary;
import ca.magex.crm.api.services.CrmServices;
import ca.magex.crm.api.system.Identifier;
import ca.magex.crm.api.system.Status;
import ca.magex.crm.ld.AbstractLinkedDataTransformer;
import ca.magex.crm.ld.data.DataObject;
import ca.magex.crm.ld.system.StatusTransformer;

public class PersonSummaryTransformer extends AbstractLinkedDataTransformer<PersonSummary> {

	private StatusTransformer statusTransformer;
	
	public PersonSummaryTransformer(CrmServices crm) {
		this.statusTransformer = new StatusTransformer(crm);
	}
	
	public Class<?> getType() {
		return PersonSummary.class;
	}
	
	@Override
	public DataObject format(PersonSummary person) {
		return format(person.getPersonId())
			.with("organization", format(person.getOrganizationId()))
			.with("status", statusTransformer.format(person.getStatus()))
			.with("displayName", person.getDisplayName());
	}

	@Override
	public PersonSummary parse(DataObject data, String parentContext) {
		validateContext(data, parentContext);
		validateType(data);
		Identifier personId = getTopicId(data);
		Identifier organizationId = getTopicId(data.getObject("organization"));
		Status status = statusTransformer.parse(data.get("status"));
		String displayName = data.getString("displayName");
		return new PersonSummary(personId, organizationId, status, displayName);
	}

}
