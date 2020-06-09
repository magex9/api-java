package ca.magex.crm.amnesia.services;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.lang3.SerializationUtils;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import ca.magex.crm.amnesia.AmnesiaDB;
import ca.magex.crm.api.MagexCrmProfiles;
import ca.magex.crm.api.crm.OrganizationDetails;
import ca.magex.crm.api.crm.OrganizationSummary;
import ca.magex.crm.api.crm.PersonDetails;
import ca.magex.crm.api.filters.OrganizationsFilter;
import ca.magex.crm.api.filters.PageBuilder;
import ca.magex.crm.api.filters.Paging;
import ca.magex.crm.api.services.CrmOrganizationService;
import ca.magex.crm.api.system.FilteredPage;
import ca.magex.crm.api.system.Identifier;
import ca.magex.crm.api.system.Status;

@Service
@Primary
@Profile(MagexCrmProfiles.CRM_DATASTORE_CENTRALIZED)
public class AmnesiaOrganizationService implements CrmOrganizationService {

	private AmnesiaDB db;
	
	public AmnesiaOrganizationService(AmnesiaDB db) {
		this.db = db;
	}
	
	public OrganizationDetails createOrganization(String organizationDisplayName, List<String> groups) {
		return db.saveOrganization(new OrganizationDetails(db.generateId(), Status.ACTIVE, organizationDisplayName, null, null, groups));
	}

	public OrganizationSummary enableOrganization(Identifier organizationId) {
		OrganizationDetails details = findOrganizationDetails(organizationId);
		if (details == null) {
			return null;
		}
		return db.saveOrganization(details.withStatus(Status.ACTIVE));
	}

	public OrganizationSummary disableOrganization(Identifier organizationId) {
		OrganizationDetails details = findOrganizationDetails(organizationId);
		if (details == null) {
			return null;
		}
		return db.saveOrganization(details.withStatus(Status.INACTIVE));
	}

	public OrganizationDetails updateOrganizationDisplayName(Identifier organizationId, String name) {
		OrganizationDetails details = findOrganizationDetails(organizationId);
		if (details == null) {
			return null;
		}
		return db.saveOrganization(details.withDisplayName(name));
	}

	public OrganizationDetails updateOrganizationMainLocation(Identifier organizationId, Identifier locationId) {
		OrganizationDetails details = findOrganizationDetails(organizationId);
		if (details == null) {
			return null;
		}
		return db.saveOrganization(details.withMainLocationId(locationId == null ? null : db.findLocation(locationId).getLocationId()));
	}
	
	public OrganizationDetails updateOrganizationMainContact(Identifier organizationId, Identifier personId) {
		OrganizationDetails details = findOrganizationDetails(organizationId);
		if (details == null) {
			return null;
		}
		PersonDetails person = db.findPerson(personId);
		return db.saveOrganization(details.withMainContactId(person == null ? null : person.getPersonId()));
	}

	public OrganizationDetails updateOrganizationGroups(Identifier organizationId, List<String> groups) {
		OrganizationDetails details = findOrganizationDetails(organizationId);
		if (details == null) {
			return null;
		}
		return db.saveOrganization(details.withGroups(groups));
	}

	@Override
	public OrganizationSummary findOrganizationSummary(Identifier organizationId) {
		return db.findOrganization(organizationId);
	}
	
	public OrganizationDetails findOrganizationDetails(Identifier organizationId) {
		return db.findOrganization(organizationId);
	}
	
	public Stream<OrganizationDetails> apply(OrganizationsFilter filter) {
		return db.findByType(OrganizationDetails.class)
			.filter(o -> filter.apply(o));
	}
	
	@Override
	public long countOrganizations(OrganizationsFilter filter) {
		return apply(filter).count();
	}
	
	public FilteredPage<OrganizationSummary> findOrganizationSummaries(OrganizationsFilter filter, Paging paging) {
		List<OrganizationSummary> allMatchingOrgs = apply(filter)
			.map(i -> SerializationUtils.clone(i))
			.sorted(filter.getComparator(paging))
			.collect(Collectors.toList());
		return PageBuilder.buildPageFor(filter, allMatchingOrgs, paging);
	}
	
	public FilteredPage<OrganizationDetails> findOrganizationDetails(OrganizationsFilter filter, Paging paging) {
		List<OrganizationDetails> allMatchingOrgs = apply(filter)
			.map(i -> SerializationUtils.clone(i))
			.sorted(filter.getComparator(paging))
			.collect(Collectors.toList());
		return PageBuilder.buildPageFor(filter, allMatchingOrgs, paging);
	}
}