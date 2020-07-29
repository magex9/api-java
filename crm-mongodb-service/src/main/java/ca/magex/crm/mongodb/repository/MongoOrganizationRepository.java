package ca.magex.crm.mongodb.repository;

import com.mongodb.client.MongoDatabase;

import ca.magex.crm.api.crm.OrganizationDetails;
import ca.magex.crm.api.crm.OrganizationSummary;
import ca.magex.crm.api.filters.OrganizationsFilter;
import ca.magex.crm.api.filters.Paging;
import ca.magex.crm.api.observer.CrmUpdateNotifier;
import ca.magex.crm.api.repositories.CrmOrganizationRepository;
import ca.magex.crm.api.system.FilteredPage;
import ca.magex.crm.api.system.id.OrganizationIdentifier;

/**
 * Implementation of the Crm Organization Repository backed by a MongoDB
 * 
 * @author Jonny
 */
public class MongoOrganizationRepository implements CrmOrganizationRepository {

	private MongoDatabase mongoCrm;
	private CrmUpdateNotifier notifier;
	
	/**
	 * Creates our new MongoDB Backed Organization Repository
	 * @param mongoCrm
	 * @param notifier
	 */
	public MongoOrganizationRepository(MongoDatabase mongoCrm, CrmUpdateNotifier notifier) {
		this.mongoCrm = mongoCrm;
		this.notifier = notifier;
	}
	
	@Override
	public OrganizationDetails saveOrganizationDetails(OrganizationDetails organization) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public OrganizationDetails findOrganizationDetails(OrganizationIdentifier organizationId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public OrganizationSummary findOrganizationSummary(OrganizationIdentifier organizationId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public FilteredPage<OrganizationDetails> findOrganizationDetails(OrganizationsFilter filter, Paging paging) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public FilteredPage<OrganizationSummary> findOrganizationSummary(OrganizationsFilter filter, Paging paging) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long countOrganizations(OrganizationsFilter filter) {
		// TODO Auto-generated method stub
		return 0;
	}

}
