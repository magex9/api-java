package ca.magex.crm.api.adapters;

/**
 * AUTO-GENERATED: This file is auto-generated by ca.magex.json.javadoc.JavadocInterfaceAdapterBuilder
 * 
 * Aggregate adapter for all the CRM services
 */
public class CrmServicesAdapter implements ca.magex.crm.api.services.CrmLookupService, ca.magex.crm.api.services.CrmPermissionService, ca.magex.crm.api.services.CrmOrganizationService, ca.magex.crm.api.services.CrmLocationService, ca.magex.crm.api.services.CrmPersonService, ca.magex.crm.api.services.CrmUserService {
	
	private ca.magex.crm.api.services.CrmLookupService crmLookupService;
	
	private ca.magex.crm.api.services.CrmPermissionService crmPermissionService;
	
	private ca.magex.crm.api.services.CrmOrganizationService crmOrganizationService;
	
	private ca.magex.crm.api.services.CrmLocationService crmLocationService;
	
	private ca.magex.crm.api.services.CrmPersonService crmPersonService;
	
	private ca.magex.crm.api.services.CrmUserService crmUserService;
	
	public CrmServicesAdapter(ca.magex.crm.api.services.CrmLookupService crmLookupService, ca.magex.crm.api.services.CrmPermissionService crmPermissionService, ca.magex.crm.api.services.CrmOrganizationService crmOrganizationService, ca.magex.crm.api.services.CrmLocationService crmLocationService, ca.magex.crm.api.services.CrmPersonService crmPersonService, ca.magex.crm.api.services.CrmUserService crmUserService) {
		this.crmLookupService = crmLookupService;
		this.crmPermissionService = crmPermissionService;
		this.crmOrganizationService = crmOrganizationService;
		this.crmLocationService = crmLocationService;
		this.crmPersonService = crmPersonService;
		this.crmUserService = crmUserService;
	}
	
	@Override
	public java.util.List<ca.magex.crm.api.system.Status> findStatuses() {
		return crmLookupService.findStatuses();
	}
	
	@Override
	public ca.magex.crm.api.system.Status findStatusByCode(String code) {
		return crmLookupService.findStatusByCode(code);
	}
	
	@Override
	public ca.magex.crm.api.system.Status findStatusByLocalizedName(java.util.Locale locale, String name) {
		return crmLookupService.findStatusByLocalizedName(locale, name);
	}
	
	@Override
	public java.util.List<ca.magex.crm.api.lookup.Country> findCountries() {
		return crmLookupService.findCountries();
	}
	
	@Override
	public ca.magex.crm.api.lookup.Country findCountryByCode(String code) {
		return crmLookupService.findCountryByCode(code);
	}
	
	@Override
	public ca.magex.crm.api.lookup.Country findCountryByLocalizedName(java.util.Locale locale, String name) {
		return crmLookupService.findCountryByLocalizedName(locale, name);
	}
	
	@Override
	public java.util.List<ca.magex.crm.api.lookup.Province> findProvinces(String country) {
		return crmLookupService.findProvinces(country);
	}
	
	@Override
	public ca.magex.crm.api.lookup.Province findProvinceByCode(String province, String country) {
		return crmLookupService.findProvinceByCode(province, country);
	}
	
	@Override
	public ca.magex.crm.api.lookup.Province findProvinceByLocalizedName(java.util.Locale locale, String province, String country) {
		return crmLookupService.findProvinceByLocalizedName(locale, province, country);
	}
	
	@Override
	public java.util.List<ca.magex.crm.api.lookup.Language> findLanguages() {
		return crmLookupService.findLanguages();
	}
	
	@Override
	public ca.magex.crm.api.lookup.Language findLanguageByCode(String code) {
		return crmLookupService.findLanguageByCode(code);
	}
	
	@Override
	public ca.magex.crm.api.lookup.Language findLanguageByLocalizedName(java.util.Locale locale, String name) {
		return crmLookupService.findLanguageByLocalizedName(locale, name);
	}
	
	@Override
	public java.util.List<ca.magex.crm.api.lookup.Salutation> findSalutations() {
		return crmLookupService.findSalutations();
	}
	
