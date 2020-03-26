package ca.magex.crm.ld.common;

import ca.magex.crm.api.common.PersonName;
import ca.magex.crm.api.lookup.Salutation;
import ca.magex.crm.ld.AbstractLinkedDataTransformer;
import ca.magex.crm.ld.data.DataObject;
import ca.magex.crm.ld.lookup.SalutationTransformer;

public class PersonNameTransformer extends AbstractLinkedDataTransformer<PersonName> {

	private SalutationTransformer salutationTransformer;
	
	public PersonNameTransformer() {
		this.salutationTransformer = new SalutationTransformer();
	}
	
	@Override
	public String getType() {
		return "person_name";
	}
	
	@Override
	public DataObject format(PersonName personName) {
		return base()
			.with("salutation", salutationTransformer.format(personName.getSalutation()))
			.with("firstName", personName.getFirstName())
			.with("middleName", personName.getMiddleName())
			.with("lastName", personName.getLastName());
	}

	@Override
	public PersonName parse(DataObject data) {
		validateContext(data);
		validateType(data);
		Salutation salutation = salutationTransformer.parse(data.getObject("salutation"));
		String firstName = data.getString("firstName");
		String middleName = data.getString("middleName");
		String lastName = data.getString("lastName");
		return new PersonName(salutation, firstName, middleName, lastName);
	}
			
}
