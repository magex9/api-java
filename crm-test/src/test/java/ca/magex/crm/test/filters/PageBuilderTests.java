package ca.magex.crm.test.filters;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Test;
import org.springframework.data.domain.Sort;

import ca.magex.crm.api.filters.OptionsFilter;
import ca.magex.crm.api.filters.PageBuilder;
import ca.magex.crm.api.filters.Paging;
import ca.magex.crm.api.system.FilteredPage;
import ca.magex.crm.api.system.Localized;
import ca.magex.crm.api.system.Option;
import ca.magex.crm.api.system.Status;
import ca.magex.crm.api.system.Type;
import ca.magex.crm.api.system.id.CountryIdentifier;

public class PageBuilderTests {

	private Option ca = new Option(new CountryIdentifier("CA"), null, Type.COUNTRY, Status.ACTIVE, true, new Localized("CA", "Canada", "Canada"));
	private Option us = new Option(new CountryIdentifier("US"), null, Type.COUNTRY, Status.ACTIVE, true, new Localized("US", "United States", "Etats Unis"));
	private Option mx = new Option(new CountryIdentifier("MX"), null, Type.COUNTRY, Status.ACTIVE, true, new Localized("MX", "Mexico", "Mexico"));
	private Option de = new Option(new CountryIdentifier("DE"), null, Type.COUNTRY, Status.ACTIVE, true, new Localized("DE", "Germany", "Germany"));
	
	@Test
	public void testInstantiate() {
		new PageBuilder();
	}
	
	@Test
	public void testBuildFirstPage() {
		FilteredPage<Option> rolePage = PageBuilder.buildPageFor(
				new OptionsFilter(), 
				List.of(ca, us, mx, de),
				new Paging(1, 1, Sort.unsorted()));
		
		assertEquals(1, rolePage.getContent().size());
		assertEquals(ca, rolePage.getContent().get(0));
	}
	
	@Test
	public void testBuildSecondPage() {
		FilteredPage<Option> rolePage = PageBuilder.buildPageFor(
				new OptionsFilter(), 
				List.of(ca, us, mx, de),
				new Paging(2, 1, Sort.unsorted()));
		
		assertEquals(1, rolePage.getContent().size());
		assertEquals(us, rolePage.getContent().get(0));
	}
	
	@Test
	public void testBuildLargerPage() {
		FilteredPage<Option> rolePage = PageBuilder.buildPageFor(
				new OptionsFilter(), 
				List.of(ca, us),
				new Paging(1, 5, Sort.unsorted()));
		
		assertEquals(2, rolePage.getContent().size());
		assertEquals(ca, rolePage.getContent().get(0));
		assertEquals(us, rolePage.getContent().get(1));
	}
	
	@Test
	public void testBuildFuturePage() {
		FilteredPage<Option> rolePage = PageBuilder.buildPageFor(
				new OptionsFilter(), 
				List.of(ca, us),
				new Paging(10, 5, Sort.unsorted()));
		
		assertEquals(0, rolePage.getContent().size());		
	}
	
	@Test
	public void testBuildFullPage() {
		FilteredPage<Option> rolePage = PageBuilder.buildPageFor(
				new OptionsFilter(), 
				List.of(ca, us, mx, de),
				new Paging(1, 4, Sort.unsorted()));
		
		assertEquals(4, rolePage.getContent().size());		
	}
}
