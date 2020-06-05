package ca.magex.crm.test;

import static ca.magex.crm.test.CrmAsserts.CANADA;
import static ca.magex.crm.test.CrmAsserts.ENGLISH;
import static ca.magex.crm.test.CrmAsserts.ONTARIO;
import static ca.magex.crm.test.CrmAsserts.SYS;
import static ca.magex.crm.test.CrmAsserts.SYS_ADMIN;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

import org.junit.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;

import ca.magex.crm.api.common.BusinessPosition;
import ca.magex.crm.api.common.Communication;
import ca.magex.crm.api.common.MailingAddress;
import ca.magex.crm.api.common.PersonName;
import ca.magex.crm.api.common.Telephone;
import ca.magex.crm.api.crm.LocationDetails;
import ca.magex.crm.api.crm.LocationSummary;
import ca.magex.crm.api.crm.OrganizationDetails;
import ca.magex.crm.api.crm.OrganizationSummary;
import ca.magex.crm.api.crm.PersonDetails;
import ca.magex.crm.api.crm.PersonSummary;
import ca.magex.crm.api.exceptions.BadRequestException;
import ca.magex.crm.api.exceptions.ItemNotFoundException;
import ca.magex.crm.api.filters.GroupsFilter;
import ca.magex.crm.api.filters.LocationsFilter;
import ca.magex.crm.api.filters.OrganizationsFilter;
import ca.magex.crm.api.filters.Paging;
import ca.magex.crm.api.filters.PersonsFilter;
import ca.magex.crm.api.filters.RolesFilter;
import ca.magex.crm.api.filters.UsersFilter;
import ca.magex.crm.api.lookup.BusinessClassification;
import ca.magex.crm.api.lookup.BusinessSector;
import ca.magex.crm.api.lookup.BusinessUnit;
import ca.magex.crm.api.lookup.Country;
import ca.magex.crm.api.lookup.CrmLookupItem;
import ca.magex.crm.api.lookup.Language;
import ca.magex.crm.api.lookup.Province;
import ca.magex.crm.api.lookup.Salutation;
import ca.magex.crm.api.roles.Group;
import ca.magex.crm.api.roles.Role;
import ca.magex.crm.api.roles.User;
import ca.magex.crm.api.services.Crm;
import ca.magex.crm.api.system.FilteredPage;
import ca.magex.crm.api.system.Identifier;
import ca.magex.crm.api.system.Lang;
import ca.magex.crm.api.system.Localized;
import ca.magex.crm.api.system.Status;
import ca.magex.crm.test.function.TriFunction;

/**
 * Test suite for running an end to end test of the CRM Services
 * 
 * @author Jonny
 *
 */
public class CrmServicesTestSuite {

	private static final Logger logger = LoggerFactory.getLogger(CrmServicesTestSuite.class);

	private Crm crm;

	public CrmServicesTestSuite(Crm crm) {
		this.crm = crm;
	}
	
	public void runAllTests() throws Exception {
		crm.reset();
		if (!crm.isInitialized())
			crm.initializeSystem("system", new PersonName(null, "Sys", null, "Admin"), "system@admin.com", "admin", "admin");
		crm.dump();
		runLookupServiceTests();
		try {
			runCreatePermissions();
			Identifier orgIdentifier = runOrganizationServiceTests();
			runLocationServiceTests(orgIdentifier);
			Identifier personIdentifer = runPersonServiceTests(orgIdentifier);
			runUserServiceTests(personIdentifer);
		} catch (BadRequestException e) {
			e.printMessages(System.out);
		}
	}

	private void runLookupServiceTests() {
		logger.info("----------------------------");
		logger.info("Running Lookup Service Tests");
		logger.info("----------------------------");
		runLookupTest(Status.class, crm::findStatuses, crm::findStatusByCode, crm::findStatusByLocalizedName);
		runLookupTest(Country.class, crm::findCountries, crm::findCountryByCode, crm::findCountryByLocalizedName);
		runLookupTest(Language.class, crm::findLanguages, crm::findLanguageByCode, crm::findLanguageByLocalizedName);
		runLookupTest(Salutation.class, crm::findSalutations, crm::findSalutationByCode, crm::findSalutationByLocalizedName);
		runLookupTest(BusinessSector.class, crm::findBusinessSectors, crm::findBusinessSectorByCode, crm::findBusinessSectorByLocalizedName);
		runLookupTest(BusinessUnit.class, crm::findBusinessUnits, crm::findBusinessUnitByCode, crm::findBusinessUnitByLocalizedName);
		runLookupTest(BusinessClassification.class, crm::findBusinessClassifications, crm::findBusinessClassificationByCode, crm::findBusinessClassificationByLocalizedName);
		runQualifiedLookupTest(Province.class, crm::findProvinces, crm.findCountryByCode("CA"), crm::findProvinceByCode, crm::findProvinceByLocalizedName);
		runQualifiedLookupTest(Province.class, crm::findProvinces, crm.findCountryByCode("MX"), crm::findProvinceByCode, crm::findProvinceByLocalizedName);
		runQualifiedLookupTest(Province.class, crm::findProvinces, crm.findCountryByCode("US"), crm::findProvinceByCode, crm::findProvinceByLocalizedName);
		
	}

