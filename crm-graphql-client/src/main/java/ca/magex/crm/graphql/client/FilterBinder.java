package ca.magex.crm.graphql.client;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import ca.magex.crm.api.filters.LocationsFilter;
import ca.magex.crm.api.filters.OptionsFilter;
import ca.magex.crm.api.filters.OrganizationsFilter;
import ca.magex.crm.api.filters.PersonsFilter;
import ca.magex.crm.api.filters.UsersFilter;

/**
 * Utility class used to convert Filter objects into a json structure to be used for the graphql filter queries
 * 
 * @author Jonny
 */
public class FilterBinder {

	public static String toFilterString(OrganizationsFilter filter) {
		List<String> filterComponents = new ArrayList<String>();
		if (filter.getDisplayName() != null) {
			filterComponents.add("displayName:\"" + filter.getDisplayName() + "\"");
		}
		if (filter.getStatus() != null) {
			filterComponents.add("status:\"" + filter.getStatus() + "\"");
		}
		if (filter.getAuthenticationGroupId() != null) {
			filterComponents.add("authenticationGroupId:\"" + filter.getAuthenticationGroupId() + "\"");
		}
		if (filter.getBusinessGroupId() != null) {
			filterComponents.add("businessGroupId:\"" + filter.getBusinessGroupId() + "\"");
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
		if (filter.getAuthenticationRoleId() != null) {
			filterComponents.add("authenticationRoleId:\"" + filter.getAuthenticationRoleId() + "\"");
		}
		return StringUtils.join(filterComponents, ", ");
	}
	
	public static String toFilterString(OptionsFilter filter) {
		List<String> filterComponents = new ArrayList<String>();
		if (filter.getParentId() != null) {
			filterComponents.add("parentId:\"" + filter.getParentId() + "\"");
		}
		if (filter.getType() != null) {
			filterComponents.add("type:\"" + filter.getType() + "\"");
		}
		if (filter.getStatus() != null) {
			filterComponents.add("status:\"" + filter.getStatus() + "\"");
		}
		if (filter.getName() != null && filter.getName().getCode() != null) {
			filterComponents.add("code:\"" + filter.getName().getCode() + "\"");
		}
		if (filter.getName() != null && filter.getName().getEnglishName() != null) {
			filterComponents.add("english:\"" + filter.getName().getEnglishName() + "\"");
		}
		if (filter.getName() != null && filter.getName().getFrenchName() != null) {
			filterComponents.add("french:\"" + filter.getName().getFrenchName() + "\"");
		}
		return StringUtils.join(filterComponents, ", ");
	}
		
}