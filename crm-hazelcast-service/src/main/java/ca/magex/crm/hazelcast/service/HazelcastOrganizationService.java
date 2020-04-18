package ca.magex.crm.hazelcast.service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang3.SerializationUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.flakeidgen.FlakeIdGenerator;

import ca.magex.crm.api.crm.OrganizationDetails;
import ca.magex.crm.api.crm.OrganizationSummary;
import ca.magex.crm.api.exceptions.ItemNotFoundException;
import ca.magex.crm.api.filters.OrganizationsFilter;
import ca.magex.crm.api.filters.PageBuilder;
import ca.magex.crm.api.filters.Paging;
import ca.magex.crm.api.services.CrmOrganizationService;
import ca.magex.crm.api.system.Identifier;
import ca.magex.crm.api.system.Status;

@Service
@Primary
public class HazelcastOrganizationService implements CrmOrganizationService {

	@Autowired private HazelcastInstance hzInstance;
	
	@Override
	public OrganizationDetails createOrganization(String organizationDisplayName) {
		Map<Identifier, OrganizationDetails> organizations = hzInstance.getMap("organizations");
		FlakeIdGenerator idGenerator = hzInstance.getFlakeIdGenerator("organizations");
		OrganizationDetails orgDetails = new OrganizationDetails(
				new Identifier(Long.toHexString(idGenerator.newId())),
				Status.ACTIVE, 
				organizationDisplayName, 
				null);
		organizations.put(orgDetails.getOrganizationId(), orgDetails);
		return orgDetails;
	}

	@Override
	public OrganizationSummary enableOrganization(Identifier organizationId) {
		Map<Identifier, OrganizationDetails> organizations = hzInstance.getMap("organizations");
		OrganizationDetails orgDetails = organizations.get(organizationId);
		if (orgDetails == null) {
			throw new ItemNotFoundException("Unable to find: " + organizationId);
		}
		if (orgDetails.getStatus() == Status.ACTIVE) {
			return orgDetails;
		}
		orgDetails = orgDetails.withStatus(Status.ACTIVE);
		organizations.put(organizationId, orgDetails);
		return orgDetails;
	}

	@Override
	public OrganizationSummary disableOrganization(Identifier organizationId) {
		Map<Identifier, OrganizationDetails> organizations = hzInstance.getMap("organizations");
		OrganizationDetails orgDetails = organizations.get(organizationId);
		if (orgDetails == null) {
			throw new ItemNotFoundException("Unable to find: " + organizationId);
		}
		if (orgDetails.getStatus() == Status.INACTIVE) {
			return orgDetails;
		}
		orgDetails = orgDetails.withStatus(Status.INACTIVE);
		organizations.put(organizationId, orgDetails);
		return orgDetails;
	}

	@Override
	public OrganizationDetails updateOrganizationDisplayName(Identifier organizationId, String name) {
		Map<Identifier, OrganizationDetails> organizations = hzInstance.getMap("organizations");
		OrganizationDetails orgDetails = organizations.get(organizationId);
		if (orgDetails == null) {
			throw new ItemNotFoundException("Unable to find: " + organizationId);
		}
		if (StringUtils.equals(orgDetails.getDisplayName(), name)) {
			return orgDetails;
		}
		orgDetails = orgDetails.withDisplayName(name);
		organizations.put(organizationId, orgDetails);
		return orgDetails;
	}

	@Override
	public OrganizationDetails updateOrganizationMainLocation(Identifier organizationId, Identifier locationId) {
		Map<Identifier, OrganizationDetails> organizations = hzInstance.getMap("organizations");
		OrganizationDetails orgDetails = organizations.get(organizationId);
		if (orgDetails == null) {
			throw new ItemNotFoundException("Unable to find: " + organizationId);
		}
		if (orgDetails.getMainLocationId() != null && orgDetails.getMainLocationId().equals(locationId)) {
			return orgDetails;
		}
		if (orgDetails.getMainLocationId() == null && locationId == null) {
			return orgDetails;
		}
		orgDetails = orgDetails.withMainLocationId(locationId);
		organizations.put(organizationId, orgDetails);
		return orgDetails;
	}

	@Override
	public OrganizationSummary findOrganizationSummary(Identifier organizationId) {
		return findOrganizationDetails(organizationId);
	}

	@Override
	public OrganizationDetails findOrganizationDetails(Identifier organizationId) {
		Map<Identifier, OrganizationDetails> organizations = hzInstance.getMap("organizations");
		OrganizationDetails orgDetails = organizations.get(organizationId);
		if (orgDetails == null) {
			throw new ItemNotFoundException("Unable to find: " + organizationId);
		}
		return orgDetails;
	}

	@Override
	public long countOrganizations(OrganizationsFilter filter) {
		Map<Identifier, OrganizationDetails> organizations = hzInstance.getMap("organizations");
		return organizations.size();
	}

	@Override
	public Page<OrganizationDetails> findOrganizationDetails(OrganizationsFilter filter, Paging paging) {
		Map<Identifier, OrganizationDetails> organizations = hzInstance.getMap("organizations");
		List<OrganizationDetails> allMatchingOrgs = organizations.values()
			.stream()
			.filter(p -> StringUtils.isNotBlank(filter.getDisplayName()) ? p.getDisplayName().contains(filter.getDisplayName()) : true)
			.filter(i -> filter.getStatus() != null ? i.getStatus().equals(filter.getStatus()) : true)
			.map(i -> SerializationUtils.clone(i))
			.sorted(filter.getComparator(paging))
			.collect(Collectors.toList());
		return PageBuilder.buildPageFor(allMatchingOrgs, paging);
	}

	@Override
	public Page<OrganizationSummary> findOrganizationSummaries(OrganizationsFilter filter, Paging paging) {		
		Map<Identifier, OrganizationDetails> organizations = hzInstance.getMap("organizations");
		List<OrganizationSummary> allMatchingOrgs = organizations.values()
			.stream()
			.filter(p -> StringUtils.isNotBlank(filter.getDisplayName()) ? p.getDisplayName().contains(filter.getDisplayName()) : true)
			.filter(i -> filter.getStatus() != null ? i.getStatus().equals(filter.getStatus()) : true)
			.map(i -> SerializationUtils.clone(i))
			.sorted(filter.getComparator(paging))
			.collect(Collectors.toList());
		return PageBuilder.buildPageFor(allMatchingOrgs, paging);
	}
}