package ca.magex.crm.mongodb.repository;

import com.mongodb.client.MongoDatabase;

import ca.magex.crm.api.crm.LocationDetails;
import ca.magex.crm.api.crm.LocationSummary;
import ca.magex.crm.api.filters.LocationsFilter;
import ca.magex.crm.api.filters.Paging;
import ca.magex.crm.api.observer.CrmUpdateNotifier;
import ca.magex.crm.api.repositories.CrmLocationRepository;
import ca.magex.crm.api.system.FilteredPage;
import ca.magex.crm.api.system.id.LocationIdentifier;

/**
 * Implementation of the Crm Location Repository backed by a MongoDB
 * 
 * @author Jonny
 */
public class MongoLocationRepository implements CrmLocationRepository {
	
	private MongoDatabase mongoCrm;
	private CrmUpdateNotifier notifier;
	
	/**
	 * Creates our new MongoDB Backed Location Repository
	 * @param mongoCrm
	 * @param notifier
	 */
	public MongoLocationRepository(MongoDatabase mongoCrm, CrmUpdateNotifier notifier) {
		this.mongoCrm = mongoCrm;
		this.notifier = notifier;
	}
	
	@Override
	public LocationDetails saveLocationDetails(LocationDetails location) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public LocationDetails findLocationDetails(LocationIdentifier locationId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public LocationSummary findLocationSummary(LocationIdentifier locationId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public FilteredPage<LocationDetails> findLocationDetails(LocationsFilter filter, Paging paging) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public FilteredPage<LocationSummary> findLocationSummary(LocationsFilter filter, Paging paging) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long countLocations(LocationsFilter filter) {
		// TODO Auto-generated method stub
		return 0;
	}

}
