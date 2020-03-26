package ca.magex.crm.ld.crm;

import java.util.List;

import ca.magex.crm.api.common.MailingAddress;
import ca.magex.crm.api.common.PersonName;
import ca.magex.crm.api.common.Telephone;
import ca.magex.crm.api.crm.Person;
import ca.magex.crm.api.lookup.Language;
import ca.magex.crm.api.system.Identifier;
import ca.magex.crm.api.system.Role;
import ca.magex.crm.api.system.Status;
import ca.magex.crm.ld.AbstractLinkedDataTransformer;
import ca.magex.crm.ld.common.MailingAddressTransformer;
import ca.magex.crm.ld.common.PersonNameTransformer;
import ca.magex.crm.ld.common.TelephoneTransformer;
import ca.magex.crm.ld.data.DataObject;
import ca.magex.crm.ld.lookup.LanguageTransformer;
import ca.magex.crm.ld.system.RoleTransformer;
import ca.magex.crm.ld.system.StatusTransformer;

public class PersonTransformer extends AbstractLinkedDataTransformer<Person> {

	private StatusTransformer statusTransformer;
	
	private PersonNameTransformer personNameTransformer;
	
	private MailingAddressTransformer mailingAddressTransformer;
	
	private LanguageTransformer languageTransformer;
	
	private TelephoneTransformer telephoneTransformer;
	
	private RoleTransformer roleTransformer;
	
	public PersonTransformer() {
		this.statusTransformer = new StatusTransformer();
		this.personNameTransformer = new PersonNameTransformer();
		this.mailingAddressTransformer = new MailingAddressTransformer();
		this.languageTransformer = new LanguageTransformer();
		this.telephoneTransformer = new TelephoneTransformer();
		this.roleTransformer = new RoleTransformer();
	}
	
	public String getType() {
		return "person";
	}
	
	@Override
	public DataObject format(Person person) {
		return format(person.getPersonId())
			.with("organization", format(person.getOrganizationId()))
			.with("status", statusTransformer.format(person.getStatus()))
			.with("displayName", person.getDisplayName())
			.with("legalName", personNameTransformer.format(person.getLegalName()))
			.with("address", mailingAddressTransformer.format(person.getAddress()))
			.with("email", person.getEmail())
			.with("jobTitle", person.getJobTitle())
			.with("language", languageTransformer.format(person.getLanguage()))
			.with("homePhone", telephoneTransformer.format(person.getHomePhone()))
			.with("faxNumber", person.getFaxNumber())
			.with("userName", person.getUserName())
			.with("roles", roleTransformer.format(person.getRoles()));
	}

	@Override
	public Person parse(DataObject data) {
		validateContext(data);
		validateType(data);
		Identifier personId = getTopicId(data);
		Identifier organizationId = getTopicId(data.getObject("organization"));
		Status status = statusTransformer.parse(data.get("status"));
		String displayName = data.getString("displayName");
		PersonName legalName = personNameTransformer.parse(data.get("legalName"));
		MailingAddress address = mailingAddressTransformer.parse(data.get("address"));
		String email = data.getString("email");
		String jobTitle = data.getString("jobTitle");
		Language language = languageTransformer.parse(data.get("language"));
		Telephone homePhone = telephoneTransformer.parse(data.get("homePhone"));
		Integer faxNumber = data.getInt("faxNumber");
		String userName = data.getString("userName");
		List<Role> roles = roleTransformer.parse(data.getArray("roles"));
		return new Person(personId, organizationId, status, displayName, legalName, address, email, jobTitle, language, homePhone, faxNumber, userName, roles);
	}

		
}
