package ca.magex.crm.amnesia.services;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.lang3.SerializationUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import ca.magex.crm.amnesia.AmnesiaDB;
import ca.magex.crm.api.MagexCrmProfiles;
import ca.magex.crm.api.crm.OrganizationDetails;
import ca.magex.crm.api.crm.OrganizationSummary;
import ca.magex.crm.api.filters.OrganizationsFilter;
import ca.magex.crm.api.filters.PageBuilder;
import ca.magex.crm.api.filters.Paging;
import ca.magex.crm.api.services.CrmOrganizationService;
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
		return db.saveOrganization(new OrganizationDetails(db.generateId(), Status.ACTIVE, organizationDisplayName, null, groups));
	}

	public OrganizationSummary enableOrganization(Identifier organizationId) {
		return db.saveOrganization(findOrganizationDetails(organizationId).withStatus(Status.ACTIVE));
	}

	public OrganizationSummary disableOrganization(Identifier organizationId) {
		return db.saveOrganization(findOrganizationDetails(organizationId).withStatus(Status.INACTIVE));
	}

	public OrganizationDetails updateOrganizationDisplayName(Identifier organizationId, String name) {
		return db.saveOrganization(findOrganizationDetails(organizationId).withDisplayName(name));
	}

	public OrganizationDetails updateOrganizationMainLocation(Identifier organizationId, Identifier locationId) {
		return db.saveOrganization(findOrganizationDetails(organizationId).withMainLocationId(db.findLocation(locationId).getLocationId()));
	}
	
	@Override
	public OrganizationDetails updateOrganizationMainContact(Identifier organizationId, Identifier personId) {
		// TODO Auto-generated method stub
		return null;
	}

	public OrganizationDetails addOrganizationGroup(Identifier organizationId, String group) {
		OrganizationDetails organization = findOrganizationDetails(organizationId);
		List<String> groups = new ArrayList<String>(organization.getGroups());
		if (!groups.contains(group))
			groups.add(group);
		return db.saveOrganization(organization.withGroups(groups));
	}
	
	public OrganizationDetails removeOrganizationGroup(Identifier organizationId, String group) {
		OrganizationDetails organization = findOrganizationDetails(organizationId);
		List<String> groups = new ArrayList<String>(organization.getGroups());
   		groups.remove(group);
   		return db.saveOrganization(organization.withGroups(groups));
	}
	
	public OrganizationDetails updateOrganizationGroups(Identifier organizationId, List<String> groups) {
		return db.saveOrganization(findOrganizationDetails(organizationId).withGroups(groups));
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
			.filter(org -> StringUtils.isNotBlank(filter.getDisplayName()) ? org.getDisplayName().contains(filter.getDisplayName()) : true)
			.filter(org -> filter.getStatus() != null ? org.getStatus().equals(filter.getStatus()) : true);		
	}
	
	@Override
	public long countOrganizations(OrganizationsFilter filter) {
		return apply(filter).count();
	}
	
	public Page<OrganizationSummary> findOrganizationSummaries(OrganizationsFilter filter, Paging paging) {
		List<OrganizationSummary> allMatchingOrgs = apply(filter)
			.map(i -> SerializationUtils.clone(i))
			.sorted(filter.getComparator(paging))
			.collect(Collectors.toList());
		return PageBuilder.buildPageFor(allMatchingOrgs, paging);
	}
	
	public Page<OrganizationDetails> findOrganizationDetails(OrganizationsFilter filter, Paging paging) {
		List<OrganizationDetails> allMatchingOrgs = apply(filter)
			.map(i -> SerializationUtils.clone(i))
			.sorted(filter.getComparator(paging))
			.collect(Collectors.toList());
		return PageBuilder.buildPageFor(allMatchingOrgs, paging);
	}

}