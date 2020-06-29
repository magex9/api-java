package ca.magex.crm.api.repositories.basic;

import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.lang3.SerializationUtils;

import ca.magex.crm.api.crm.OrganizationDetails;
import ca.magex.crm.api.crm.OrganizationSummary;
import ca.magex.crm.api.filters.OrganizationsFilter;
import ca.magex.crm.api.filters.PageBuilder;
import ca.magex.crm.api.filters.Paging;
import ca.magex.crm.api.repositories.CrmOrganizationRepository;
import ca.magex.crm.api.store.CrmStore;
import ca.magex.crm.api.system.FilteredPage;
import ca.magex.crm.api.system.id.OrganizationIdentifier;

public class BasicOrganizationRepository implements CrmOrganizationRepository {

	private CrmStore store;

	public BasicOrganizationRepository(CrmStore store) {
		this.store = store;
	}

	private Stream<OrganizationDetails> apply(OrganizationsFilter filter) {
		return store.getOrganizations().values().stream().filter(p -> filter.apply(p));
	}

	@Override
	public FilteredPage<OrganizationDetails> findOrganizationDetails(OrganizationsFilter filter, Paging paging) {
		return PageBuilder.buildPageFor(filter, apply(filter)
				.map(i -> SerializationUtils.clone(i))
				.sorted(filter.getComparator(paging))
				.collect(Collectors.toList()), paging);
	}

	@Override
	public FilteredPage<OrganizationSummary> findOrganizationSummary(OrganizationsFilter filter, Paging paging) {
		return PageBuilder.buildPageFor(filter, apply(filter)
				.map(i -> SerializationUtils.clone(i))
				.sorted(filter.getComparator(paging))
				.collect(Collectors.toList()), paging);
	}

	@Override
	public long countOrganizations(OrganizationsFilter filter) {
		return apply(filter).count();
	}

	@Override
	public OrganizationDetails findOrganizationDetails(OrganizationIdentifier organizationId) {
		return store.getOrganizations().get(organizationId);
	}

	@Override
	public OrganizationSummary findOrganizationSummary(OrganizationIdentifier organizationId) {
		return findOrganizationDetails(organizationId);
	}

	@Override
	public OrganizationDetails saveOrganizationDetails(OrganizationDetails organization) {
		store.getNotifier().organizationUpdated(System.nanoTime(), organization.getOrganizationId());
		store.getOrganizations().put(organization.getOrganizationId(), organization);
		return organization;
	}
}