	@Override
	public ca.magex.crm.api.lookup.Salutation findSalutationByCode(String code) {
		return crmLookupService.findSalutationByCode(code);
	}
	
	@Override
	public ca.magex.crm.api.lookup.Salutation findSalutationByLocalizedName(java.util.Locale locale, String name) {
		return crmLookupService.findSalutationByLocalizedName(locale, name);
	}
	
	@Override
	public java.util.List<ca.magex.crm.api.lookup.BusinessSector> findBusinessSectors() {
		return crmLookupService.findBusinessSectors();
	}
	
	@Override
	public ca.magex.crm.api.lookup.BusinessSector findBusinessSectorByCode(String code) {
		return crmLookupService.findBusinessSectorByCode(code);
	}
	
	@Override
	public ca.magex.crm.api.lookup.BusinessSector findBusinessSectorByLocalizedName(java.util.Locale locale, String name) {
		return crmLookupService.findBusinessSectorByLocalizedName(locale, name);
	}
	
	@Override
	public java.util.List<ca.magex.crm.api.lookup.BusinessUnit> findBusinessUnits() {
		return crmLookupService.findBusinessUnits();
	}
	
	@Override
	public ca.magex.crm.api.lookup.BusinessUnit findBusinessUnitByCode(String code) {
		return crmLookupService.findBusinessUnitByCode(code);
	}
	
	@Override
	public ca.magex.crm.api.lookup.BusinessUnit findBusinessUnitByLocalizedName(java.util.Locale locale, String name) {
		return crmLookupService.findBusinessUnitByLocalizedName(locale, name);
	}
	
	@Override
	public java.util.List<ca.magex.crm.api.lookup.BusinessClassification> findBusinessClassifications() {
		return crmLookupService.findBusinessClassifications();
	}
	
	@Override
	public ca.magex.crm.api.lookup.BusinessClassification findBusinessClassificationByCode(String code) {
		return crmLookupService.findBusinessClassificationByCode(code);
	}
	
	@Override
	public ca.magex.crm.api.lookup.BusinessClassification findBusinessClassificationByLocalizedName(java.util.Locale locale, String name) {
		return crmLookupService.findBusinessClassificationByLocalizedName(locale, name);
	}
	
	@Override
	public ca.magex.crm.api.roles.Group prototypeGroup(ca.magex.crm.api.system.Localized name) {
		return crmPermissionService.prototypeGroup(name);
	}
	
	@Override
	public ca.magex.crm.api.roles.Group createGroup(ca.magex.crm.api.roles.Group group) {
		return crmPermissionService.createGroup(group);
	}
	
	@Override
	public ca.magex.crm.api.roles.Group createGroup(ca.magex.crm.api.system.Localized name) {
		return crmPermissionService.createGroup(name);
	}
	
	@Override
	public ca.magex.crm.api.roles.Group findGroup(ca.magex.crm.api.system.Identifier groupId) {
		return crmPermissionService.findGroup(groupId);
	}
	
	@Override
	public ca.magex.crm.api.roles.Group findGroupByCode(String code) {
		return crmPermissionService.findGroupByCode(code);
	}
	
	@Override
	public ca.magex.crm.api.roles.Group updateGroupName(ca.magex.crm.api.system.Identifier groupId, ca.magex.crm.api.system.Localized name) {
		return crmPermissionService.updateGroupName(groupId, name);
	}
	
	@Override
	public ca.magex.crm.api.roles.Group enableGroup(ca.magex.crm.api.system.Identifier groupId) {
		return crmPermissionService.enableGroup(groupId);
	}
	
	@Override
	public ca.magex.crm.api.roles.Group disableGroup(ca.magex.crm.api.system.Identifier groupId) {
		return crmPermissionService.disableGroup(groupId);
	}
	
	@Override
	public ca.magex.crm.api.filters.GroupsFilter defaultGroupsFilter() {
		return crmPermissionService.defaultGroupsFilter();
	}
	
