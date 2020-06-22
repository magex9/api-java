package ca.magex.crm.test;

import static ca.magex.crm.test.CrmAsserts.ADMIN;
import static ca.magex.crm.test.CrmAsserts.ENGLISH;
import static ca.magex.crm.test.CrmAsserts.FRENCH;
import static ca.magex.crm.test.CrmAsserts.GROUP;
import static ca.magex.crm.test.CrmAsserts.LOCALIZED_SORTED_ENGLISH_ASC;
import static ca.magex.crm.test.CrmAsserts.LOCALIZED_SORTED_ENGLISH_DESC;
import static ca.magex.crm.test.CrmAsserts.LOCALIZED_SORTED_FRENCH_ASC;
import static ca.magex.crm.test.CrmAsserts.LOCALIZED_SORTED_FRENCH_DESC;
import static ca.magex.crm.test.CrmAsserts.LOCALIZED_SORTING_OPTIONS;
import static ca.magex.crm.test.CrmAsserts.ORG;
import static ca.magex.crm.test.CrmAsserts.ORG_ADMIN;
import static ca.magex.crm.test.CrmAsserts.ORG_ASSISTANT;
import static ca.magex.crm.test.CrmAsserts.SYS;
import static ca.magex.crm.test.CrmAsserts.SYS_ADMIN;
import static ca.magex.crm.test.CrmAsserts.assertBadRequestMessage;
import static ca.magex.crm.test.CrmAsserts.assertMessage;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.List;
import java.util.stream.Collectors;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;

import ca.magex.crm.api.Crm;
import ca.magex.crm.api.exceptions.BadRequestException;
import ca.magex.crm.api.filters.GroupsFilter;
import ca.magex.crm.api.filters.Paging;
import ca.magex.crm.api.filters.RolesFilter;
import ca.magex.crm.api.roles.Group;
import ca.magex.crm.api.roles.Role;
import ca.magex.crm.api.system.Identifier;
import ca.magex.crm.api.system.Lang;
import ca.magex.crm.api.system.Localized;
import ca.magex.crm.api.system.Status;

public abstract class AbstractRoleServiceTests {

	@Autowired
	protected Crm crm;
	
	@Before
	public void setup() {
		crm.reset();
	}

	@Test
	public void testRolesWithInvalidCodes() throws Exception {
		Identifier groupId = crm.createGroup(GROUP).getGroupId();
		try {
			crm.createRole(groupId, new Localized(null, "English", "French"));
			fail("Invalid group code");
		} catch (IllegalArgumentException expected) { }
		try {
			crm.createRole(groupId, new Localized("", "English", "French"));
			fail("Invalid group code");
		} catch (BadRequestException e) { 
			assertBadRequestMessage(e, null, "error", "code", "Role code must not be blank");
		}
		try {
			crm.createRole(groupId, new Localized("a", "English", "French"));
			fail("Invalid group code");
		} catch (BadRequestException e) { 
			assertBadRequestMessage(e, null, "error", "code", "Role code must match: .*");
		}
		try {
			crm.createRole(groupId, new Localized("$", "English", "French"));
			fail("Invalid group code");
		} catch (BadRequestException e) { 
			assertBadRequestMessage(e, null, "error", "code", "Role code must match: .*");
		}
	}	
	
	@Test
	public void testCreatingRoleWithoutStatus() throws Exception {
		try {
			crm.validate(new Role(new Identifier("role"), new Identifier("group"), null, GROUP));
			fail("Should fail validation");
		} catch(BadRequestException e) {
			assertBadRequestMessage(e, new Identifier("role"), "error", "status", "Status is mandatory for a role");
		}
	}
	
