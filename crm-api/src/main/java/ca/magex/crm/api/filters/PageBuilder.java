package ca.magex.crm.api.filters;

import java.util.Collections;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

public class PageBuilder {
	
	public static <T> Page<T> buildPageFor(List<T> items, Pageable pageable) {
		int fromIndex = (int) pageable.getOffset();
		if (fromIndex > items.size()) {
			return new PageImpl<T>(Collections.emptyList(), pageable, items.size());
		}
		int toIndex = fromIndex + pageable.getPageSize();
		if (toIndex > items.size() - 1) {
			toIndex = items.size();
		}
		if (items.size() >= toIndex)
			toIndex = items.size() - 1;
		return new PageImpl<T>(items.subList(fromIndex, toIndex), pageable, items.size());
	}
}