	@Override
	public ca.magex.crm.api.system.FilteredPage<ca.magex.crm.api.roles.Group> findGroups(ca.magex.crm.api.filters.GroupsFilter filter, ca.magex.crm.api.filters.Paging paging) {
		return crmPermissionService.findGroups(filter, paging);
	}
	
	@Override
	public ca.magex.crm.api.system.FilteredPage<ca.magex.crm.api.roles.Group> findGroups(ca.magex.crm.api.filters.GroupsFilter filter) {
		return crmPermissionService.findGroups(filter);
	}
	
	@Override
	public java.util.List<String> findActiveGroupCodes() {
		return crmPermissionService.findActiveGroupCodes();
	}
	
	@Override
	public ca.magex.crm.api.roles.Role prototypeRole(ca.magex.crm.api.system.Identifier groupId, ca.magex.crm.api.system.Localized name) {
		return crmPermissionService.prototypeRole(groupId, name);
	}
	
	@Override
	public ca.magex.crm.api.roles.Role createRole(ca.magex.crm.api.roles.Role role) {
		return crmPermissionService.createRole(role);
	}
	
	@Override
	public ca.magex.crm.api.roles.Role createRole(ca.magex.crm.api.system.Identifier groupId, ca.magex.crm.api.system.Localized name) {
		return crmPermissionService.createRole(groupId, name);
	}
	
	@Override
	public ca.magex.crm.api.roles.Role findRole(ca.magex.crm.api.system.Identifier roleId) {
		return crmPermissionService.findRole(roleId);
	}
	
	@Override
	public ca.magex.crm.api.roles.Role findRoleByCode(String code) {
		return crmPermissionService.findRoleByCode(code);
	}
	
	@Override
	public ca.magex.crm.api.roles.Role updateRoleName(ca.magex.crm.api.system.Identifier roleId, ca.magex.crm.api.system.Localized name) {
		return crmPermissionService.updateRoleName(roleId, name);
	}
	
	@Override
	public ca.magex.crm.api.roles.Role enableRole(ca.magex.crm.api.system.Identifier roleId) {
		return crmPermissionService.enableRole(roleId);
	}
	
	@Override
	public ca.magex.crm.api.roles.Role disableRole(ca.magex.crm.api.system.Identifier roleId) {
		return crmPermissionService.disableRole(roleId);
	}
	
	@Override
	public ca.magex.crm.api.system.FilteredPage<ca.magex.crm.api.roles.Role> findRoles(ca.magex.crm.api.filters.RolesFilter filter, ca.magex.crm.api.filters.Paging paging) {
		return crmPermissionService.findRoles(filter, paging);
	}
	
	@Override
	public ca.magex.crm.api.system.FilteredPage<ca.magex.crm.api.roles.Role> findRoles(ca.magex.crm.api.filters.RolesFilter filter) {
		return crmPermissionService.findRoles(filter);
	}
	
	@Override
	public java.util.List<ca.magex.crm.api.roles.Role> findRoles() {
		return crmPermissionService.findRoles();
	}
	
	@Override
	public java.util.List<String> findActiveRoleCodesForGroup(String group) {
		return crmPermissionService.findActiveRoleCodesForGroup(group);
	}
	
	@Override
	public ca.magex.crm.api.filters.RolesFilter defaultRolesFilter() {
		return crmPermissionService.defaultRolesFilter();
	}
	
	@Override
	public ca.magex.crm.api.filters.Paging defaultGroupPaging() {
		return crmPermissionService.defaultGroupPaging();
	}
	
	@Override
	public ca.magex.crm.api.filters.Paging defaultRolePaging() {
		return crmPermissionService.defaultRolePaging();
	}
	
	@Override
	public ca.magex.crm.api.crm.OrganizationDetails prototypeOrganization(String displayName, java.util.List<String> groups) {
		return crmOrganizationService.prototypeOrganization(displayName, groups);
	}
	
	@Override
	public ca.magex.crm.api.crm.OrganizationDetails createOrganization(ca.magex.crm.api.crm.OrganizationDetails prototype) {
		return crmOrganizationService.createOrganization(prototype);
	}
	
