package ca.magex.crm.graphql.util;

import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Sort;

import ca.magex.crm.api.filters.Paging;

public class PagingBuilder {

	private Integer pageNumber;
	private Integer pageSize;
	private String sortField;
	private String sortDirection;
	
	public PagingBuilder withPageNumber(Integer pageNumber) {
		this.pageNumber = pageNumber;
		return this;
	}
	
	public PagingBuilder withPageSize(Integer pageSize) {
		this.pageSize = pageSize;
		return this;
	}
	
	public PagingBuilder withSortField(String sortField) {
		this.sortField = sortField;
		return this;
	}
	
	public PagingBuilder withSortDirection(String sortDirection) {
		this.sortDirection = sortDirection;
		return this;
	}
	
	public Paging build() {
		Sort sort = Sort.by(sortField);
		if (StringUtils.equalsIgnoreCase(sortDirection, "ASC")) {
			sort = sort.ascending();
		}
		else {
			sort = sort.descending();
		}
		
		return new Paging(
				pageNumber.intValue(), 
				pageSize.intValue(),
				sort);
	}
}