	@Test
	public void testCreatingDuplicateRoles() throws Exception {
		Identifier groupId = crm.createGroup(SYS).getGroupId();
		Identifier roleId = crm.createRole(groupId, SYS_ADMIN).getRoleId();
		try {
			crm.createRole(groupId, SYS_ADMIN).getRoleId();
			fail("Cannot create duplicate groups");
		} catch (BadRequestException e) { 
			assertBadRequestMessage(e, null, "error", "code", "Duplicate code found in another role: .*");
		}
		crm.disableRole(roleId);
		try {
			crm.createRole(groupId, SYS_ADMIN);
			fail("Cannot create duplicate groups");
		} catch (BadRequestException e) { 
			assertBadRequestMessage(e, null, "error", "code", "Duplicate code found in another role: .*");
		}
	}
	
	@Test
	public void testCreateRoleForDisabledGroup() throws Exception {
		Identifier groupId = crm.createGroup(ORG).getGroupId();
		crm.createRole(groupId, ORG_ADMIN).getRoleId();
		crm.disableGroup(groupId);
		try {
			crm.createRole(groupId, ORG_ASSISTANT).getRoleId();
		} catch (BadRequestException e) {
			assertBadRequestMessage(e, null, "error", "groupId", "Cannot create role for disabled group");
		}
	}
	
	@Test
	public void testCreatingRoleWithBlankNamesGivesMultipleErrors() throws Exception {
		Identifier groupId = crm.createGroup(GROUP).getGroupId();
		try {
			crm.createRole(groupId, new Localized("", "", ""));
			fail("Should fail validation");
		} catch(BadRequestException e) {
			assertEquals(3, e.getMessages().size());
			assertMessage(e.getMessages().get(0), null, "error", "code", "Role code must not be blank");
			assertMessage(e.getMessages().get(1), null, "error", "englishName", "An English description is required");
			assertMessage(e.getMessages().get(2), null, "error", "frenchName", "An French description is required");
		}
		try {
			crm.createRole(groupId, new Localized("", "", ""));
			fail("Should fail validation");
		} catch(BadRequestException e) {
			assertEquals(3, e.getMessages().size());
			assertMessage(e.getMessages().get(0), null, "error", "code", "Role code must not be blank");
			assertMessage(e.getMessages().get(1), null, "error", "englishName", "An English description is required");
			assertMessage(e.getMessages().get(2), null, "error", "frenchName", "An French description is required");
		}
	}
	
	@Test
	public void testGroupPaging() throws Exception {
		for (Localized name : LOCALIZED_SORTING_OPTIONS) {
			crm.createGroup(name);
		}
		GroupsFilter filter = crm.defaultGroupsFilter();
		Page<Group> page1 = crm.findGroups(filter, GroupsFilter.getDefaultPaging().withSort(Sort.by(Order.asc("englishName"))));
		assertEquals(1, page1.getNumber());
		assertEquals(false, page1.hasPrevious());
		assertEquals(true, page1.hasNext());
		assertEquals(10, page1.getContent().size());
		
		Page<Group> page2 = crm.findGroups(filter, GroupsFilter.getDefaultPaging().withSort(Sort.by(Order.asc("frenchName"))).withPageNumber(2));
		assertEquals(2, page2.getNumber());
		assertEquals(true, page2.hasPrevious());
		assertEquals(true, page2.hasNext());
		assertEquals(10, page2.getContent().size());
		
		Page<Group> page3 = crm.findGroups(filter, GroupsFilter.getDefaultPaging().withSort(Sort.by(Order.asc("code"))).withPageNumber(3));
		assertEquals(3, page3.getNumber());		
		assertEquals(true, page3.hasPrevious());
		assertEquals(true, page3.hasNext());
		assertEquals(10, page3.getContent().size());
		
		Page<Group> page4 = crm.findGroups(filter, GroupsFilter.getDefaultPaging().withSort(Sort.by(Order.asc("englishName"))).withPageNumber(4));
		assertEquals(4, page4.getNumber());
		assertEquals(true, page4.hasPrevious());
		assertEquals(false, page4.hasNext());
		assertEquals(2, page4.getContent().size());
	}

