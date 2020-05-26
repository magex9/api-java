package ca.magex.crm.graphql.client;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import ca.magex.crm.api.filters.GroupsFilter;
import ca.magex.crm.api.filters.LocationsFilter;
import ca.magex.crm.api.filters.OrganizationsFilter;
import ca.magex.crm.api.filters.PersonsFilter;
import ca.magex.crm.api.filters.RolesFilter;
import ca.magex.crm.api.filters.UsersFilter;

public class FilterBinder {

	public static String toFilterString(OrganizationsFilter filter) {
		List<String> filterComponents = new ArrayList<String>();
		if (filter.getDisplayName() != null) {
			filterComponents.add("displayName:\"" + filter.getDisplayName() + "\"");
		}
		if (filter.getStatus() != null) {
			filterComponents.add("status:\"" + filter.getStatus() + "\"");
		}
		return StringUtils.join(filterComponents, ", ");
	}
	
	public static String toFilterString(LocationsFilter filter) {
		List<String> filterComponents = new ArrayList<String>();
		if (filter.getOrganizationId() != null) {
			filterComponents.add("organizationId:\"" + filter.getOrganizationId() + "\"");
		}
		if (filter.getDisplayName() != null) {
			filterComponents.add("displayName:\"" + filter.getDisplayName() + "\"");
		}
		if (filter.getReference() != null) {
			filterComponents.add("reference:\"" + filter.getReference() + "\"");
		}
		if (filter.getStatus() != null) {
			filterComponents.add("status:\"" + filter.getStatus() + "\"");
		}
		return StringUtils.join(filterComponents, ", ");
	}
	
	public static String toFilterString(PersonsFilter filter) {
		List<String> filterComponents = new ArrayList<String>();
		if (filter.getOrganizationId() != null) {
			filterComponents.add("organizationId:\"" + filter.getOrganizationId() + "\"");
		}
		if (filter.getDisplayName() != null) {
			filterComponents.add("displayName:\"" + filter.getDisplayName() + "\"");
		}
		if (filter.getStatus() != null) {
			filterComponents.add("status:\"" + filter.getStatus() + "\"");
		}
		return StringUtils.join(filterComponents, ", ");
	}
	
	public static String toFilterString(UsersFilter filter) {
		List<String> filterComponents = new ArrayList<String>();
		if (filter.getOrganizationId() != null) {
			filterComponents.add("organizationId:\"" + filter.getOrganizationId() + "\"");
		}
		if (filter.getPersonId() != null) {
			filterComponents.add("personId:\"" + filter.getPersonId() + "\"");
		}
		if (filter.getUsername() != null) {
			filterComponents.add("username:\"" + filter.getUsername() + "\"");
		}
		if (filter.getStatus() != null) {
			filterComponents.add("status:\"" + filter.getStatus() + "\"");
		}
		if (filter.getRole() != null) {
			filterComponents.add("role:\"" + filter.getRole() + "\"");
		}
		return StringUtils.join(filterComponents, ", ");
	}
	
	public static String toFilterString(GroupsFilter filter) {
		List<String> filterComponents = new ArrayList<String>();
		if (filter.getCode() != null) {
			filterComponents.add("code:\"" + filter.getCode() + "\"");
		}
		if (filter.getEnglishName() != null) {
			filterComponents.add("englishName:\"" + filter.getEnglishName() + "\"");
		}
		if (filter.getFrenchName() != null) {
			filterComponents.add("frenchName:\"" + filter.getFrenchName() + "\"");
		}
		if (filter.getStatus() != null) {
			filterComponents.add("status:\"" + filter.getStatus() + "\"");
		}
		return StringUtils.join(filterComponents, ", ");
	}
	
	public static String toFilterString(RolesFilter filter) {
		List<String> filterComponents = new ArrayList<String>();
		if (filter.getCode() != null) {
			filterComponents.add("code:\"" + filter.getCode() + "\"");
		}
		if (filter.getEnglishName() != null) {
			filterComponents.add("englishName:\"" + filter.getEnglishName() + "\"");
		}
		if (filter.getFrenchName() != null) {
			filterComponents.add("frenchName:\"" + filter.getFrenchName() + "\"");
		}
		if (filter.getStatus() != null) {
			filterComponents.add("status:\"" + filter.getStatus() + "\"");
		}
		return StringUtils.join(filterComponents, ", ");
	}
}
