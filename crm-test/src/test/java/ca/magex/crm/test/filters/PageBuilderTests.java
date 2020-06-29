package ca.magex.crm.test.filters;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Test;
import org.springframework.data.domain.Sort;

import ca.magex.crm.api.filters.OptionsFilter;
import ca.magex.crm.api.filters.PageBuilder;
import ca.magex.crm.api.filters.Paging;
import ca.magex.crm.api.system.FilteredPage;
import ca.magex.crm.api.system.Option;
import ca.magex.crm.api.system.Status;
import ca.magex.crm.api.system.Type;
import ca.magex.crm.api.system.id.CountryIdentifier;
import ca.magex.crm.test.CrmAsserts;

public class PageBuilderTests {

	private Option roleA = new Option(new CountryIdentifier("A"), null, Type.COUNTRY, Status.ACTIVE, true, CrmAsserts.CANADA);
	private Option roleB = new Option(new CountryIdentifier("B"), null, Type.COUNTRY, Status.ACTIVE, true, CrmAsserts.ENGLAND);
	private Option roleC = new Option(new CountryIdentifier("C"), null, Type.COUNTRY, Status.ACTIVE, true, CrmAsserts.MEXICO);
	private Option roleD = new Option(new CountryIdentifier("D"), null, Type.COUNTRY, Status.ACTIVE, true, CrmAsserts.UNITED_STATES);
	
	@Test
	public void testInstantiate() {
		new PageBuilder();
	}
	
	@Test
	public void testBuildFirstPage() {
		FilteredPage<Option> rolePage = PageBuilder.buildPageFor(
				new OptionsFilter(), 
				List.of(roleA, roleB, roleC, roleD),
				new Paging(1, 1, Sort.unsorted()));
		
		assertEquals(1, rolePage.getContent().size());
		assertEquals(roleA, rolePage.getContent().get(0));
	}
	
	@Test
	public void testBuildSecondPage() {
		FilteredPage<Option> rolePage = PageBuilder.buildPageFor(
				new OptionsFilter(), 
				List.of(roleA, roleB, roleC, roleD),
				new Paging(2, 1, Sort.unsorted()));
		
		assertEquals(1, rolePage.getContent().size());
		assertEquals(roleB, rolePage.getContent().get(0));
	}
	
	@Test
	public void testBuildLargerPage() {
		FilteredPage<Option> rolePage = PageBuilder.buildPageFor(
				new OptionsFilter(), 
				List.of(roleA, roleB),
				new Paging(1, 5, Sort.unsorted()));
		
		assertEquals(2, rolePage.getContent().size());
		assertEquals(roleA, rolePage.getContent().get(0));
		assertEquals(roleB, rolePage.getContent().get(1));
	}
	
	@Test
	public void testBuildFuturePage() {
		FilteredPage<Option> rolePage = PageBuilder.buildPageFor(
				new OptionsFilter(), 
				List.of(roleA, roleB),
				new Paging(10, 5, Sort.unsorted()));
		
		assertEquals(0, rolePage.getContent().size());		
	}
	
	@Test
	public void testBuildFullPage() {
		FilteredPage<Option> rolePage = PageBuilder.buildPageFor(
				new OptionsFilter(), 
				List.of(roleA, roleB, roleC, roleD),
				new Paging(1, 4, Sort.unsorted()));
		
		assertEquals(4, rolePage.getContent().size());		
	}
}