	@Test
	public void testGroupSorting() throws Exception {
		for (Localized name : LOCALIZED_SORTING_OPTIONS) {
			crm.createGroup(name);
		}		
		crm.disableGroup(crm.findGroupByCode("E").getGroupId());
		crm.disableGroup(crm.findGroupByCode("F").getGroupId());
		crm.disableGroup(crm.findGroupByCode("H").getGroupId());
		
		GroupsFilter filter = crm.defaultGroupsFilter();
		
		assertEquals(LOCALIZED_SORTED_ENGLISH_ASC,
			crm.findGroups(filter, 
				GroupsFilter.getDefaultPaging().allItems().withSort(Sort.by(Order.asc("englishName"))))
					.getContent().stream().map(g -> g.getName(Lang.ENGLISH)).collect(Collectors.toList()));
			
		assertEquals(LOCALIZED_SORTED_ENGLISH_DESC, 
			crm.findGroups(filter, 
				GroupsFilter.getDefaultPaging().allItems().withSort(Sort.by(Order.desc("englishName"))))
					.getContent().stream().map(g -> g.getName(Lang.ENGLISH)).collect(Collectors.toList()));
				
		assertEquals(LOCALIZED_SORTED_FRENCH_ASC, 
			crm.findGroups(filter, 
				GroupsFilter.getDefaultPaging().allItems().withSort(Sort.by(Order.asc("frenchName"))))
					.getContent().stream().map(g -> g.getName(Lang.FRENCH)).collect(Collectors.toList()));
					
		assertEquals(LOCALIZED_SORTED_FRENCH_DESC, 
			crm.findGroups(filter, 
				GroupsFilter.getDefaultPaging().allItems().withSort(Sort.by(Order.desc("frenchName"))))
					.getContent().stream().map(g -> g.getName(Lang.FRENCH)).collect(Collectors.toList()));
	}
	
	@Test
	public void testGroupFilters() throws Exception {
		crm.createGroup(GROUP);
		crm.createGroup(SYS);
		crm.createGroup(ADMIN);
		crm.disableGroup(crm.createGroup(ENGLISH).getGroupId());
		crm.disableGroup(crm.createGroup(FRENCH).getGroupId());
		
		Paging englishSort = GroupsFilter.getDefaultPaging().withSort(Sort.by(Order.asc("englishName")));
		Paging frenchSort = GroupsFilter.getDefaultPaging().withSort(Sort.by(Order.asc("frenchName")));
		
		assertEquals(List.of(ENGLISH, FRENCH, SYS),
			crm.findGroups(crm.defaultGroupsFilter()
				.withEnglishName("e"), englishSort).stream().map(g -> g.getName()).collect(Collectors.toList()));
			
		assertEquals(List.of(GROUP, SYS),
			crm.findGroups(crm.defaultGroupsFilter()
				.withFrenchName("e"), frenchSort).stream().map(g -> g.getName()).collect(Collectors.toList()));
		
		assertEquals(List.of(ADMIN, GROUP, SYS),
			crm.findGroups(crm.defaultGroupsFilter()
				.withStatus(Status.ACTIVE), englishSort).stream().map(g -> g.getName()).collect(Collectors.toList()));
				
		assertEquals(List.of(ENGLISH, FRENCH),
			crm.findGroups(crm.defaultGroupsFilter()
				.withStatus(Status.INACTIVE), englishSort).stream().map(g -> g.getName()).collect(Collectors.toList()));
				
	}