	@Override
	public ca.magex.crm.api.crm.OrganizationDetails createOrganization(String displayName, java.util.List<String> groups) {
		return crmOrganizationService.createOrganization(displayName, groups);
	}
	
	@Override
	public ca.magex.crm.api.crm.OrganizationSummary enableOrganization(ca.magex.crm.api.system.Identifier organizationId) {
		return crmOrganizationService.enableOrganization(organizationId);
	}
	
	@Override
	public ca.magex.crm.api.crm.OrganizationSummary disableOrganization(ca.magex.crm.api.system.Identifier organizationId) {
		return crmOrganizationService.disableOrganization(organizationId);
	}
	
	@Override
	public ca.magex.crm.api.crm.OrganizationDetails updateOrganizationDisplayName(ca.magex.crm.api.system.Identifier organizationId, String name) {
		return crmOrganizationService.updateOrganizationDisplayName(organizationId, name);
	}
	
	@Override
	public ca.magex.crm.api.crm.OrganizationDetails updateOrganizationMainLocation(ca.magex.crm.api.system.Identifier organizationId, ca.magex.crm.api.system.Identifier locationId) {
		return crmOrganizationService.updateOrganizationMainLocation(organizationId, locationId);
	}
	
	@Override
	public ca.magex.crm.api.crm.OrganizationDetails updateOrganizationMainContact(ca.magex.crm.api.system.Identifier organizationId, ca.magex.crm.api.system.Identifier personId) {
		return crmOrganizationService.updateOrganizationMainContact(organizationId, personId);
	}
	
	@Override
	public ca.magex.crm.api.crm.OrganizationDetails updateOrganizationGroups(ca.magex.crm.api.system.Identifier organizationId, java.util.List<String> groups) {
		return crmOrganizationService.updateOrganizationGroups(organizationId, groups);
	}
	
	@Override
	public ca.magex.crm.api.crm.OrganizationSummary findOrganizationSummary(ca.magex.crm.api.system.Identifier organizationId) {
		return crmOrganizationService.findOrganizationSummary(organizationId);
	}
	
	@Override
	public ca.magex.crm.api.crm.OrganizationDetails findOrganizationDetails(ca.magex.crm.api.system.Identifier organizationId) {
		return crmOrganizationService.findOrganizationDetails(organizationId);
	}
	
	@Override
	public long countOrganizations(ca.magex.crm.api.filters.OrganizationsFilter filter) {
		return crmOrganizationService.countOrganizations(filter);
	}
	
	@Override
	public ca.magex.crm.api.system.FilteredPage<ca.magex.crm.api.crm.OrganizationDetails> findOrganizationDetails(ca.magex.crm.api.filters.OrganizationsFilter filter, ca.magex.crm.api.filters.Paging paging) {
		return crmOrganizationService.findOrganizationDetails(filter, paging);
	}
	
	@Override
	public ca.magex.crm.api.system.FilteredPage<ca.magex.crm.api.crm.OrganizationSummary> findOrganizationSummaries(ca.magex.crm.api.filters.OrganizationsFilter filter, ca.magex.crm.api.filters.Paging paging) {
		return crmOrganizationService.findOrganizationSummaries(filter, paging);
	}
	
	@Override
	public ca.magex.crm.api.system.FilteredPage<ca.magex.crm.api.crm.OrganizationDetails> findOrganizationDetails(ca.magex.crm.api.filters.OrganizationsFilter filter) {
		return crmOrganizationService.findOrganizationDetails(filter);
	}
	
	@Override
	public ca.magex.crm.api.system.FilteredPage<ca.magex.crm.api.crm.OrganizationSummary> findOrganizationSummaries(ca.magex.crm.api.filters.OrganizationsFilter filter) {
		return crmOrganizationService.findOrganizationSummaries(filter);
	}
	
	@Override
	public ca.magex.crm.api.filters.OrganizationsFilter defaultOrganizationsFilter() {
		return crmOrganizationService.defaultOrganizationsFilter();
	}
	
