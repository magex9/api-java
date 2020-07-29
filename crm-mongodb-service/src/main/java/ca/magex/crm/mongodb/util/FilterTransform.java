package ca.magex.crm.mongodb.util;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.bson.conversions.Bson;
import org.springframework.data.domain.Sort.Direction;

import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Sorts;

import ca.magex.crm.api.filters.OptionsFilter;
import ca.magex.crm.api.filters.Paging;

public class FilterTransform {

	/**
	 * constructs a Bson based on the given Options Filter
	 * @param filter
	 * @return
	 */
	public static Bson toMatcher(OptionsFilter filter) {
		List<Bson> bsonFilters = new ArrayList<>();
		if (filter.getStatus() != null) {
			bsonFilters.add(Filters.eq("options.status", filter.getStatusCode()));
		}
		if (filter.getParentId() != null) {
			bsonFilters.add(Filters.eq("options.parentId", filter.getParentId().getFullIdentifier()));
		}
		if (StringUtils.isNotBlank(filter.getCode())) {
			bsonFilters.add(Filters.or(
					Filters.eq("options.name.code", filter.getCode()), 						// matches full code
					Filters.regex("options.name.code", "/" + filter.getCode() + "$"))); 	// ends with /code
		}
		if (StringUtils.isNotBlank(filter.getEnglishName())) {
			bsonFilters.add(Filters.eq("options.name.english_searchable", TextUtils.toSearchable(filter.getEnglishName())));
		}		
		if (StringUtils.isNotBlank(filter.getFrenchName())) {
			bsonFilters.add(Filters.eq("options.name.french_searchable", TextUtils.toSearchable(filter.getFrenchName())));
		}
		
		if (bsonFilters.size() == 0) {
			return null;
		}
		else if (bsonFilters.size() == 1) {
			return bsonFilters.get(0);
		}
		else {
			return Filters.and(bsonFilters);
		}
	}
	
	/**
	 * constructs a Bson for sorting based on our paging
	 * @param paging
	 * @return
	 */
	public static Bson toSort(Paging paging, String prefix) {
		List<Bson> bsonSorts = new ArrayList<>();
		paging.getSort().get().forEach((sort) -> {
			if (sort.getDirection() == Direction.ASC) {
				bsonSorts.add(Sorts.ascending(prefix + "." + sort.getProperty()));
			}
			else {
				bsonSorts.add(Sorts.descending(prefix + "." + sort.getProperty()));
			}
		});
		return Sorts.orderBy(bsonSorts);
	}
}
