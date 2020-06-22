package ca.magex.crm.test.filters;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Test;
import org.springframework.data.domain.Sort;

import ca.magex.crm.api.filters.PageBuilder;
import ca.magex.crm.api.filters.Paging;
import ca.magex.crm.api.filters.RolesFilter;
import ca.magex.crm.api.roles.Role;
import ca.magex.crm.api.system.FilteredPage;
import ca.magex.crm.api.system.Identifier;
import ca.magex.crm.api.system.Localized;
import ca.magex.crm.api.system.Status;

public class PageBuilderTests {

	private Role roleA = new Role(new Identifier("A"), new Identifier("G1"), Status.ACTIVE, new Localized("a", "a", "a"));
	private Role roleB = new Role(new Identifier("B"), new Identifier("G1"), Status.ACTIVE, new Localized("b", "b", "b"));
	private Role roleC = new Role(new Identifier("C"), new Identifier("G1"), Status.ACTIVE, new Localized("c", "c", "c"));
	private Role roleD = new Role(new Identifier("D"), new Identifier("G1"), Status.ACTIVE, new Localized("d", "d", "d"));
	
	@Test
	public void testInstantiate() {
		new PageBuilder();
	}
	
	@Test
	public void testBuildFirstPage() {
		FilteredPage<Role> rolePage = PageBuilder.buildPageFor(
				new RolesFilter(), 
				List.of(roleA, roleB, roleC, roleD),
				new Paging(1, 1, Sort.unsorted()));
		
		assertEquals(1, rolePage.getContent().size());
		assertEquals(roleA, rolePage.getContent().get(0));
	}
	
	@Test
	public void testBuildSecondPage() {
		FilteredPage<Role> rolePage = PageBuilder.buildPageFor(
				new RolesFilter(), 
				List.of(roleA, roleB, roleC, roleD),
				new Paging(2, 1, Sort.unsorted()));
		
		assertEquals(1, rolePage.getContent().size());
		assertEquals(roleB, rolePage.getContent().get(0));
	}
	
	@Test
	public void testBuildLargerPage() {
		FilteredPage<Role> rolePage = PageBuilder.buildPageFor(
				new RolesFilter(), 
				List.of(roleA, roleB),
				new Paging(1, 5, Sort.unsorted()));
		
		assertEquals(2, rolePage.getContent().size());
		assertEquals(roleA, rolePage.getContent().get(0));
		assertEquals(roleB, rolePage.getContent().get(1));
	}
	
	@Test
	public void testBuildFuturePage() {
		FilteredPage<Role> rolePage = PageBuilder.buildPageFor(
				new RolesFilter(), 
				List.of(roleA, roleB),
				new Paging(10, 5, Sort.unsorted()));
		
		assertEquals(0, rolePage.getContent().size());		
	}
	
	@Test
	public void testBuildFullPage() {
		FilteredPage<Role> rolePage = PageBuilder.buildPageFor(
				new RolesFilter(), 
				List.of(roleA, roleB, roleC, roleD),
				new Paging(1, 4, Sort.unsorted()));
		
		assertEquals(4, rolePage.getContent().size());		
	}
}
