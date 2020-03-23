package ca.magex.crm.api.common;

import ca.magex.crm.api.lookup.Salutation;

public class PersonName {

	private Salutation salutation;

	private String firstName;

	private String middleName;

	private String lastName;
	
	public PersonName(Salutation salutation, String firstName, String middleName, String lastName) {
		super();
		this.salutation = salutation;
		this.firstName = firstName;
		this.middleName = middleName;
		this.lastName = lastName;
	}

	public Salutation getSalutation() {
		return salutation;
	}

	public String getFirstName() {
		return firstName;
	}

	public String getMiddleName() {
		return middleName;
	}

	public String getLastName() {
		return lastName;
	}
	
}