	private <T extends CrmLookupItem> void runLookupTest(Class<T> item, Supplier<List<T>> supplier, Function<String, T> codeLookup, BiFunction<Locale, String, T> localizedLookup) {
		/* countries tests */
		List<T> values = supplier.get();
		for (T value : values) {
			Assert.assertEquals(value, codeLookup.apply(value.getCode()));
			Assert.assertEquals(value, localizedLookup.apply(Lang.ENGLISH, value.getName(Lang.ENGLISH)));
			Assert.assertEquals(value, localizedLookup.apply(Lang.FRENCH, value.getName(Lang.FRENCH)));
			try {
				localizedLookup.apply(Lang.ENGLISH, "????");
				Assert.fail("Unsupported Value");
			} catch (ItemNotFoundException e) {
			}

			try {
				localizedLookup.apply(Locale.GERMAN, "");
				Assert.fail("Unsupported Country");
			} catch (ItemNotFoundException e) {
			}
		}
		logger.info("Running lookup tests for " + item.getName() + " Passed");
	}
	
	private <T extends CrmLookupItem> void runQualifiedLookupTest(Class<T> item, Function<String, List<T>> supplier, CrmLookupItem qualifier, BiFunction<String, String, T> codeLookup, TriFunction<Locale, String, String, T> localizedLookup) {
		/* countries tests */
		List<T> values = supplier.apply(qualifier.getCode());
		for (T value : values) {
			Assert.assertEquals(value, codeLookup.apply(value.getCode(), qualifier.getCode()));
			Assert.assertEquals(value, localizedLookup.apply(Lang.ENGLISH, value.getName(Lang.ENGLISH), qualifier.getName(Lang.ENGLISH)));
			Assert.assertEquals(value, localizedLookup.apply(Lang.FRENCH, value.getName(Lang.FRENCH), qualifier.getName(Lang.FRENCH)));
			try {
				localizedLookup.apply(Lang.ENGLISH, "????", qualifier.getName(Lang.ENGLISH));
				Assert.fail("Unsupported Value");
			} catch (ItemNotFoundException e) {
			}

			try {
				localizedLookup.apply(Locale.GERMAN, "", "Hans");
				Assert.fail("Unsupported Country");
			} catch (ItemNotFoundException e) {
			}
		}
		logger.info("Running lookup tests for " + item.getName() + " Passed");
	}

	private void runCreatePermissions() {
		Group system = crm.findGroupByCode(SYS.getCode());
		Identifier sysId = system.getGroupId();
		verifyGroupDetails(system, sysId, Status.ACTIVE, SYS.getCode(), SYS.getEnglishName(), SYS.getFrenchName());
		Assert.assertEquals(system, crm.findGroup(sysId));
		Assert.assertEquals(system, crm.findGroupByCode(SYS.getCode()));
		verifyGroupDetails(crm.disableGroup(sysId), sysId, Status.INACTIVE, SYS.getCode(), SYS.getEnglishName(), SYS.getFrenchName());
		verifyGroupDetails(crm.enableGroup(sysId), sysId, Status.ACTIVE, SYS.getCode(), SYS.getEnglishName(), SYS.getFrenchName());
		verifyGroupDetails(crm.updateGroupName(sysId, new Localized(SYS.getCode(), "ENGLISH", "FRENCH")), sysId, Status.ACTIVE, SYS.getCode(), "ENGLISH", "FRENCH");
		verifyGroupDetails(crm.updateGroupName(sysId, new Localized(SYS.getCode(), SYS.getEnglishName(), SYS.getFrenchName())), sysId, Status.ACTIVE, SYS.getCode(), SYS.getEnglishName(), SYS.getFrenchName());
		FilteredPage<Group> groupPage = crm.findGroups(new GroupsFilter(null, null, SYS.getCode(), null), new Paging(Sort.by("code")));
		Assert.assertEquals(1, groupPage.getContent().size());

		Role sysadmin = crm.findRoleByCode(SYS_ADMIN.getCode());
		verifyRoleDetails(sysadmin, sysId, sysadmin.getRoleId(), Status.ACTIVE, SYS_ADMIN.getCode(), SYS_ADMIN.getEnglishName(), SYS_ADMIN.getFrenchName());
		verifyRoleDetails(crm.disableRole(sysadmin.getRoleId()), sysId, sysadmin.getRoleId(), Status.INACTIVE, SYS_ADMIN.getCode(), SYS_ADMIN.getEnglishName(), SYS_ADMIN.getFrenchName());
		verifyRoleDetails(crm.enableRole(sysadmin.getRoleId()), sysId, sysadmin.getRoleId(), Status.ACTIVE, SYS_ADMIN.getCode(), SYS_ADMIN.getEnglishName(), SYS_ADMIN.getFrenchName());
		verifyRoleDetails(crm.updateRoleName(sysadmin.getRoleId(), new Localized(SYS_ADMIN.getCode(), "ENGLISH", "FRENCH")), sysId, sysadmin.getRoleId(), Status.ACTIVE, SYS_ADMIN.getCode(), "ENGLISH", "FRENCH");
		verifyRoleDetails(crm.updateRoleName(sysadmin.getRoleId(), new Localized(SYS_ADMIN.getCode(), SYS_ADMIN.getEnglishName(), SYS_ADMIN.getFrenchName())), sysId, sysadmin.getRoleId(), Status.ACTIVE, SYS_ADMIN.getCode(),
				SYS_ADMIN.getEnglishName(), SYS_ADMIN.getFrenchName());
		verifyRoleDetails(crm.findRole(sysadmin.getRoleId()), sysId, sysadmin.getRoleId(), Status.ACTIVE, SYS_ADMIN.getCode(), SYS_ADMIN.getEnglishName(), SYS_ADMIN.getFrenchName());
		verifyRoleDetails(crm.findRoleByCode(sysadmin.getCode()), sysId, sysadmin.getRoleId(), Status.ACTIVE, SYS_ADMIN.getCode(), SYS_ADMIN.getEnglishName(), SYS_ADMIN.getFrenchName());
		FilteredPage<Role> rolePage = crm.findRoles(new RolesFilter(null, null, null, SYS_ADMIN.getCode(), null), new Paging(Sort.by("code")));
		Assert.assertEquals(1, rolePage.getContent().size());

		/* create a second group */
		Group dev = crm.createGroup(new Localized("DEV", "developers", "developeurs"));
		crm.createRole(dev.getGroupId(), new Localized("JAVA", "java", "java"));
	}

