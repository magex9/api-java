package ca.magex.crm.api.filters;

import java.util.Comparator;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import ca.magex.crm.api.filters.Paging.PagingComparator;

public class CrmFilterTests {

	@Test
	public void testDefaultPaging() {
		
		CrmFilter<Object> obj = new CrmFilter<Object>() {			
			private static final long serialVersionUID = -5544980022142116695L;

			@Override
			public boolean apply(Object instance) {				
				return false;
			}
		};
		
		Comparator<Object> comparator = obj.getComparator(Paging.singleInstance());
		
		/* make sure the default comparator is a Paging Comparator */
		Assertions.assertTrue(comparator instanceof PagingComparator);
		
		PagingComparator<Object> pc = (PagingComparator<Object>) comparator;
		Assertions.assertEquals(pc.getPaging(), Paging.singleInstance());
	}
}
