package ca.magex.crm.hazelcast.service;

import java.util.List;
import java.util.stream.Collectors;

import javax.validation.constraints.NotNull;

import org.apache.commons.lang3.SerializationUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.hazelcast.core.TransactionalMap;
import com.hazelcast.flakeidgen.FlakeIdGenerator;

import ca.magex.crm.api.MagexCrmProfiles;
import ca.magex.crm.api.crm.OrganizationDetails;
import ca.magex.crm.api.crm.OrganizationSummary;
import ca.magex.crm.api.exceptions.BadRequestException;
import ca.magex.crm.api.exceptions.ItemNotFoundException;
import ca.magex.crm.api.filters.OrganizationsFilter;
import ca.magex.crm.api.filters.PageBuilder;
import ca.magex.crm.api.filters.Paging;
import ca.magex.crm.api.services.CrmOrganizationService;
import ca.magex.crm.api.services.CrmPermissionService;
import ca.magex.crm.api.system.FilteredPage;
import ca.magex.crm.api.system.Identifier;
import ca.magex.crm.api.system.Status;
import ca.magex.crm.hazelcast.predicate.CrmFilterPredicate;
import ca.magex.crm.hazelcast.xa.XATransactionAwareHazelcastInstance;

@Service
@Primary
@Profile(MagexCrmProfiles.CRM_DATASTORE_DECENTRALIZED)
@Transactional(propagation = Propagation.REQUIRED, noRollbackFor = {
		ItemNotFoundException.class,
		BadRequestException.class
})
public class HazelcastOrganizationService implements CrmOrganizationService {

	public static String HZ_ORGANIZATION_KEY = "organizations";

	private XATransactionAwareHazelcastInstance hzInstance;
	private CrmPermissionService permissionService;

	public HazelcastOrganizationService(
			XATransactionAwareHazelcastInstance hzInstance,
			CrmPermissionService permissionService) {
		this.hzInstance = hzInstance;
		this.permissionService = permissionService;
	}

	@Override
	public OrganizationDetails createOrganization(String organizationDisplayName, List<String> groups) {
		TransactionalMap<Identifier, OrganizationDetails> organizations = hzInstance.getOrganizationsMap();		
		FlakeIdGenerator idGenerator = hzInstance.getFlakeIdGenerator(HZ_ORGANIZATION_KEY);
		OrganizationDetails orgDetails = new OrganizationDetails(
				new Identifier(Long.toHexString(idGenerator.newId())),
				Status.ACTIVE,
				organizationDisplayName,
				null,
				null,
				groups);
		organizations.put(orgDetails.getOrganizationId(), orgDetails);
		return SerializationUtils.clone(orgDetails);
	}

	@Override
	public OrganizationDetails updateOrganizationDisplayName(Identifier organizationId, String name) {
		TransactionalMap<Identifier, OrganizationDetails> organizations = hzInstance.getOrganizationsMap();
		OrganizationDetails orgDetails = organizations.get(organizationId);
		if (orgDetails == null) {
			throw new ItemNotFoundException("Organization ID '" + organizationId + "'");
		}
		/* nothing to update here */
		if (StringUtils.equals(orgDetails.getDisplayName(), name)) {
			return SerializationUtils.clone(orgDetails);
		}
		orgDetails = orgDetails.withDisplayName(name);
		organizations.put(organizationId, orgDetails);
		return SerializationUtils.clone(orgDetails);
	}

	@Override
	public OrganizationDetails updateOrganizationMainContact(Identifier organizationId, Identifier personId) {
		TransactionalMap<Identifier, OrganizationDetails> organizations = hzInstance.getOrganizationsMap();
		OrganizationDetails orgDetails = organizations.get(organizationId);
		if (orgDetails == null) {
			throw new ItemNotFoundException("Organization ID '" + organizationId + "'");
		}
		/* nothing to update here */
		if (orgDetails.getMainContactId() != null && orgDetails.getMainContactId().equals(personId)) {
			return SerializationUtils.clone(orgDetails);
		}
		orgDetails = orgDetails.withMainContactId(personId);
		organizations.put(organizationId, orgDetails);
		return SerializationUtils.clone(orgDetails);
	}

	@Override
	public OrganizationDetails updateOrganizationMainLocation(Identifier organizationId, Identifier locationId) {
		TransactionalMap<Identifier, OrganizationDetails> organizations = hzInstance.getOrganizationsMap();
		OrganizationDetails orgDetails = organizations.get(organizationId);
		if (orgDetails == null) {
			throw new ItemNotFoundException("Organization ID '" + organizationId + "'");
		}
		/* nothing to update here */
		if (orgDetails.getMainLocationId() != null && orgDetails.getMainLocationId().equals(locationId)) {
			return SerializationUtils.clone(orgDetails);
		}
		orgDetails = orgDetails.withMainLocationId(locationId);
		organizations.put(organizationId, orgDetails);
		return SerializationUtils.clone(orgDetails);
	}

