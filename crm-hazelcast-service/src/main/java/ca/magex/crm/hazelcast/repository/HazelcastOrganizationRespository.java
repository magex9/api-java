package ca.magex.crm.hazelcast.repository;

import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.SerializationUtils;

import com.hazelcast.core.TransactionalMap;

import ca.magex.crm.api.crm.OrganizationDetails;
import ca.magex.crm.api.crm.OrganizationSummary;
import ca.magex.crm.api.filters.OrganizationsFilter;
import ca.magex.crm.api.filters.PageBuilder;
import ca.magex.crm.api.filters.Paging;
import ca.magex.crm.api.repositories.CrmOrganizationRepository;
import ca.magex.crm.api.store.CrmStore;
import ca.magex.crm.api.system.FilteredPage;
import ca.magex.crm.api.system.Identifier;
import ca.magex.crm.hazelcast.predicate.CrmFilterPredicate;
import ca.magex.crm.hazelcast.xa.XATransactionAwareHazelcastInstance;

/**
 * An implementation of the Organization Repository that uses the Hazelcast in memory data grid
 * for persisting instances across multiple nodes
 * 
 * @author Jonny
 */
public class HazelcastOrganizationRespository implements CrmOrganizationRepository {

	private XATransactionAwareHazelcastInstance hzInstance;

	/**
	 * Creates the new repository using the backing Transaction Aware Hazelcast Instance
	 * 
	 * @param hzInstance
	 */
	public HazelcastOrganizationRespository(XATransactionAwareHazelcastInstance hzInstance) {
		this.hzInstance = hzInstance;
	}
	
	@Override
	public Identifier generateOrganizationId() {
		return CrmStore.generateId(OrganizationDetails.class);
	}
		
	@Override
	public OrganizationDetails saveOrganizationDetails(OrganizationDetails organization) {
		TransactionalMap<Identifier, OrganizationDetails> organizations = hzInstance.getOrganizationsMap();
		/* persist a clone of this organization, and return the original */
		organizations.put(organization.getOrganizationId(), SerializationUtils.clone(organization));
		return organization;
	}
	
	@Override
	public OrganizationDetails findOrganizationDetails(Identifier organizationId) {
		TransactionalMap<Identifier, OrganizationDetails> organizations = hzInstance.getOrganizationsMap();
		OrganizationDetails orgDetails = organizations.get(organizationId);
		if (orgDetails == null) {
			return null;
		}
		return SerializationUtils.clone(orgDetails);
	}
	
	@Override
	public OrganizationSummary findOrganizationSummary(Identifier organizationId) {
		return findOrganizationDetails(organizationId).asSummary();
	}

	@Override
	public FilteredPage<OrganizationDetails> findOrganizationDetails(OrganizationsFilter filter, Paging paging) {
		TransactionalMap<Identifier, OrganizationDetails> organizations = hzInstance.getOrganizationsMap();
		List<OrganizationDetails> allMatchingOrgs = organizations.values(new CrmFilterPredicate<OrganizationDetails>(filter))
				.stream()				
				.sorted(filter.getComparator(paging))
				.map(i -> SerializationUtils.clone(i))
				.collect(Collectors.toList());
		return PageBuilder.buildPageFor(filter, allMatchingOrgs, paging);
	}

	@Override
	public FilteredPage<OrganizationSummary> findOrganizationSummary(OrganizationsFilter filter, Paging paging) {
		TransactionalMap<Identifier, OrganizationDetails> organizations = hzInstance.getOrganizationsMap();
		List<OrganizationSummary> allMatchingOrgs = organizations.values(new CrmFilterPredicate<OrganizationDetails>(filter))
				.stream()
				.sorted(filter.getComparator(paging))
				.map(i -> i.asSummary())
				.collect(Collectors.toList());
		return PageBuilder.buildPageFor(filter, allMatchingOrgs, paging);
	}

	@Override
	public long countOrganizations(OrganizationsFilter filter) {
		TransactionalMap<Identifier, OrganizationDetails> organizations = hzInstance.getOrganizationsMap();
		return organizations.values(new CrmFilterPredicate<OrganizationDetails>(filter)).size();
	}
}