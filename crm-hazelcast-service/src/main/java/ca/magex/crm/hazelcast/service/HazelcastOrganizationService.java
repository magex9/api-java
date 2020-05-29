package ca.magex.crm.hazelcast.service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.validation.constraints.NotNull;

import org.apache.commons.lang3.SerializationUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.flakeidgen.FlakeIdGenerator;

import ca.magex.crm.api.MagexCrmProfiles;
import ca.magex.crm.api.crm.OrganizationDetails;
import ca.magex.crm.api.crm.OrganizationSummary;
import ca.magex.crm.api.exceptions.ItemNotFoundException;
import ca.magex.crm.api.filters.OrganizationsFilter;
import ca.magex.crm.api.filters.PageBuilder;
import ca.magex.crm.api.filters.Paging;
import ca.magex.crm.api.services.CrmOrganizationService;
import ca.magex.crm.api.services.CrmPermissionService;
import ca.magex.crm.api.system.FilteredPage;
import ca.magex.crm.api.system.Identifier;
import ca.magex.crm.api.system.Status;
import ca.magex.crm.api.validation.StructureValidationService;

@Service
@Primary
@Profile(MagexCrmProfiles.CRM_DATASTORE_DECENTRALIZED)
public class HazelcastOrganizationService implements CrmOrganizationService {

	public static String HZ_ORGANIZATION_KEY = "organizations";

	@Autowired private HazelcastInstance hzInstance;
	@Autowired private CrmPermissionService permissionService;

	@Autowired @Lazy private StructureValidationService validationService; // needs to be lazy because it depends on other services

	@Override
	public OrganizationDetails createOrganization(
			@NotNull String organizationDisplayName,
			@NotNull List<String> groups) {
		Map<Identifier, OrganizationDetails> organizations = hzInstance.getMap(HZ_ORGANIZATION_KEY);
		FlakeIdGenerator idGenerator = hzInstance.getFlakeIdGenerator(HZ_ORGANIZATION_KEY);
		OrganizationDetails orgDetails = new OrganizationDetails(
				new Identifier(Long.toHexString(idGenerator.newId())),
				Status.ACTIVE,
				organizationDisplayName,
				null,
				null,
				groups);
		validationService.validate(orgDetails);
		organizations.put(orgDetails.getOrganizationId(), orgDetails);
		return SerializationUtils.clone(orgDetails);
	}

	@Override
	public OrganizationDetails updateOrganizationDisplayName(
			@NotNull Identifier organizationId,
			@NotNull String name) {
		Map<Identifier, OrganizationDetails> organizations = hzInstance.getMap(HZ_ORGANIZATION_KEY);
		OrganizationDetails orgDetails = organizations.get(organizationId);
		if (orgDetails == null) {
			throw new ItemNotFoundException("Organization ID '" + organizationId + "'");
		}
		/* nothing to update here */
		if (StringUtils.equals(orgDetails.getDisplayName(), name)) {
			return SerializationUtils.clone(orgDetails);
		}
		orgDetails = orgDetails.withDisplayName(name);
		validationService.validate(orgDetails);
		organizations.put(organizationId, orgDetails);
		return SerializationUtils.clone(orgDetails);
	}

	@Override
	public OrganizationDetails updateOrganizationMainContact(
			@NotNull Identifier organizationId,
			@NotNull Identifier personId) {
		Map<Identifier, OrganizationDetails> organizations = hzInstance.getMap(HZ_ORGANIZATION_KEY);
		OrganizationDetails orgDetails = organizations.get(organizationId);
		if (orgDetails == null) {
			throw new ItemNotFoundException("Organization ID '" + organizationId + "'");
		}
		/* nothing to update here */
		if (orgDetails.getMainContactId() != null && orgDetails.getMainContactId().equals(personId)) {
			return SerializationUtils.clone(orgDetails);
		}
		orgDetails = orgDetails.withMainContactId(personId);
		validationService.validate(orgDetails);
		organizations.put(organizationId, orgDetails);
		return SerializationUtils.clone(orgDetails);
	}

	@Override
	public OrganizationDetails updateOrganizationMainLocation(
			@NotNull Identifier organizationId,
			@NotNull Identifier locationId) {
		Map<Identifier, OrganizationDetails> organizations = hzInstance.getMap(HZ_ORGANIZATION_KEY);
		OrganizationDetails orgDetails = organizations.get(organizationId);
		if (orgDetails == null) {
			throw new ItemNotFoundException("Organization ID '" + organizationId + "'");
		}
		/* nothing to update here */
		if (orgDetails.getMainLocationId() != null && orgDetails.getMainLocationId().equals(locationId)) {
			return SerializationUtils.clone(orgDetails);
		}
		orgDetails = orgDetails.withMainLocationId(locationId);
		validationService.validate(orgDetails);
		organizations.put(organizationId, orgDetails);
		return SerializationUtils.clone(orgDetails);
	}