	@Override
	public OrganizationDetails updateOrganizationGroups(Identifier organizationId, List<String> groups) {
		TransactionalMap<Identifier, OrganizationDetails> organizations = hzInstance.getOrganizationsMap();
		OrganizationDetails orgDetails = organizations.get(organizationId);
		if (orgDetails == null) {
			throw new ItemNotFoundException("Organization ID '" + organizationId + "'");
		}
		for (String group : groups) {
			permissionService.findGroupByCode(group); // ensure each group exists
		}
		/* nothing to update here */
		if (orgDetails.getGroups().containsAll(groups) && groups.containsAll(orgDetails.getGroups())) {
			return SerializationUtils.clone(orgDetails);
		}
		orgDetails = orgDetails.withGroups(groups);
		organizations.put(organizationId, orgDetails);
		return SerializationUtils.clone(orgDetails);
	}

	@Override
	public OrganizationSummary enableOrganization(Identifier organizationId) {
		TransactionalMap<Identifier, OrganizationDetails> organizations = hzInstance.getOrganizationsMap();
		OrganizationDetails orgDetails = organizations.get(organizationId);
		if (orgDetails == null) {
			throw new ItemNotFoundException("Organization ID '" + organizationId + "'");
		}
		if (orgDetails.getStatus() == Status.ACTIVE) {
			return SerializationUtils.clone(orgDetails);
		}
		orgDetails = orgDetails.withStatus(Status.ACTIVE);
		organizations.put(organizationId, orgDetails);
		return SerializationUtils.clone(orgDetails);
	}

	@Override
	public OrganizationSummary disableOrganization(Identifier organizationId) {
		TransactionalMap<Identifier, OrganizationDetails> organizations = hzInstance.getOrganizationsMap();
		OrganizationDetails orgDetails = organizations.get(organizationId);
		if (orgDetails == null) {
			throw new ItemNotFoundException("Organization ID '" + organizationId + "'");
		}
		if (orgDetails.getStatus() == Status.INACTIVE) {
			return SerializationUtils.clone(orgDetails);
		}
		orgDetails = orgDetails.withStatus(Status.INACTIVE);
		organizations.put(organizationId, orgDetails);
		return SerializationUtils.clone(orgDetails);
	}

	@Override
	public OrganizationSummary findOrganizationSummary(Identifier organizationId) {
		return findOrganizationDetails(organizationId);
	}

	@Override
	public OrganizationDetails findOrganizationDetails(Identifier organizationId) {
		TransactionalMap<Identifier, OrganizationDetails> organizations = hzInstance.getOrganizationsMap();
		OrganizationDetails orgDetails = organizations.get(organizationId);
		if (orgDetails == null) {
			throw new ItemNotFoundException("Organization ID '" + organizationId + "'");
		}
		return SerializationUtils.clone(orgDetails);
	}

	@Override
	public long countOrganizations(OrganizationsFilter filter) {
		TransactionalMap<Identifier, OrganizationDetails> organizations = hzInstance.getOrganizationsMap();
		return organizations.values(new CrmFilterPredicate<OrganizationSummary>(filter)).size();				
	}

	@Override
	public FilteredPage<OrganizationDetails> findOrganizationDetails(OrganizationsFilter filter, Paging paging) {
		TransactionalMap<Identifier, OrganizationDetails> organizations = hzInstance.getOrganizationsMap();
		List<OrganizationDetails> allMatchingOrgs = organizations.values(new CrmFilterPredicate<OrganizationSummary>(filter))
				.stream()				
				.map(i -> SerializationUtils.clone(i))
				.sorted(filter.getComparator(paging))
				.collect(Collectors.toList());
		return PageBuilder.buildPageFor(filter, allMatchingOrgs, paging);
	}

	@Override
	public FilteredPage<OrganizationSummary> findOrganizationSummaries(OrganizationsFilter filter, Paging paging) {
		TransactionalMap<Identifier, OrganizationDetails> organizations = hzInstance.getOrganizationsMap();
		List<OrganizationSummary> allMatchingOrgs = organizations.values(new CrmFilterPredicate<OrganizationSummary>(filter))
				.stream()
				.map(i -> SerializationUtils.clone(i))
				.sorted(filter.getComparator(paging))
				.collect(Collectors.toList());
		return PageBuilder.buildPageFor(filter, allMatchingOrgs, paging);
	}

	@Override
	public OrganizationDetails findOrganizationByDisplayName(String displayName) {
		return CrmOrganizationService.super.findOrganizationByDisplayName(displayName);
	}

	@Override
	public FilteredPage<OrganizationDetails> findOrganizationDetails(OrganizationsFilter filter) {
		return CrmOrganizationService.super.findOrganizationDetails(filter);
	}

	@Override
	public FilteredPage<OrganizationSummary> findOrganizationSummaries(OrganizationsFilter filter) {
		return CrmOrganizationService.super.findOrganizationSummaries(filter);
	}
	
	@Override
	public OrganizationDetails createOrganization(OrganizationDetails prototype) {	
		return CrmOrganizationService.super.createOrganization(prototype);
	}
	
	@Override
	public OrganizationDetails prototypeOrganization(@NotNull String displayName, @NotNull List<String> groups) {	
		return CrmOrganizationService.super.prototypeOrganization(displayName, groups);
	}
}