	@Test
	public void testRoleSorting() throws Exception {
		Identifier group1 = crm.createGroup(SYS).getGroupId();
		Identifier group2 = crm.createGroup(ADMIN).getGroupId();
		for (int i = 0; i < LOCALIZED_SORTING_OPTIONS.size(); i++) {
			if (i < 6) {
				crm.createRole(group1, LOCALIZED_SORTING_OPTIONS.get(i));
			} else {
				crm.createRole(group2, LOCALIZED_SORTING_OPTIONS.get(i));
			}
		}
		crm.disableRole(crm.findRoleByCode(LOCALIZED_SORTING_OPTIONS.get(3).getCode()).getRoleId());
		crm.disableRole(crm.findRoleByCode(LOCALIZED_SORTING_OPTIONS.get(8).getCode()).getRoleId());
		crm.disableRole(crm.findRoleByCode(LOCALIZED_SORTING_OPTIONS.get(11).getCode()).getRoleId());
		
		RolesFilter filter = crm.defaultRolesFilter();
		
		assertEquals(LOCALIZED_SORTED_ENGLISH_ASC,
			crm.findRoles(filter, 
				GroupsFilter.getDefaultPaging().allItems().withSort(Sort.by(Order.asc("englishName"))))
					.getContent().stream().map(g -> g.getName(Lang.ENGLISH)).collect(Collectors.toList()));
			
		assertEquals(LOCALIZED_SORTED_ENGLISH_DESC, 
			crm.findRoles(filter, 
				GroupsFilter.getDefaultPaging().allItems().withSort(Sort.by(Order.desc("englishName"))))
					.getContent().stream().map(g -> g.getName(Lang.ENGLISH)).collect(Collectors.toList()));
				
		assertEquals(LOCALIZED_SORTED_FRENCH_ASC, 
			crm.findRoles(filter, 
				GroupsFilter.getDefaultPaging().allItems().withSort(Sort.by(Order.asc("frenchName"))))
					.getContent().stream().map(g -> g.getName(Lang.FRENCH)).collect(Collectors.toList()));
					
		assertEquals(LOCALIZED_SORTED_FRENCH_DESC, 
			crm.findRoles(filter, 
				GroupsFilter.getDefaultPaging().allItems().withSort(Sort.by(Order.desc("frenchName"))))
					.getContent().stream().map(g -> g.getName(Lang.FRENCH)).collect(Collectors.toList()));
		
		assertEquals(List.of(LOCALIZED_SORTING_OPTIONS.get(8).getEnglishName(), LOCALIZED_SORTING_OPTIONS.get(11).getEnglishName()),
			crm.findRoles(filter.withGroupId(group2).withStatus(Status.INACTIVE), 
				GroupsFilter.getDefaultPaging().allItems().withSort(Sort.by(Order.desc("englishName"))))
					.getContent().stream().map(g -> g.getName(Lang.ENGLISH)).collect(Collectors.toList()));
		
	}
	
	@Test
	public void testRolesFilters() throws Exception {
		Identifier groupId = crm.createGroup(ORG).getGroupId();
		crm.createRole(groupId, GROUP);
		crm.createRole(groupId, SYS);
		crm.createRole(groupId, ADMIN);
		crm.disableRole(crm.createRole(groupId, ENGLISH).getRoleId());
		crm.disableRole(crm.createRole(groupId, FRENCH).getRoleId());
		
		Paging englishSort = RolesFilter.getDefaultPaging().withSort(Sort.by(Order.asc("englishName")));
		Paging frenchSort = RolesFilter.getDefaultPaging().withSort(Sort.by(Order.asc("frenchName")));
		
		assertEquals(List.of(ENGLISH, FRENCH, SYS),
			crm.findRoles(crm.defaultRolesFilter()
				.withEnglishName("e"), englishSort).stream().map(g -> g.getName()).collect(Collectors.toList()));
			
		assertEquals(List.of(GROUP, SYS),
			crm.findRoles(crm.defaultRolesFilter()
				.withFrenchName("e"), frenchSort).stream().map(g -> g.getName()).collect(Collectors.toList()));
		
		assertEquals(List.of(ADMIN, GROUP, SYS),
			crm.findRoles(crm.defaultRolesFilter()
				.withStatus(Status.ACTIVE), englishSort).stream().map(g -> g.getName()).collect(Collectors.toList()));
				
		assertEquals(List.of(ENGLISH, FRENCH),
			crm.findRoles(crm.defaultRolesFilter()
				.withStatus(Status.INACTIVE), englishSort).stream().map(g -> g.getName()).collect(Collectors.toList()));
				
	}
		
}