	private Identifier runOrganizationServiceTests() {
		logger.info("----------------------------------");
		logger.info("Running Organization Service Tests");
		logger.info("----------------------------------");
		/* get initial organization count */
		long orgCount = crm.countOrganizations(new OrganizationsFilter());
		logger.info("Organization Count: " + orgCount);

		/* create and verify new organization */
		logger.info("Creating new Organiztion");
		OrganizationDetails orgDetails = crm.createOrganization("ABC", List.of(SYS.getCode()));
		Identifier orgId = orgDetails.getOrganizationId();
		logger.info("Generated OrgId: " + orgId);
		verifyOrgDetails(orgDetails, orgId, Status.ACTIVE, "ABC", null, null, List.of("SYS"));		

		/* verify that we our organization count incremented by 1 */
		long newOrgCount = crm.countOrganizations(new OrganizationsFilter());
		logger.info("Organization Count: " + newOrgCount);
		Assert.assertEquals(orgCount + 1, newOrgCount);

		/* create and verify new location for organization */
		logger.info("Creating main Location");
		MailingAddress address = new MailingAddress("54 fifth street", "Toronto", ONTARIO.getCode(), CANADA.getCode(), "T5R5X3");
		LocationDetails locDetails = crm.createLocation(
				orgId,
				"HeadQuarters",
				"HQ",
				address);
		Identifier locId = locDetails.getLocationId();
		logger.info("Generated locId: " + locId);

		/* set and verify organization main location */
		logger.info("Updating Organization main Location");
		orgDetails = crm.updateOrganizationMainLocation(orgDetails.getOrganizationId(), locDetails.getLocationId());
		verifyOrgDetails(orgDetails, orgId, Status.ACTIVE, "ABC", locId, null, List.of("SYS"));

		/* set and verify organization main contact */
		logger.info("Creating main Contact");
		PersonDetails personDetails = crm.createPerson(
				orgId,
				CrmAsserts.PERSON_NAME,
				CrmAsserts.MAILING_ADDRESS,
				CrmAsserts.WORK_COMMUNICATIONS,
				CrmAsserts.BUSINESS_POSITION);
		Identifier personId = personDetails.getPersonId();
		logger.info("Generated personId: " + personId);

		/* set and verify organization main contact */
		logger.info("Updating Organization main Contact");
		orgDetails = crm.updateOrganizationMainContact(orgDetails.getOrganizationId(), personDetails.getPersonId());
		verifyOrgDetails(orgDetails, orgId, Status.ACTIVE, "ABC", locId, personId, List.of("SYS"));

		/* update and verify organization name */
		logger.info("Updating Organization name");
		String newName = "ABC" + System.currentTimeMillis();
		orgDetails = crm.updateOrganizationDisplayName(orgDetails.getOrganizationId(), newName);
		verifyOrgDetails(orgDetails, orgId, Status.ACTIVE, newName, locId, personId, List.of("SYS"));

		/* update and verify organization groups */
		logger.info("Updating Organization groups");
		orgDetails = crm.updateOrganizationGroups(orgDetails.getOrganizationId(), List.of("SYS", "DEV"));
		verifyOrgDetails(orgDetails, orgId, Status.ACTIVE, newName, locId, personId, List.of("SYS", "DEV"));

		/* disable and verify organization */
		logger.info("Disabling Organization");
		OrganizationSummary orgSummary = crm.disableOrganization(orgDetails.getOrganizationId());
		verifyOrgSummary(orgSummary, orgId, Status.INACTIVE, newName);

		orgSummary = crm.findOrganizationSummary(orgDetails.getOrganizationId());
		verifyOrgSummary(orgSummary, orgId, Status.INACTIVE, newName);

		orgDetails = crm.findOrganizationDetails(orgDetails.getOrganizationId());
		verifyOrgDetails(orgDetails, orgId, Status.INACTIVE, newName, locId, personId, List.of("SYS", "DEV"));

		/* enable and verify organization */
		logger.info("Enabling Organization");
		orgSummary = crm.enableOrganization(orgDetails.getOrganizationId());
		verifyOrgSummary(orgSummary, orgId, Status.ACTIVE, newName);

		orgSummary = crm.findOrganizationSummary(orgDetails.getOrganizationId());
		verifyOrgSummary(orgSummary, orgId, Status.ACTIVE, newName);

		orgDetails = crm.findOrganizationDetails(orgDetails.getOrganizationId());
		verifyOrgDetails(orgDetails, orgId, Status.ACTIVE, newName, locId, personId, List.of("SYS", "DEV"));

		/* validate details paging with 1 match on name filter */
		logger.info("Finding Organization Details with Name Match");
		Page<OrganizationDetails> orgDetailsPage = crm.findOrganizationDetails(new OrganizationsFilter(newName, Status.ACTIVE, null), new Paging(1, 5, Sort.by(Direction.ASC, "displayName")));
		Assert.assertEquals(1, orgDetailsPage.getNumber());
		Assert.assertEquals(1, orgDetailsPage.getTotalPages());
		Assert.assertEquals(1, orgDetailsPage.getNumberOfElements());
		Assert.assertEquals(1, orgDetailsPage.getTotalElements());
		Assert.assertEquals(1, orgDetailsPage.getContent().size());
		Assert.assertEquals(orgDetails, orgDetailsPage.getContent().get(0));

		/* validate details paging with no match on name filter */
		logger.info("Finding Organization Details without Name Match");
		orgDetailsPage = crm.findOrganizationDetails(new OrganizationsFilter(newName + "00", Status.ACTIVE, null), new Paging(1, 5, Sort.by(Direction.ASC, "displayName")));
		Assert.assertEquals(1, orgDetailsPage.getNumber());
		Assert.assertEquals(0, orgDetailsPage.getTotalPages());
		Assert.assertEquals(0, orgDetailsPage.getNumberOfElements());
		Assert.assertEquals(0, orgDetailsPage.getTotalElements());
		Assert.assertEquals(0, orgDetailsPage.getContent().size());

		/* validate summary paging with 1 match on name filter */
		logger.info("Finding Organization Summary without Name Match");
		Page<OrganizationSummary> orgSummaryPage = crm.findOrganizationSummaries(new OrganizationsFilter(newName, Status.ACTIVE, null), new Paging(1, 5, Sort.by(Direction.ASC, "displayName")));
		Assert.assertEquals(1, orgSummaryPage.getNumber());
		Assert.assertEquals(1, orgSummaryPage.getTotalPages());
		Assert.assertEquals(1, orgSummaryPage.getNumberOfElements());
		Assert.assertEquals(1, orgSummaryPage.getTotalElements());
		Assert.assertEquals(1, orgSummaryPage.getContent().size());
		Assert.assertEquals(orgSummary, orgSummaryPage.getContent().get(0));

		/* validate summary paging with no match on name filter */
		logger.info("Finding Organization Summary without Name Match");
		orgSummaryPage = crm.findOrganizationSummaries(new OrganizationsFilter(newName + "00", Status.ACTIVE, null), new Paging(1, 5, Sort.by(Direction.ASC, "displayName")));
		Assert.assertEquals(1, orgSummaryPage.getNumber());
		Assert.assertEquals(0, orgSummaryPage.getTotalPages());
		Assert.assertEquals(0, orgSummaryPage.getNumberOfElements());
		Assert.assertEquals(0, orgSummaryPage.getTotalElements());
		Assert.assertEquals(0, orgSummaryPage.getContent().size());

		return orgDetails.getOrganizationId();
	}

