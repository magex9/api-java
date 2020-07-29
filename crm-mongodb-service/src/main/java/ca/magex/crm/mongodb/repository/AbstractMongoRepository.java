package ca.magex.crm.mongodb.repository;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import org.bson.conversions.Bson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Sort.Direction;

import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Sorts;

import ca.magex.crm.api.filters.Paging;
import ca.magex.crm.api.observer.CrmUpdateNotifier;

/**
 * Base class used for all of the Mono Repositories
 * 
 * @author Jonny
 */
public abstract class AbstractMongoRepository {

	private static final Logger logger = LoggerFactory.getLogger(AbstractMongoRepository.class.getPackageName());
	
	private MongoDatabase mongoCrm;
	private CrmUpdateNotifier notifier;
	
	protected AbstractMongoRepository(MongoDatabase mongoCrm, CrmUpdateNotifier notifier) {
		this.mongoCrm = mongoCrm;
		this.notifier = notifier;
	}
	
	/**
	 * returns our Mongo database client
	 * @return
	 */
	public MongoDatabase getMongoCrm() {
		return mongoCrm;
	}
	
	/**
	 * returns our notifier used when items within the repository change
	 * @return
	 */
	public CrmUpdateNotifier getNotifier() {
		return notifier;
	}
	
	/**
	 * logger helper
	 * @param messageSupplier
	 */
	protected void debug(Supplier<String> messageSupplier) {
		if (logger.isDebugEnabled()) {
			logger.debug(messageSupplier.get());
		}
	}
	
	/**
	 * returns a conjunction of the given components or null of list is empty
	 * @param components
	 * @return
	 */
	protected Bson conjunction(List<Bson> components) {
		if (components.size() == 0) {
			return null;
		}
		else if (components.size() == 1) {
			return components.get(0);
		}
		else {
			return Filters.and(components);
		}
	}
	
	/**
	 * returns a sorting component based on the paging required
	 * @param paging
	 * @return
	 */
	protected Bson sorting(Paging paging) {
		return sorting(paging, null);
	}

	/**
	 * returns a sorting component based on the paging required and the prefix for each field
	 * @param paging
	 * @return
	 */
	protected Bson sorting(Paging paging, String prefix) {
		List<Bson> bsonSorts = new ArrayList<>();
		paging.getSort().get().forEach((sort) -> {
			if (sort.getDirection() == Direction.ASC) {
				bsonSorts.add(Sorts.ascending(prefix == null ? sort.getProperty() : prefix + "." + sort.getProperty()));
			}
			else {
				bsonSorts.add(Sorts.descending(prefix == null ? sort.getProperty() :  prefix + "." + sort.getProperty()));
			}
		});
		return Sorts.orderBy(bsonSorts);
	}
}