	@Override
	public ca.magex.crm.api.crm.LocationDetails prototypeLocation(ca.magex.crm.api.system.Identifier organizationId, String reference, String displayName, ca.magex.crm.api.common.MailingAddress address) {
		return crmLocationService.prototypeLocation(organizationId, reference, displayName, address);
	}
	
	@Override
	public ca.magex.crm.api.crm.LocationDetails createLocation(ca.magex.crm.api.crm.LocationDetails prototype) {
		return crmLocationService.createLocation(prototype);
	}
	
	@Override
	public ca.magex.crm.api.crm.LocationDetails createLocation(ca.magex.crm.api.system.Identifier organizationId, String reference, String displayName, ca.magex.crm.api.common.MailingAddress address) {
		return crmLocationService.createLocation(organizationId, reference, displayName, address);
	}
	
	@Override
	public ca.magex.crm.api.crm.LocationSummary enableLocation(ca.magex.crm.api.system.Identifier locationId) {
		return crmLocationService.enableLocation(locationId);
	}
	
	@Override
	public ca.magex.crm.api.crm.LocationSummary disableLocation(ca.magex.crm.api.system.Identifier locationId) {
		return crmLocationService.disableLocation(locationId);
	}
	
	@Override
	public ca.magex.crm.api.crm.LocationDetails updateLocationName(ca.magex.crm.api.system.Identifier locationId, String displaysName) {
		return crmLocationService.updateLocationName(locationId, displaysName);
	}
	
	@Override
	public ca.magex.crm.api.crm.LocationDetails updateLocationAddress(ca.magex.crm.api.system.Identifier locationId, ca.magex.crm.api.common.MailingAddress address) {
		return crmLocationService.updateLocationAddress(locationId, address);
	}
	
	@Override
	public ca.magex.crm.api.crm.LocationSummary findLocationSummary(ca.magex.crm.api.system.Identifier locationId) {
		return crmLocationService.findLocationSummary(locationId);
	}
	
	@Override
	public ca.magex.crm.api.crm.LocationDetails findLocationDetails(ca.magex.crm.api.system.Identifier locationId) {
		return crmLocationService.findLocationDetails(locationId);
	}
	
	@Override
	public long countLocations(ca.magex.crm.api.filters.LocationsFilter filter) {
		return crmLocationService.countLocations(filter);
	}
	
	@Override
	public ca.magex.crm.api.system.FilteredPage<ca.magex.crm.api.crm.LocationDetails> findLocationDetails(ca.magex.crm.api.filters.LocationsFilter filter, ca.magex.crm.api.filters.Paging paging) {
		return crmLocationService.findLocationDetails(filter, paging);
	}
	
	@Override
	public ca.magex.crm.api.system.FilteredPage<ca.magex.crm.api.crm.LocationSummary> findLocationSummaries(ca.magex.crm.api.filters.LocationsFilter filter, ca.magex.crm.api.filters.Paging paging) {
		return crmLocationService.findLocationSummaries(filter, paging);
	}
	
	@Override
	public ca.magex.crm.api.system.FilteredPage<ca.magex.crm.api.crm.LocationDetails> findLocationDetails(ca.magex.crm.api.filters.LocationsFilter filter) {
		return crmLocationService.findLocationDetails(filter);
	}
	
	@Override
	public ca.magex.crm.api.system.FilteredPage<ca.magex.crm.api.crm.LocationSummary> findLocationSummaries(ca.magex.crm.api.filters.LocationsFilter filter) {
		return crmLocationService.findLocationSummaries(filter);
	}
	
	@Override
	public ca.magex.crm.api.system.FilteredPage<ca.magex.crm.api.crm.LocationSummary> findActiveLocationSummariesForOrg(ca.magex.crm.api.system.Identifier organizationId) {
		return crmLocationService.findActiveLocationSummariesForOrg(organizationId);
	}
	
	@Override
	public ca.magex.crm.api.filters.LocationsFilter defaultLocationsFilter() {
		return crmLocationService.defaultLocationsFilter();
	}
	
	@Override
	public ca.magex.crm.api.filters.Paging defaultLocationsPaging() {
		return crmLocationService.defaultLocationsPaging();
	}
	