	private void runLocationServiceTests(Identifier orgId) {
		logger.info("------------------------------");
		logger.info("Running Location Service Tests");
		logger.info("------------------------------");
		OrganizationDetails orgDetails = crm.findOrganizationDetails(orgId);
		Identifier locId = orgDetails.getMainLocationId();

		/* retrieve the location details */
		final LocationDetails originalLocationDetails = crm.findLocationDetails(locId);

		/* disable location and verify the result */
		logger.info("Disabling Location");
		LocationSummary locSummary = crm.disableLocation(orgDetails.getMainLocationId());
		verifyLocationSummary(locSummary, orgId, locId, Status.INACTIVE, originalLocationDetails.getReference(), originalLocationDetails.getDisplayName());

		locSummary = crm.findLocationSummary(locId);
		verifyLocationSummary(locSummary, orgId, locId, Status.INACTIVE, originalLocationDetails.getReference(), originalLocationDetails.getDisplayName());

		LocationDetails locDetails = crm.findLocationDetails(locId);
		verifyLocationDetails(locDetails, orgId, locId, Status.INACTIVE, originalLocationDetails.getReference(), originalLocationDetails.getDisplayName(), originalLocationDetails.getAddress());

		/* enable location and verify the result */
		logger.info("Enabling Location");
		locSummary = crm.enableLocation(orgDetails.getMainLocationId());
		verifyLocationSummary(locSummary, orgId, locId, Status.ACTIVE, originalLocationDetails.getReference(), originalLocationDetails.getDisplayName());

		locSummary = crm.findLocationSummary(locId);
		verifyLocationSummary(locSummary, orgId, locId, Status.ACTIVE, originalLocationDetails.getReference(), originalLocationDetails.getDisplayName());

		locDetails = crm.findLocationDetails(locId);
		verifyLocationDetails(locDetails, orgId, locId, Status.ACTIVE, originalLocationDetails.getReference(), originalLocationDetails.getDisplayName(), originalLocationDetails.getAddress());

		/* update and verify the location name */
		logger.info("Updating Location Name");
		String newName = originalLocationDetails.getDisplayName() + System.currentTimeMillis();
		locDetails = crm.updateLocationName(locId, newName);
		verifyLocationDetails(locDetails, orgId, locId, Status.ACTIVE, originalLocationDetails.getReference(), newName, originalLocationDetails.getAddress());

		locSummary = crm.findLocationSummary(locId);
		verifyLocationSummary(locSummary, orgId, locId, Status.ACTIVE, originalLocationDetails.getReference(), newName);

		/* update and verify the location address */
		logger.info("Updating Location Address");
		MailingAddress newAddress = new MailingAddress("55 second street", "Toronto", ONTARIO.getCode(), CANADA.getCode(), "T5R5X3");
		locDetails = crm.updateLocationAddress(locId, newAddress);
		verifyLocationDetails(locDetails, orgId, locId, Status.ACTIVE, originalLocationDetails.getReference(), newName, newAddress);

		locSummary = crm.findLocationSummary(locId);
		verifyLocationSummary(locSummary, orgId, locId, Status.ACTIVE, originalLocationDetails.getReference(), newName);

		/* validate details paging with 1 match on name filter */
		logger.info("Finding Location Details with Name Match");
		Page<LocationDetails> locDetailsPage = crm.findLocationDetails(new LocationsFilter(orgDetails.getOrganizationId(), newName, null, Status.ACTIVE), new Paging(1, 5, Sort.by(Direction.ASC, "displayName")));
		Assert.assertEquals(1, locDetailsPage.getNumber());
		Assert.assertEquals(1, locDetailsPage.getTotalPages());
		Assert.assertEquals(1, locDetailsPage.getNumberOfElements());
		Assert.assertEquals(1, locDetailsPage.getTotalElements());
		Assert.assertEquals(1, locDetailsPage.getContent().size());
		Assert.assertEquals(locDetails, locDetailsPage.getContent().get(0));

		/* validate details paging with no match on name filter */
		logger.info("Finding Location Details without Name Match");
		locDetailsPage = crm.findLocationDetails(new LocationsFilter(orgDetails.getOrganizationId(), newName + "00", null, Status.ACTIVE), new Paging(1, 5, Sort.by(Direction.ASC, "displayName")));
		Assert.assertEquals(1, locDetailsPage.getNumber());
		Assert.assertEquals(0, locDetailsPage.getTotalPages());
		Assert.assertEquals(0, locDetailsPage.getNumberOfElements());
		Assert.assertEquals(0, locDetailsPage.getTotalElements());
		Assert.assertEquals(0, locDetailsPage.getContent().size());

		/* validate summary paging with 1 match on name filter */
		logger.info("Finding Location Summary without Name Match");
		Page<LocationSummary> locSummaryPage = crm.findLocationSummaries(new LocationsFilter(orgDetails.getOrganizationId(), newName, null, Status.ACTIVE), new Paging(1, 5, Sort.by(Direction.ASC, "displayName")));
		Assert.assertEquals(1, locSummaryPage.getNumber());
		Assert.assertEquals(1, locSummaryPage.getTotalPages());
		Assert.assertEquals(1, locSummaryPage.getNumberOfElements());
		Assert.assertEquals(1, locSummaryPage.getTotalElements());
		Assert.assertEquals(1, locSummaryPage.getContent().size());
		Assert.assertEquals(locSummary, locSummaryPage.getContent().get(0));

		/* validate summary paging with no match on name filter */
		logger.info("Finding Location Summary without Name Match");
		locSummaryPage = crm.findLocationSummaries(new LocationsFilter(orgDetails.getOrganizationId(), newName + "00", null, Status.ACTIVE), new Paging(1, 5, Sort.by(Direction.ASC, "displayName")));
		Assert.assertEquals(1, locSummaryPage.getNumber());
		Assert.assertEquals(0, locSummaryPage.getTotalPages());
		Assert.assertEquals(0, locSummaryPage.getNumberOfElements());
		Assert.assertEquals(0, locSummaryPage.getTotalElements());
		Assert.assertEquals(0, locSummaryPage.getContent().size());
	}

