package ca.magex.crm.api.services.basic;

import java.io.OutputStream;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import ca.magex.crm.api.authentication.CrmPasswordService;
import ca.magex.crm.api.common.Communication;
import ca.magex.crm.api.common.MailingAddress;
import ca.magex.crm.api.common.PersonName;
import ca.magex.crm.api.crm.LocationDetails;
import ca.magex.crm.api.crm.OrganizationDetails;
import ca.magex.crm.api.crm.PersonDetails;
import ca.magex.crm.api.crm.User;
import ca.magex.crm.api.dictionary.CrmDictionary;
import ca.magex.crm.api.filters.OptionsFilter;
import ca.magex.crm.api.filters.UsersFilter;
import ca.magex.crm.api.repositories.CrmRepositories;
import ca.magex.crm.api.services.CrmConfigurationService;
import ca.magex.crm.api.system.Identifier;
import ca.magex.crm.api.system.Status;
import ca.magex.crm.api.system.Type;

public class BasicConfigurationService implements CrmConfigurationService {

	private CrmRepositories repos;
	
	private CrmPasswordService passwords;
	
	private CrmDictionary dictionary;
	
	public BasicConfigurationService(CrmRepositories repos, CrmPasswordService passwords, CrmDictionary dictionary) {
		this.repos = repos;
		this.passwords = passwords;
		this.dictionary = dictionary;
	}

	@Override
	public boolean isInitialized() {
		return repos.countUsers(new UsersFilter().withRoleId(roles("SYS_ADMIN").get(0)).withStatus(Status.ACTIVE)) > 0;
	}

	@Override
	public User initializeSystem(String organization, PersonName name, String email, String username, String password) {
		if (!isInitialized()) {
			initialize(repos);
			Identifier organizationId = repos.generateOrganizationId();
			Identifier mainLocationId = repos.generateLocationId();
			Identifier mainContactId = repos.generatePersonId();
			Identifier systemId = repos.generateUserId();
			
			MailingAddress address = new MailingAddress("221b Baker Street", "London", "England", "GB", "NW1 6XE");
			Communication communication = new Communication("System Admin", "en", email, null, null);
			repos.saveOrganizationDetails(new OrganizationDetails(organizationId, Status.ACTIVE, organization, mainLocationId, mainContactId, groups("SYS", "CRM")));
			repos.saveLocationDetails(new LocationDetails(mainLocationId, organizationId, Status.ACTIVE, "SYSTEM", "System Administrator", address));
			repos.savePersonDetails(new PersonDetails(mainContactId, organizationId, Status.ACTIVE, name.getDisplayName(), name, address, communication, null));
			repos.saveUser(new User(systemId, username, repos.findPersonSummary(mainContactId), Status.ACTIVE, roles("SYS_ADMIN", "SYS_ACTUATOR", "SYS_ACCESS", "CRM_ADMIN")));
			passwords.generateTemporaryPassword(username);
			passwords.updatePassword(username, passwords.encodePassword(password));
		}
		return repos.findUsers(new UsersFilter().withRoleId(roles("SYS_ADMIN").get(0)).withStatus(Status.ACTIVE), UsersFilter.getDefaultPaging()).getContent().get(0);
	}
	
	private List<Identifier> groups(String... codes) {
		return Arrays.asList(codes).stream()
				.map(c -> repos.findOptions(new OptionsFilter().withOptionCode(c).withType(Type.AUTHENTICATION_ROLE),
						OptionsFilter.getDefaultPaging()).getSingleItem().getOptionId())
				.collect(Collectors.toList());
	}

	private List<Identifier> roles(String... codes) {
		return Arrays.asList(codes).stream()
				.map(c -> repos.findOptions(new OptionsFilter().withOptionCode(c).withType(Type.AUTHENTICATION_ROLE),
						OptionsFilter.getDefaultPaging()).getSingleItem().getOptionId())
				.collect(Collectors.toList());
	}
	
	@Override
	public CrmDictionary getDictionary() {
		return dictionary;
	}
		
	@Override
	public boolean reset() {
		repos.reset();
		return true;
	}
	
	@Override
	public void dump(OutputStream os) {
		repos.dump(os);
	}

}