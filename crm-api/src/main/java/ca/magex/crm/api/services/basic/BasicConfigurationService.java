package ca.magex.crm.api.services.basic;

import java.io.OutputStream;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import ca.magex.crm.api.authentication.CrmPasswordService;
import ca.magex.crm.api.common.Communication;
import ca.magex.crm.api.common.MailingAddress;
import ca.magex.crm.api.common.PersonName;
import ca.magex.crm.api.crm.LocationDetails;
import ca.magex.crm.api.crm.OrganizationDetails;
import ca.magex.crm.api.crm.PersonDetails;
import ca.magex.crm.api.crm.User;
import ca.magex.crm.api.dictionary.CrmDictionary;
import ca.magex.crm.api.filters.UsersFilter;
import ca.magex.crm.api.repositories.CrmRepositories;
import ca.magex.crm.api.services.CrmConfigurationService;
import ca.magex.crm.api.system.Choice;
import ca.magex.crm.api.system.Status;
import ca.magex.crm.api.system.id.AuthenticationGroupIdentifier;
import ca.magex.crm.api.system.id.AuthenticationRoleIdentifier;
import ca.magex.crm.api.system.id.CountryIdentifier;
import ca.magex.crm.api.system.id.LanguageIdentifier;
import ca.magex.crm.api.system.id.LocationIdentifier;
import ca.magex.crm.api.system.id.OrganizationIdentifier;
import ca.magex.crm.api.system.id.PersonIdentifier;
import ca.magex.crm.api.system.id.UserIdentifier;

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
		return repos.isInitialized();
	}

	@Override
	public User initializeSystem(String organization, PersonName name, String email, String username, String password) {
		if (!isInitialized()) {
			initialize(repos);
			OrganizationIdentifier organizationId = repos.generateOrganizationId();
			LocationIdentifier mainLocationId = repos.generateLocationId();
			PersonIdentifier mainContactId = repos.generatePersonId();
			UserIdentifier systemId = repos.generateUserId();
			
			MailingAddress address = new MailingAddress("221b Baker Street", "London", new Choice<>("England"), new Choice<>(new CountryIdentifier("GB")), "NW1 6XE");
			Communication communication = new Communication("System Admin", new Choice<LanguageIdentifier>(new LanguageIdentifier("EN")), email, null, null);
			repos.saveOrganizationDetails(new OrganizationDetails(organizationId, Status.ACTIVE, organization, mainLocationId, mainContactId, groups("SYS", "CRM")));
			repos.saveLocationDetails(new LocationDetails(mainLocationId, organizationId, Status.ACTIVE, "SYSTEM", "System Administrator", address));
			repos.savePersonDetails(new PersonDetails(mainContactId, organizationId, Status.ACTIVE, name.getDisplayName(), name, address, communication, null));
			repos.saveUser(new User(systemId, mainContactId, username, Status.ACTIVE, roles("SYS/ADMIN", "SYS/ACTUATOR", "SYS/ACCESS", "CRM/ADMIN")));
			passwords.generateTemporaryPassword(username);
			passwords.updatePassword(username, passwords.encodePassword(password));
			repos.setInitialized();
		}
		return repos.findUsers(new UsersFilter().withRoleId(roles("SYS/ADMIN").get(0)).withStatus(Status.ACTIVE), UsersFilter.getDefaultPaging()).getContent().get(0);
	}
	
	private List<AuthenticationGroupIdentifier> groups(String... codes) {
		return Arrays.asList(codes).stream()
				.map(c -> new AuthenticationGroupIdentifier(c))
				.collect(Collectors.toList());
	}

	private List<AuthenticationRoleIdentifier> roles(String... roleCodes) {
		return Arrays.asList(roleCodes).stream()
				.map(c -> new AuthenticationRoleIdentifier(c))
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


	@Override
	public int hashCode() {
		return HashCodeBuilder.reflectionHashCode(this);
	}

	@Override
	public boolean equals(Object obj) {
		return EqualsBuilder.reflectionEquals(this, obj);
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this, ToStringStyle.JSON_STYLE);
	}

}