package ca.magex.crm.api.filters;

import java.util.Comparator;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;

import ca.magex.crm.api.common.MailingAddress;
import ca.magex.crm.api.lookup.Country;

public class PagingTests {
			
	@Test
	public void testPagingComparator() {

		MailingAddress ma1 = new MailingAddress("125 Stewart St", "Ottawa", "ON", new Country("CA", "Canada"), "K1N 6J3");
		MailingAddress ma2 = new MailingAddress("125 Stewart St", "Nepean", "Ontario", new Country("CA", "Canada"), "K1N 6J3");
		
		Comparator<MailingAddress> comparator = new Paging(Sort.by(Direction.ASC, "city")).new PagingComparator<MailingAddress>();
		Assert.assertEquals(1, comparator.compare(ma1, ma2));
		Assert.assertEquals(-1, comparator.compare(ma2, ma1));
		
		
		comparator = new Paging(Sort.by(Direction.ASC, "postalCode")).new PagingComparator<MailingAddress>();
		Assert.assertEquals(0, comparator.compare(ma1, ma2));
		Assert.assertEquals(0, comparator.compare(ma2, ma1));
		
		comparator = new Paging(Sort.by(Direction.ASC, "postalCode", "city")).new PagingComparator<MailingAddress>();
		Assert.assertEquals(1, comparator.compare(ma1, ma2));
		Assert.assertEquals(-1, comparator.compare(ma2, ma1));
	}
}
