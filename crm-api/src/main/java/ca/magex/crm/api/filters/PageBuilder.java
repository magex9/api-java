package ca.magex.crm.api.filters;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

import ca.magex.crm.api.system.FilteredPage;

public class PageBuilder {
	
	public static <T> FilteredPage<T> buildPageFor(Serializable filter, List<T> items, Paging paging) {
		int fromIndex = (int) paging.getOffset();
		if (fromIndex > items.size()) {
			return new FilteredPage<T>(filter, paging, Collections.emptyList(), items.size());
		}
		int toIndex = fromIndex + paging.getPageSize();
		if (toIndex > items.size() - 1) {
			toIndex = items.size();
		}
		return new FilteredPage<T>(filter, paging, items.subList(fromIndex, toIndex), items.size());
	}
	
}