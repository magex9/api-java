package ca.magex.crm.api.services.basic;

import java.io.OutputStream;
import java.util.List;

import ca.magex.crm.api.authentication.CrmPasswordRepository;
import ca.magex.crm.api.common.Communication;
import ca.magex.crm.api.common.MailingAddress;
import ca.magex.crm.api.common.PersonName;
import ca.magex.crm.api.crm.LocationDetails;
import ca.magex.crm.api.crm.OrganizationDetails;
import ca.magex.crm.api.crm.PersonDetails;
import ca.magex.crm.api.filters.UsersFilter;
import ca.magex.crm.api.repositories.CrmRepositories;
import ca.magex.crm.api.roles.User;
import ca.magex.crm.api.services.CrmConfigurationService;
import ca.magex.crm.api.system.Identifier;
import ca.magex.crm.api.system.Status;

public class BasicConfigurationService implements CrmConfigurationService {

	private CrmRepositories repos;
	
	private CrmPasswordRepository passwords;
	
	public BasicConfigurationService(CrmRepositories repos, CrmPasswordRepository passwords) {
		this.repos = repos;
		this.passwords = passwords;
	}

	@Override
	public boolean isInitialized() {
		return repos.countUsers(new UsersFilter().withRole("SYS_ADMIN").withStatus(Status.ACTIVE)) > 0;
	}

	@Override
	public User initializeSystem(String organization, PersonName name, String email, String username, String password) {
		if (!isInitialized()) {
			initialize(repos);
			Identifier organizationId = repos.generateId();
			Identifier mainLocationId = repos.generateId();
			Identifier mainContactId = repos.generateId();
			Identifier systemId = repos.generateId();
			
			MailingAddress address = new MailingAddress("221b Baker Street", "London", "England", "GB", "NW1 6XE");
			Communication communication = new Communication("System Admin", "en", email, null, null);
			repos.saveOrganizationDetails(new OrganizationDetails(organizationId, Status.ACTIVE, organization, mainLocationId, mainContactId, List.of("SYS", "CRM")));
			repos.saveLocationDetails(new LocationDetails(mainLocationId, organizationId, Status.ACTIVE, "SYSTEM", "System Administrator", address));
			repos.savePersonDetails(new PersonDetails(mainContactId, organizationId, Status.ACTIVE, name.getDisplayName(), name, address, communication, null));
			repos.saveUser(new User(systemId, username, repos.findPersonSummary(mainContactId), Status.ACTIVE, List.of("SYS_ADMIN", "CRM_ADMIN")));
			passwords.generateTemporaryPassword(username);
			passwords.updatePassword(username, passwords.encodePassword(password));
		}
		return null;
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