	private Identifier runPersonServiceTests(Identifier orgId) {
		logger.info("----------------------------");
		logger.info("Running Person Service Tests");
		logger.info("----------------------------");
		long personCount = crm.countPersons(new PersonsFilter());
		final PersonName originalName = new PersonName("3", "Mike", "Peter", "Johns");
		final MailingAddress originalAddress = new MailingAddress("12 ninth street", "Ottawa", ONTARIO.getCode(), CANADA.getCode(), "K4J9O9");
		final Communication originalComms = new Communication("Engineer", ENGLISH.getCode(), "Mike.Johns@ABC.ca", new Telephone("6135554545"), "6135554545");
		final BusinessPosition originalPosition = new BusinessPosition("IM/IT", "Solutions", "Team Lead");

		/* create a person and verify results */
		logger.info("Creating new Person");
		PersonDetails personDetails = crm.createPerson(orgId,
				originalName,
				originalAddress,
				originalComms,
				originalPosition);
		Assert.assertEquals(personCount + 1, crm.countPersons(new PersonsFilter()));
		verifyPersonDetails(personDetails, orgId, personDetails.getPersonId(), Status.ACTIVE, originalName.getDisplayName(), originalName, originalAddress, originalComms, originalPosition);

		/* disable and verify the results */
		logger.info("Disabling Person");
		PersonSummary personSummary = crm.disablePerson(personDetails.getPersonId());
		verifyPersonSummary(personSummary, orgId, personDetails.getPersonId(), Status.INACTIVE, originalName.getDisplayName());

		personSummary = crm.findPersonSummary(personDetails.getPersonId());
		verifyPersonSummary(personSummary, orgId, personDetails.getPersonId(), Status.INACTIVE, originalName.getDisplayName());

		personDetails = crm.findPersonDetails(personDetails.getPersonId());
		verifyPersonDetails(personDetails, orgId, personDetails.getPersonId(), Status.INACTIVE, originalName.getDisplayName(), originalName, originalAddress, originalComms, originalPosition);

		/* enable and verify the results */
		logger.info("Enabling Person");
		personSummary = crm.enablePerson(personDetails.getPersonId());
		verifyPersonSummary(personSummary, orgId, personDetails.getPersonId(), Status.ACTIVE, originalName.getDisplayName());

		personSummary = crm.findPersonSummary(personDetails.getPersonId());
		verifyPersonSummary(personSummary, orgId, personDetails.getPersonId(), Status.ACTIVE, originalName.getDisplayName());

		personDetails = crm.findPersonDetails(personDetails.getPersonId());
		verifyPersonDetails(personDetails, orgId, personDetails.getPersonId(), Status.ACTIVE, originalName.getDisplayName(), originalName, originalAddress, originalComms, originalPosition);

		/* update person name and verify */
		logger.info("Updating Person Name");
		final PersonName newName = new PersonName("2", "Susan", System.currentTimeMillis() + "", "Anderson");
		personDetails = crm.updatePersonName(personDetails.getPersonId(), newName);
		verifyPersonDetails(personDetails, orgId, personDetails.getPersonId(), Status.ACTIVE, newName.getDisplayName(), newName, originalAddress, originalComms, originalPosition);

		personSummary = crm.findPersonSummary(personDetails.getPersonId());
		verifyPersonSummary(personSummary, orgId, personDetails.getPersonId(), Status.ACTIVE, newName.getDisplayName());

		personDetails = crm.findPersonDetails(personDetails.getPersonId());
		verifyPersonDetails(personDetails, orgId, personDetails.getPersonId(), Status.ACTIVE, newName.getDisplayName(), newName, originalAddress, originalComms, originalPosition);

		/* update person address and verify */
		logger.info("Updating Person Address");
		final MailingAddress newAddress = new MailingAddress("15 fourth street", "Ottawa", ONTARIO.getCode(), CANADA.getCode(), "K4J9O9");
		personDetails = crm.updatePersonAddress(personDetails.getPersonId(), newAddress);
		verifyPersonDetails(personDetails, orgId, personDetails.getPersonId(), Status.ACTIVE, newName.getDisplayName(), newName, newAddress, originalComms, originalPosition);

		personSummary = crm.findPersonSummary(personDetails.getPersonId());
		verifyPersonSummary(personSummary, orgId, personDetails.getPersonId(), Status.ACTIVE, newName.getDisplayName());

		personDetails = crm.findPersonDetails(personDetails.getPersonId());
		verifyPersonDetails(personDetails, orgId, personDetails.getPersonId(), Status.ACTIVE, newName.getDisplayName(), newName, newAddress, originalComms, originalPosition);

		/* update person communications and verify */
		logger.info("Updating Person Communiation");
		final Communication newComms = new Communication("Supervisor", ENGLISH.getCode(), "Susan.Anderson@ABC.ca", new Telephone("6135554543", ""), "6135554543");
		personDetails = crm.updatePersonCommunication(personDetails.getPersonId(), newComms);
		verifyPersonDetails(personDetails, orgId, personDetails.getPersonId(), Status.ACTIVE, newName.getDisplayName(), newName, newAddress, newComms, originalPosition);

		personSummary = crm.findPersonSummary(personDetails.getPersonId());
		verifyPersonSummary(personSummary, orgId, personDetails.getPersonId(), Status.ACTIVE, newName.getDisplayName());

		personDetails = crm.findPersonDetails(personDetails.getPersonId());
		verifyPersonDetails(personDetails, orgId, personDetails.getPersonId(), Status.ACTIVE, newName.getDisplayName(), newName, newAddress, newComms, originalPosition);

		/* update person position and verify */
		logger.info("Updating Person Business Position");
		final BusinessPosition newPosition = new BusinessPosition("IM/IT", "Solutions", "Developer");
		personDetails = crm.updatePersonBusinessPosition(personDetails.getPersonId(), newPosition);
		verifyPersonDetails(personDetails, orgId, personDetails.getPersonId(), Status.ACTIVE, newName.getDisplayName(), newName, newAddress, newComms, newPosition);

		personSummary = crm.findPersonSummary(personDetails.getPersonId());
		verifyPersonSummary(personSummary, orgId, personDetails.getPersonId(), Status.ACTIVE, newName.getDisplayName());

		personDetails = crm.findPersonDetails(personDetails.getPersonId());
		verifyPersonDetails(personDetails, orgId, personDetails.getPersonId(), Status.ACTIVE, newName.getDisplayName(), newName, newAddress, newComms, newPosition);

		/* validate details paging with 1 match on name filter */
		logger.info("Finding Person Details with Name Match");
		Page<PersonDetails> personDetailsPage = crm.findPersonDetails(new PersonsFilter(orgId, newName.getDisplayName(), Status.ACTIVE), new Paging(1, 5, Sort.by(Direction.ASC, "displayName")));
		Assert.assertEquals(1, personDetailsPage.getNumber());
		Assert.assertEquals(1, personDetailsPage.getTotalPages());
		Assert.assertEquals(1, personDetailsPage.getNumberOfElements());
		Assert.assertEquals(1, personDetailsPage.getTotalElements());
		Assert.assertEquals(1, personDetailsPage.getContent().size());
		Assert.assertEquals(personDetails, personDetailsPage.getContent().get(0));

		/* validate details paging with no match on name filter */
		logger.info("Finding Person Details without Name Match");
		personDetailsPage = crm.findPersonDetails(new PersonsFilter(orgId, newName.getDisplayName() + "00", Status.ACTIVE), new Paging(1, 5, Sort.by(Direction.ASC, "displayName")));
		Assert.assertEquals(1, personDetailsPage.getNumber());
		Assert.assertEquals(0, personDetailsPage.getTotalPages());
		Assert.assertEquals(0, personDetailsPage.getNumberOfElements());
		Assert.assertEquals(0, personDetailsPage.getTotalElements());
		Assert.assertEquals(0, personDetailsPage.getContent().size());

		/* validate summary paging with 1 match on name filter */
		logger.info("Finding Person Summary with Name Match");
		Page<PersonSummary> personSummaryPage = crm.findPersonSummaries(new PersonsFilter(orgId, newName.getDisplayName(), Status.ACTIVE), new Paging(1, 5, Sort.by(Direction.ASC, "displayName")));
		Assert.assertEquals(1, personSummaryPage.getNumber());
		Assert.assertEquals(1, personSummaryPage.getTotalPages());
		Assert.assertEquals(1, personSummaryPage.getNumberOfElements());
		Assert.assertEquals(1, personSummaryPage.getTotalElements());
		Assert.assertEquals(1, personSummaryPage.getContent().size());
		Assert.assertEquals(personSummary, personSummaryPage.getContent().get(0));

		/* validate summary paging with no match on name filter */
		logger.info("Finding Person Summary without Name Match");
		personSummaryPage = crm.findPersonSummaries(new PersonsFilter(orgId, newName.getDisplayName() + "00", Status.ACTIVE), new Paging(1, 5, Sort.by(Direction.ASC, "displayName")));
		Assert.assertEquals(1, personSummaryPage.getNumber());
		Assert.assertEquals(0, personSummaryPage.getTotalPages());
		Assert.assertEquals(0, personSummaryPage.getNumberOfElements());
		Assert.assertEquals(0, personSummaryPage.getTotalElements());
		Assert.assertEquals(0, personSummaryPage.getContent().size());

		return personDetails.getPersonId();
	}