	@Override
	public OrganizationDetails updateOrganizationGroups(
			@NotNull Identifier organizationId,
			@NotNull List<String> groups) {
		Map<Identifier, OrganizationDetails> organizations = hzInstance.getMap(HZ_ORGANIZATION_KEY);
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
		validationService.validate(orgDetails);
		organizations.put(organizationId, orgDetails);
		return SerializationUtils.clone(orgDetails);
	}

	@Override
	public OrganizationSummary enableOrganization(
			@NotNull Identifier organizationId) {
		Map<Identifier, OrganizationDetails> organizations = hzInstance.getMap(HZ_ORGANIZATION_KEY);
		OrganizationDetails orgDetails = organizations.get(organizationId);
		if (orgDetails == null) {
			throw new ItemNotFoundException("Organization ID '" + organizationId + "'");
		}
		if (orgDetails.getStatus() == Status.ACTIVE) {
			return SerializationUtils.clone(orgDetails);
		}
		orgDetails = orgDetails.withStatus(Status.ACTIVE);
		validationService.validate(orgDetails);
		organizations.put(organizationId, orgDetails);
		return SerializationUtils.clone(orgDetails);
	}

	@Override
	public OrganizationSummary disableOrganization(
			@NotNull Identifier organizationId) {
		Map<Identifier, OrganizationDetails> organizations = hzInstance.getMap(HZ_ORGANIZATION_KEY);
		OrganizationDetails orgDetails = organizations.get(organizationId);
		if (orgDetails == null) {
			throw new ItemNotFoundException("Organization ID '" + organizationId + "'");
		}
		if (orgDetails.getStatus() == Status.INACTIVE) {
			return SerializationUtils.clone(orgDetails);
		}
		orgDetails = orgDetails.withStatus(Status.INACTIVE);
		validationService.validate(orgDetails);
		organizations.put(organizationId, orgDetails);
		return SerializationUtils.clone(orgDetails);
	}

	@Override
	public OrganizationSummary findOrganizationSummary(
			@NotNull Identifier organizationId) {
		return findOrganizationDetails(organizationId);
	}

	@Override
	public OrganizationDetails findOrganizationDetails(
			@NotNull Identifier organizationId) {
		Map<Identifier, OrganizationDetails> organizations = hzInstance.getMap(HZ_ORGANIZATION_KEY);
		OrganizationDetails orgDetails = organizations.get(organizationId);
		if (orgDetails == null) {
			throw new ItemNotFoundException("Organization ID '" + organizationId + "'");
		}
		return SerializationUtils.clone(orgDetails);
	}

	@Override
	public long countOrganizations(
			@NotNull OrganizationsFilter filter) {
		Map<Identifier, OrganizationDetails> organizations = hzInstance.getMap(HZ_ORGANIZATION_KEY);
		return organizations.values()
				.stream()
				.filter(o -> filter.apply(o))
				.count();
	}

	@Override
	public FilteredPage<OrganizationDetails> findOrganizationDetails(
			@NotNull OrganizationsFilter filter,
			@NotNull Paging paging) {
		Map<Identifier, OrganizationDetails> organizations = hzInstance.getMap(HZ_ORGANIZATION_KEY);
		List<OrganizationDetails> allMatchingOrgs = organizations.values()
				.stream()
				.filter(o -> filter.apply(o))
				.map(i -> SerializationUtils.clone(i))
				.sorted(filter.getComparator(paging))
				.collect(Collectors.toList());
		return PageBuilder.buildPageFor(filter, allMatchingOrgs, paging);
	}

	@Override
	public FilteredPage<OrganizationSummary> findOrganizationSummaries(
			@NotNull OrganizationsFilter filter,
			@NotNull Paging paging) {
		Map<Identifier, OrganizationDetails> organizations = hzInstance.getMap(HZ_ORGANIZATION_KEY);
		List<OrganizationSummary> allMatchingOrgs = organizations.values()
				.stream()
				.filter(o -> filter.apply(o))
				.map(i -> SerializationUtils.clone(i))
				.sorted(filter.getComparator(paging))
				.collect(Collectors.toList());
		return PageBuilder.buildPageFor(filter, allMatchingOrgs, paging);
	}
}