package ca.magex.crm.test.filters;

import static org.junit.Assert.assertEquals;

import java.util.Comparator;

import org.junit.Test;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;

import ca.magex.crm.api.common.MailingAddress;
import ca.magex.crm.api.filters.Paging;
import ca.magex.crm.api.system.Localized;

public class PagingTests {
			
	@Test
	public void testPagingComparator() {

		Localized ontario = new Localized("ON", "Ontario", "Ontario");
		Localized canada = new Localized("CA", "Canada", "Canada");
		
		MailingAddress ma1 = new MailingAddress("125 Stewart St", "Ottawa", ontario.getCode(), canada.getCode(), "K1N 6J3");
		MailingAddress ma2 = new MailingAddress("125 Stewart St", "Nepean", ontario.getCode(), canada.getCode(), "K1N 6J3");
		
		Comparator<MailingAddress> comparator = new Paging(Sort.by(Direction.ASC, "city")).new PagingComparator<MailingAddress>();
		assertEquals(1, comparator.compare(ma1, ma2));
		assertEquals(-1, comparator.compare(ma2, ma1));
		
		comparator = new Paging(Sort.by(Direction.ASC, "postalCode")).new PagingComparator<MailingAddress>();
		assertEquals(0, comparator.compare(ma1, ma2));
		assertEquals(0, comparator.compare(ma2, ma1));
		
		comparator = new Paging(Sort.by(Direction.ASC, "postalCode", "city")).new PagingComparator<MailingAddress>();
		assertEquals(1, comparator.compare(ma1, ma2));
		assertEquals(-1, comparator.compare(ma2, ma1));
		
	}
}
