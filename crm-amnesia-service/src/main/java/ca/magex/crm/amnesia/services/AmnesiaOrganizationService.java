package ca.magex.crm.amnesia.services;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.lang3.SerializationUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import ca.magex.crm.amnesia.AmnesiaDB;
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
public class AmnesiaOrganizationService implements CrmOrganizationService {

	@Autowired private AmnesiaDB db;
	
	public AmnesiaOrganizationService() {}
	
	public OrganizationDetails createOrganization(String organizationDisplayName) {
		return db.saveOrganization(new OrganizationDetails(db.generateId(), Status.ACTIVE, organizationDisplayName, null));
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
	public OrganizationSummary findOrganizationSummary(Identifier organizationId) {
		return db.findOrganization(organizationId);
	}
	
	public OrganizationDetails findOrganizationDetails(Identifier organizationId) {
		return db.findOrganization(organizationId);
	}
	
	public Stream<OrganizationDetails> apply(OrganizationsFilter filter) {
		return db.findByType(OrganizationDetails.class)
			.filter(p -> StringUtils.isNotBlank(filter.getDisplayName()) ? p.getDisplayName().contains(filter.getDisplayName()) : true)
			.filter(i -> filter.getStatus() != null ? i.getStatus().equals(filter.getStatus()) : true);		
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