	private void runUserServiceTests(Identifier personId) {
		Assert.assertEquals(0, crm.countUsers(new UsersFilter().withPersonId(personId)));
		
		logger.info("Creating User");
		User user = crm.createUser(personId, "tonka", Collections.emptyList());
		Identifier userId = user.getUserId();
		verifyUser(user, personId, userId, "tonka", Status.ACTIVE, Collections.emptyList());		
		Assert.assertEquals(1, crm.countUsers(new UsersFilter().withPersonId(personId)));

		/* disable and verify the user */
		logger.info("Disabling User");
		user = crm.disableUser(userId);
		verifyUser(user, personId, userId, "tonka", Status.INACTIVE, Collections.emptyList());

		/* enable and verify the user */
		logger.info("enabling User");
		user = crm.enableUser(userId);
		verifyUser(user, personId, userId, "tonka", Status.ACTIVE, Collections.emptyList());

		/*
		 * add user role (first time we add a role it will generate a user name for us)
		 */
		logger.info("Updating User Role");
		Role r1 = crm.findRoleByCode("ORG_ADMIN");
		user = crm.updateUserRoles(user.getUserId(), List.of(r1.getCode()));
		verifyUser(user, personId, userId, "tonka", Status.ACTIVE, Arrays.asList(r1.getCode()));

		user = crm.findUser(user.getUserId());
		verifyUser(user, personId, userId, "tonka", Status.ACTIVE, Arrays.asList(r1.getCode()));

		/* add another role and verify */
		logger.info("Updating User Role");
		Role r2 = crm.findRoleByCode("SYS_ADMIN");
		user = crm.updateUserRoles(user.getUserId(), List.of(r1.getCode(), r2.getCode()));
		verifyUser(user, personId, userId, "tonka", Status.ACTIVE, Arrays.asList(r1.getCode(), r2.getCode()));

		user = crm.findUser(user.getUserId());
		verifyUser(user, personId, userId, "tonka", Status.ACTIVE, Arrays.asList(r1.getCode(), r2.getCode()));

		/* remove a role and verify */
		logger.info("Removing User Role");
		user = crm.updateUserRoles(user.getUserId(), List.of(r2.getCode()));
		verifyUser(user, personId, userId, "tonka", Status.ACTIVE, Arrays.asList(r2.getCode()));
		user = crm.findUser(user.getUserId());
		verifyUser(user, personId, userId, "tonka", Status.ACTIVE, Arrays.asList(r2.getCode()));

		/* set roles and verify */
		logger.info("Setting User Roles");
		user = crm.updateUserRoles(user.getUserId(), Arrays.asList(r1.getCode(), r2.getCode()));
		verifyUser(user, personId, userId, "tonka", Status.ACTIVE, Arrays.asList(r1.getCode(), r2.getCode()));
		
		/* reset password */
		String tempPassword = crm.resetPassword(user.getUserId());
		Assert.assertNotNull(tempPassword);
		
		/* change password using temp password */
		Boolean success = crm.changePassword(user.getUserId(), tempPassword, "SonyPlaystation");
		Assert.assertTrue(success);
		
		success = crm.changePassword(user.getUserId(), tempPassword, "SonyPlaystation");
		Assert.assertFalse(success);
		
		
		/* find and verify user by id */
		user = crm.findUser(user.getUserId());
		verifyUser(user, personId, userId, "tonka", Status.ACTIVE, Arrays.asList(r1.getCode(), r2.getCode()));
		
		/* find and verify user by username */
		user = crm.findUserByUsername(user.getUsername());
		verifyUser(user, personId, userId, "tonka", Status.ACTIVE, Arrays.asList(r1.getCode(), r2.getCode()));

		/* find users */
		Page<User> userPage = crm.findUsers(new UsersFilter().withOrganizationId(user.getPerson().getOrganizationId()));
		Assert.assertEquals(1, userPage.getNumber());
		Assert.assertEquals(1, userPage.getTotalPages());
		Assert.assertEquals(1, userPage.getNumberOfElements());
		Assert.assertEquals(1, userPage.getTotalElements());
		Assert.assertEquals(1, userPage.getContent().size());
	}