	@Override
	public ca.magex.crm.api.crm.PersonDetails prototypePerson(ca.magex.crm.api.system.Identifier organizationId, ca.magex.crm.api.common.PersonName name, ca.magex.crm.api.common.MailingAddress address, ca.magex.crm.api.common.Communication communication, ca.magex.crm.api.common.BusinessPosition position) {
		return crmPersonService.prototypePerson(organizationId, name, address, communication, position);
	}
	
	@Override
	public ca.magex.crm.api.crm.PersonDetails createPerson(ca.magex.crm.api.crm.PersonDetails prototype) {
		return crmPersonService.createPerson(prototype);
	}
	
	@Override
	public ca.magex.crm.api.crm.PersonDetails createPerson(ca.magex.crm.api.system.Identifier organizationId, ca.magex.crm.api.common.PersonName name, ca.magex.crm.api.common.MailingAddress address, ca.magex.crm.api.common.Communication communication, ca.magex.crm.api.common.BusinessPosition position) {
		return crmPersonService.createPerson(organizationId, name, address, communication, position);
	}
	
	@Override
	public ca.magex.crm.api.crm.PersonSummary enablePerson(ca.magex.crm.api.system.Identifier personId) {
		return crmPersonService.enablePerson(personId);
	}
	
	@Override
	public ca.magex.crm.api.crm.PersonSummary disablePerson(ca.magex.crm.api.system.Identifier personId) {
		return crmPersonService.disablePerson(personId);
	}
	
	@Override
	public ca.magex.crm.api.crm.PersonDetails updatePersonName(ca.magex.crm.api.system.Identifier personId, ca.magex.crm.api.common.PersonName name) {
		return crmPersonService.updatePersonName(personId, name);
	}
	
	@Override
	public ca.magex.crm.api.crm.PersonDetails updatePersonAddress(ca.magex.crm.api.system.Identifier personId, ca.magex.crm.api.common.MailingAddress address) {
		return crmPersonService.updatePersonAddress(personId, address);
	}
	
	@Override
	public ca.magex.crm.api.crm.PersonDetails updatePersonCommunication(ca.magex.crm.api.system.Identifier personId, ca.magex.crm.api.common.Communication communication) {
		return crmPersonService.updatePersonCommunication(personId, communication);
	}
	
	@Override
	public ca.magex.crm.api.crm.PersonDetails updatePersonBusinessPosition(ca.magex.crm.api.system.Identifier personId, ca.magex.crm.api.common.BusinessPosition position) {
		return crmPersonService.updatePersonBusinessPosition(personId, position);
	}
	
	@Override
	public ca.magex.crm.api.crm.PersonSummary findPersonSummary(ca.magex.crm.api.system.Identifier personId) {
		return crmPersonService.findPersonSummary(personId);
	}
	
	@Override
	public ca.magex.crm.api.crm.PersonDetails findPersonDetails(ca.magex.crm.api.system.Identifier personId) {
		return crmPersonService.findPersonDetails(personId);
	}
	
	@Override
	public long countPersons(ca.magex.crm.api.filters.PersonsFilter filter) {
		return crmPersonService.countPersons(filter);
	}
	
	@Override
	public ca.magex.crm.api.system.FilteredPage<ca.magex.crm.api.crm.PersonSummary> findPersonSummaries(ca.magex.crm.api.filters.PersonsFilter filter, ca.magex.crm.api.filters.Paging paging) {
		return crmPersonService.findPersonSummaries(filter, paging);
	}
	
	@Override
	public ca.magex.crm.api.system.FilteredPage<ca.magex.crm.api.crm.PersonDetails> findPersonDetails(ca.magex.crm.api.filters.PersonsFilter filter, ca.magex.crm.api.filters.Paging paging) {
		return crmPersonService.findPersonDetails(filter, paging);
	}
	
	@Override
	public ca.magex.crm.api.system.FilteredPage<ca.magex.crm.api.crm.PersonDetails> findPersonDetails(ca.magex.crm.api.filters.PersonsFilter filter) {
		return crmPersonService.findPersonDetails(filter);
	}
	
