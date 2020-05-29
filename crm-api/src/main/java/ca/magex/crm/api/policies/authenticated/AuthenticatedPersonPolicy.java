package ca.magex.crm.api.policies.authenticated;

import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import ca.magex.crm.api.MagexCrmProfiles;
import ca.magex.crm.api.policies.CrmPersonPolicy;
import ca.magex.crm.api.policies.basic.BasicPersonPolicy;
import ca.magex.crm.api.roles.User;
import ca.magex.crm.api.services.CrmAuthenticationService;
import ca.magex.crm.api.services.CrmOrganizationService;
import ca.magex.crm.api.services.CrmPersonService;
import ca.magex.crm.api.services.CrmUserService;
import ca.magex.crm.api.system.Identifier;

@Component
@Primary
@Profile(MagexCrmProfiles.CRM_AUTH)
public class AuthenticatedPersonPolicy extends BaseAuthenticatedPolicy implements CrmPersonPolicy {

	private CrmPersonService personService;	
	private CrmPersonPolicy basicPolicy;
	
	/**
	 * Authenticated Person Policy handles roles and association checks required for policy approval
	 * 
	 * @param authenticationService
	 * @param organizationService
	 * @param personService
	 * @param userService
	 */
	public AuthenticatedPersonPolicy(
			CrmAuthenticationService authenticationService,
			CrmOrganizationService organizationService,
			CrmPersonService personService,
			CrmUserService userService) {
		super(authenticationService, userService);
		this.basicPolicy = new BasicPersonPolicy(organizationService, personService);
		this.personService = personService;		
	}

	@Override
	public boolean canCreatePersonForOrganization(Identifier organizationId) {
		if (!basicPolicy.canCreatePersonForOrganization(organizationId)) {
			return false;
		}
		User currentUser = getCurrentUser();
		/* if the user is a CRM Admin then return true */
		if (isCrmAdmin(currentUser)) {
			return true;
		}
		/* if the current user is associated to the organization, then return true if RE Admin */
		if (currentUser.getPerson().getOrganizationId().equals(organizationId)) {
			return isReAdmin(currentUser);
		}
		/* the current user is not associated with the organization */
		return false;
	}

	@Override
	public boolean canViewPerson(Identifier personId) {
		if (!basicPolicy.canViewPerson(personId)) {
			return false;
		}
		User currentUser = getCurrentUser();
		/* if the user is a CRM Admin then return true */
		if (isCrmAdmin(currentUser)) {
			return true;
		}
		/* ensure the current user is associated to the organization this person belongs to */
		return currentUser.getPerson().getOrganizationId().equals(personService.findPersonSummary(personId).getOrganizationId());
	}

	@Override
	public boolean canUpdatePerson(Identifier personId) {
		if (!basicPolicy.canUpdatePerson(personId)) {
			return false;
		}
		User currentUser = getCurrentUser();
		/* if the user is a CRM Admin then return true */
		if (isCrmAdmin(currentUser)) {
			return true;
		}
		/* can always update yourself */
		if (currentUser.getPerson().getPersonId().equals(personId)) {
			return true;
		}
		/* ensure the current user is associated to the organization this person belongs to and they are an RE Admin */
		if (currentUser.getPerson().getOrganizationId().equals(personService.findPersonSummary(personId).getOrganizationId())) {
			return isReAdmin(currentUser);
		}
		/* the current user is not associated with the organization */
		return false;
	}

	@Override
	public boolean canEnablePerson(Identifier personId) {
		if (!basicPolicy.canEnablePerson(personId)) {
			return false;
		}
		User currentUser = getCurrentUser();
		/* if the user is a CRM_ADMIN then return true */
		if (isCrmAdmin(currentUser)) {
			return true;
		}
		/* ensure the current user is associated to the organization this person belongs to and they are an RE Admin */
		if (currentUser.getPerson().getOrganizationId().equals(personService.findPersonSummary(personId).getOrganizationId())) {
			return isReAdmin(currentUser);
		}
		/* the current user is not associated with the organization */
		return false;
	}

	@Override
	public boolean canDisablePerson(Identifier personId) {
		if (!basicPolicy.canDisablePerson(personId)) {
			return false;
		}
		User currentUser = getCurrentUser();
		/* cannot disable yourself!! */
		if (currentUser.getPerson().getPersonId().equals(personId)) {
			return false;
		}
		/* if the user is a CRM_ADMIN then return true */
		if (isCrmAdmin(currentUser)) {
			return true;
		}
		/* ensure the current user is associated to the organization this person belongs to and they are an RE Admin */
		if (currentUser.getPerson().getOrganizationId().equals(personService.findPersonSummary(personId).getOrganizationId())) {
			return isReAdmin(currentUser);
		}
		/* the current user is not associated with the organization */
		return false;
	}
}