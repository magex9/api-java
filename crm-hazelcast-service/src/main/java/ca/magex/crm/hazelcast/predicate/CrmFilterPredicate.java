package ca.magex.crm.hazelcast.predicate;

import java.util.Map.Entry;

import com.hazelcast.query.Predicate;

import ca.magex.crm.api.filters.CrmFilter;
import ca.magex.crm.api.system.Identifier;

public class CrmFilterPredicate<T> implements Predicate<Identifier, T> {

	private static final long serialVersionUID = 4150271484528540476L;
	
	private CrmFilter<T> filter= null;
	
	public CrmFilterPredicate(CrmFilter<T> filter) {
		this.filter = filter;
	}
	
	@Override
	public boolean apply(Entry<Identifier, T> mapEntry) {
		T value = mapEntry.getValue();
		System.out.println("Filtering Value: " + value);
		return filter.apply(value);
	}
}