	@Override
	public ca.magex.crm.api.system.FilteredPage<ca.magex.crm.api.crm.PersonSummary> findPersonSummaries(ca.magex.crm.api.filters.PersonsFilter filter) {
		return crmPersonService.findPersonSummaries(filter);
	}
	
	@Override
	public ca.magex.crm.api.system.FilteredPage<ca.magex.crm.api.crm.PersonSummary> findActivePersonSummariesForOrg(ca.magex.crm.api.system.Identifier organizationId) {
		return crmPersonService.findActivePersonSummariesForOrg(organizationId);
	}
	
	@Override
	public ca.magex.crm.api.filters.PersonsFilter defaultPersonsFilter() {
		return crmPersonService.defaultPersonsFilter();
	}
	
	@Override
	public ca.magex.crm.api.roles.User prototypeUser(ca.magex.crm.api.system.Identifier personId, String username, java.util.List<String> roles) {
		return crmUserService.prototypeUser(personId, username, roles);
	}
	
	@Override
	public ca.magex.crm.api.roles.User createUser(ca.magex.crm.api.roles.User prototype) {
		return crmUserService.createUser(prototype);
	}
	
	@Override
	public ca.magex.crm.api.roles.User createUser(ca.magex.crm.api.system.Identifier personId, String username, java.util.List<String> roles) {
		return crmUserService.createUser(personId, username, roles);
	}
	
	@Override
	public ca.magex.crm.api.roles.User enableUser(ca.magex.crm.api.system.Identifier userId) {
		return crmUserService.enableUser(userId);
	}
	
	@Override
	public ca.magex.crm.api.roles.User disableUser(ca.magex.crm.api.system.Identifier userId) {
		return crmUserService.disableUser(userId);
	}
	
	@Override
	public ca.magex.crm.api.roles.User updateUserRoles(ca.magex.crm.api.system.Identifier userId, java.util.List<String> roles) {
		return crmUserService.updateUserRoles(userId, roles);
	}
	
	@Override
	public boolean changePassword(ca.magex.crm.api.system.Identifier userId, String currentPassword, String newPassword) {
		return crmUserService.changePassword(userId, currentPassword, newPassword);
	}
	
	@Override
	public String resetPassword(ca.magex.crm.api.system.Identifier userId) {
		return crmUserService.resetPassword(userId);
	}
	
	@Override
	public ca.magex.crm.api.roles.User findUser(ca.magex.crm.api.system.Identifier userId) {
		return crmUserService.findUser(userId);
	}
	
	@Override
	public ca.magex.crm.api.roles.User findUserByUsername(String username) {
		return crmUserService.findUserByUsername(username);
	}
	
	@Override
	public long countUsers(ca.magex.crm.api.filters.UsersFilter filter) {
		return crmUserService.countUsers(filter);
	}
	
	@Override
	public ca.magex.crm.api.system.FilteredPage<ca.magex.crm.api.roles.User> findUsers(ca.magex.crm.api.filters.UsersFilter filter, ca.magex.crm.api.filters.Paging paging) {
		return crmUserService.findUsers(filter, paging);
	}
	
	@Override
	public boolean isValidPasswordFormat(String password) {
		return crmUserService.isValidPasswordFormat(password);
	}
	
	@Override
	public ca.magex.crm.api.system.FilteredPage<ca.magex.crm.api.roles.User> findUsers(ca.magex.crm.api.filters.UsersFilter filter) {
		return crmUserService.findUsers(filter);
	}
	
	@Override
	public ca.magex.crm.api.system.FilteredPage<ca.magex.crm.api.roles.User> findActiveUserForOrg(ca.magex.crm.api.system.Identifier organizationId) {
		return crmUserService.findActiveUserForOrg(organizationId);
	}
	
	@Override
	public ca.magex.crm.api.filters.UsersFilter defaultUsersFilter() {
		return crmUserService.defaultUsersFilter();
	}
	
	@Override
	public ca.magex.crm.api.filters.Paging defaultUsersPaging() {
		return crmUserService.defaultUsersPaging();
	}
	
}