	private void verifyGroupDetails(Group group, Identifier groupId, Status status, String code, String englishName, String frenchName) {
		Assert.assertNotNull(group.getGroupId());
		Assert.assertEquals(groupId, group.getGroupId());
		Assert.assertEquals(status, group.getStatus());
		Assert.assertEquals(code, group.getCode());
		Assert.assertEquals(englishName, group.getName().getEnglishName());
		Assert.assertEquals(frenchName, group.getName().getFrenchName());
		logger.info("Verifying Group Details " + groupId + " Passed");
	}

	private void verifyRoleDetails(Role role, Identifier groupId, Identifier roleId, Status status, String code, String englishName, String frenchName) {
		Assert.assertNotNull(role.getRoleId());
		Assert.assertEquals(groupId, role.getGroupId());
		Assert.assertEquals(roleId, role.getRoleId());
		Assert.assertEquals(status, role.getStatus());
		Assert.assertEquals(code, role.getCode());
		Assert.assertEquals(englishName, role.getName().getEnglishName());
		Assert.assertEquals(frenchName, role.getName().getFrenchName());
		logger.info("Verifying Role Details " + roleId + " Passed");
	}

	private void verifyOrgDetails(OrganizationDetails orgDetails, Identifier orgId, Status status, String displayName, Identifier mainLocationIdentifier, Identifier mainContactIdentifier, List<String> groups) {
		Assert.assertNotNull(orgDetails.getOrganizationId());
		Assert.assertEquals(orgId, orgDetails.getOrganizationId());
		Assert.assertEquals(status, orgDetails.getStatus());
		Assert.assertEquals(displayName, orgDetails.getDisplayName());
		Assert.assertEquals(mainLocationIdentifier, orgDetails.getMainLocationId());
		Assert.assertEquals(mainContactIdentifier, orgDetails.getMainContactId());
		Assert.assertTrue(groups.size() == orgDetails.getGroups().size() && groups.containsAll(orgDetails.getGroups()) && orgDetails.getGroups().containsAll(groups));
		logger.info("Verifying Organization Details " + orgId + " Passed");
	}

