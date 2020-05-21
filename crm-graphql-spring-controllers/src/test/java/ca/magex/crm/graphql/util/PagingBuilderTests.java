package ca.magex.crm.graphql.util;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.data.domain.Sort.Order;

import ca.magex.crm.api.exceptions.ApiException;
import ca.magex.crm.api.filters.Paging;

public class PagingBuilderTests {

	@Test
	public void testBuildPage() {
		Paging paging = new PagingBuilder()
			.withPageNumber(5)
			.withPageSize(3)
			.withSortFields(List.of("A", "B", "C"))
			.withSortDirections(List.of("ASC", "DESC", "ASC"))
			.build();
		
		Assert.assertEquals(5, paging.getPageNumber());
		Assert.assertEquals(3, paging.getPageSize());
		Assert.assertEquals(Order.asc("A"), paging.getSort().getOrderFor("A"));
		Assert.assertEquals(Order.desc("B"), paging.getSort().getOrderFor("B"));
		Assert.assertEquals(Order.asc("C"), paging.getSort().getOrderFor("C"));
	}

	@Test
	public void testBuildPageError() {
		try {
			new PagingBuilder()
					.withPageNumber(5)
					.withPageSize(3)
					.withSortFields(List.of("A", "B", "C", "D"))
					.withSortDirections(List.of("ASC", "DESC", "ASC"))
					.build();
		}
		catch(ApiException e) {
			Assert.assertEquals("sortFields count does not match sortDirections count", e.getMessage());
		}
	}
}
