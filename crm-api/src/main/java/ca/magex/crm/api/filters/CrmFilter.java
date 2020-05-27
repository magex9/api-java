package ca.magex.crm.api.filters;

import java.io.Serializable;
import java.util.Comparator;

public interface CrmFilter<T> extends Serializable {

	public boolean apply(T instance);
	
	default public Comparator<T> getComparator(Paging paging) {
		return paging.new PagingComparator<T>();
	};
}
