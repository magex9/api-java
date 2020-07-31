package ca.magex.crm.test.filters;

import java.util.Comparator;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;

import ca.magex.crm.api.common.MailingAddress;
import ca.magex.crm.api.filters.Paging;
import ca.magex.crm.api.filters.Paging.CrmStringComparator;
import ca.magex.crm.api.system.Choice;
import ca.magex.crm.api.system.Localized;
import ca.magex.crm.api.system.Option;
import ca.magex.crm.api.system.Status;
import ca.magex.crm.api.system.Type;
import ca.magex.crm.api.system.id.CountryIdentifier;
import ca.magex.crm.api.system.id.ProvinceIdentifier;

public class PagingTests {
			
	@Test
	public void testPagingComparator() {

		Option canada = new Option(new CountryIdentifier("CA"), null, Type.COUNTRY, Status.ACTIVE, false, new Localized("CA", "Canada", "Canada"));		
		Option ontario = new Option(new ProvinceIdentifier("CA/ON"), canada.getOptionId(), Type.PROVINCE, Status.ACTIVE, false, new Localized("CA/ON", "Ontario", "Ontario"));
		
		MailingAddress ma1 = new MailingAddress("125 Stewart St", "Ottawa", new Choice<>(ontario.getOptionId()), new Choice<>(canada.getOptionId()), "K1N 6J3");
		MailingAddress ma2 = new MailingAddress("125 Stewart St", "Nepean", new Choice<>("Ontario"), new Choice<>("Canada"), "K1N 6J3");
		
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
	
	@Test
	public void testCrmStringComparator() {
		CrmStringComparator comparator = new Paging.CrmStringComparator();
		
		Assert.assertEquals(0, comparator.compare("e", "é"));
	}
}
