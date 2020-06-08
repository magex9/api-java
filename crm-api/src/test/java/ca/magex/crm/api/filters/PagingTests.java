package ca.magex.crm.api.filters;

import java.util.Comparator;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
//import org.junit.Assert;
//import org.junit.Test;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;

import ca.magex.crm.api.common.MailingAddress;
import ca.magex.crm.api.system.Localized;

@TestInstance(Lifecycle.PER_METHOD)
public class PagingTests {
			
	@Test
	public void testPagingComparator() {

		Localized ontario = new Localized("ON", "Ontario", "Ontario");
		Localized canada = new Localized("CA", "Canada", "Canada");
		
		MailingAddress ma1 = new MailingAddress("125 Stewart St", "Ottawa", ontario.getCode(), canada.getCode(), "K1N 6J3");
		MailingAddress ma2 = new MailingAddress("125 Stewart St", "Nepean", ontario.getCode(), canada.getCode(), "K1N 6J3");
		
		Comparator<MailingAddress> comparator = new Paging(Sort.by(Direction.ASC, "city")).new PagingComparator<MailingAddress>();
		Assertions.assertEquals(1, comparator.compare(ma1, ma2));
		Assertions.assertEquals(-1, comparator.compare(ma2, ma1));
		
		comparator = new Paging(Sort.by(Direction.ASC, "postalCode")).new PagingComparator<MailingAddress>();
		Assertions.assertEquals(0, comparator.compare(ma1, ma2));
		Assertions.assertEquals(0, comparator.compare(ma2, ma1));
		
		comparator = new Paging(Sort.by(Direction.ASC, "postalCode", "city")).new PagingComparator<MailingAddress>();
		Assertions.assertEquals(1, comparator.compare(ma1, ma2));
		Assertions.assertEquals(-1, comparator.compare(ma2, ma1));
		
	}
}