	private void verifyOrgSummary(OrganizationSummary orgSummary, Identifier orgId, Status status, String displayName) {
		Assert.assertNotNull(orgSummary.getOrganizationId());
		Assert.assertEquals(orgId, orgSummary.getOrganizationId());
		Assert.assertEquals(status, orgSummary.getStatus());
		Assert.assertEquals(displayName, orgSummary.getDisplayName());
		logger.info("Verifying Organization Summary " + orgId + " Passed");
	}

	private void verifyLocationDetails(LocationDetails locDetails, Identifier orgId, Identifier locId, Status status, String reference, String displayName, MailingAddress address) {
		Assert.assertNotNull(locDetails.getLocationId());
		Assert.assertEquals(orgId, locDetails.getOrganizationId());
		Assert.assertEquals(locId, locDetails.getLocationId());
		Assert.assertEquals(reference, locDetails.getReference());
		Assert.assertEquals(displayName, locDetails.getDisplayName());
		Assert.assertEquals(address, locDetails.getAddress());
		Assert.assertEquals(status, locDetails.getStatus());
		logger.info("Verifying Location Details " + locId + " Passed");
	}

	private void verifyLocationSummary(LocationSummary locSummary, Identifier orgId, Identifier locId, Status status, String reference, String displayName) {
		Assert.assertNotNull(locSummary.getLocationId());
		Assert.assertEquals(orgId, locSummary.getOrganizationId());
		Assert.assertEquals(locId, locSummary.getLocationId());
		Assert.assertEquals(reference, locSummary.getReference());
		Assert.assertEquals(displayName, locSummary.getDisplayName());
		Assert.assertEquals(status, locSummary.getStatus());
		logger.info("Verifying Location Summary " + locId + " Passed");
	}

	private void verifyPersonDetails(PersonDetails personDetails, Identifier orgId, Identifier personId, Status status, String displayName, PersonName legalName, MailingAddress address, Communication communication, BusinessPosition position) {
		Assert.assertNotNull(personDetails.getPersonId());
		Assert.assertEquals(orgId, personDetails.getOrganizationId());
		Assert.assertEquals(personId, personDetails.getPersonId());
		Assert.assertEquals(status, personDetails.getStatus());
		Assert.assertEquals(displayName, personDetails.getDisplayName());
		Assert.assertEquals(legalName, personDetails.getLegalName());
		Assert.assertEquals(address, personDetails.getAddress());
		Assert.assertEquals(communication, personDetails.getCommunication());
		Assert.assertEquals(position, personDetails.getPosition());
		logger.info("Verifying Person Details " + personId + " Passed");
	}

	private void verifyPersonSummary(PersonSummary personSummary, Identifier orgId, Identifier personId, Status status, String displayName) {
		Assert.assertNotNull(personSummary.getPersonId());
		Assert.assertEquals(orgId, personSummary.getOrganizationId());
		Assert.assertEquals(personId, personSummary.getPersonId());
		Assert.assertEquals(status, personSummary.getStatus());
		Assert.assertEquals(displayName, personSummary.getDisplayName());
		logger.info("Verifying Person Summary " + personId + " Passed");
	}

	private void verifyUser(User user, Identifier personId, Identifier userId, String username, Status status, List<String> roles) {
		Assert.assertNotNull(user.getUserId());
		Assert.assertEquals(personId, user.getPerson().getPersonId());
		Assert.assertEquals(userId, user.getUserId());
		Assert.assertEquals(username, user.getUsername());
		Assert.assertEquals(status, user.getStatus());
		Assert.assertTrue("user roles: " + user.getRoles() + ", expected roles: " + roles,
				user.getRoles().size() == roles.size() && user.getRoles().containsAll(roles) && roles.containsAll(user.getRoles()));
		logger.info("Verifying User " + userId + " Passed");
	}
}
