package ca.magex.crm.graphql.util;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.domain.Sort.Order;

import ca.magex.crm.api.exceptions.ApiException;
import ca.magex.crm.api.filters.Paging;

public class PagingBuilder {

	private Integer pageNumber;
	private Integer pageSize;
	private List<String> sortFields;
	private List<String> sortDirections;
	
	public PagingBuilder withPageNumber(Integer pageNumber) {
		this.pageNumber = pageNumber;
		return this;
	}
	
	public PagingBuilder withPageSize(Integer pageSize) {
		this.pageSize = pageSize;
		return this;
	}
	
	public PagingBuilder withSortFields(List<String> sortFields) {
		this.sortFields = sortFields;
		return this;
	}
	
	public PagingBuilder withSortDirections(List<String> sortDirections) {
		this.sortDirections = sortDirections;
		return this;
	}
	
	public Paging build() {
		if (sortFields.size() != sortDirections.size()) {
			throw new ApiException("sortFields count does not match sortDirections count");
		}
		List<Order> ordering = new ArrayList<>();
		for (int i=0; i<sortFields.size(); i++) {
			Direction dir = Direction.valueOf(sortDirections.get(i));
			if (dir == Direction.ASC) {
				ordering.add(Order.asc(sortFields.get(i)));
			}
			else {
				ordering.add(Order.desc(sortFields.get(i)));
			}
		}
		return new Paging(
				pageNumber.intValue(), 
				pageSize.intValue(),
				Sort.by(ordering));
	}
}
