package ca.magex.crm.graphql.client;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.boot.configurationprocessor.json.JSONException;

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
import ca.magex.crm.api.crm.UserDetails;
import ca.magex.crm.api.crm.UserSummary;
import ca.magex.crm.api.system.Choice;
import ca.magex.crm.api.system.Option;
import ca.magex.crm.api.system.Status;
import ca.magex.crm.api.system.Type;
import ca.magex.crm.api.system.id.AuthenticationGroupIdentifier;
import ca.magex.crm.api.system.id.BusinessGroupIdentifier;
import ca.magex.crm.api.system.id.CountryIdentifier;
import ca.magex.crm.api.system.id.LocationIdentifier;
import ca.magex.crm.api.system.id.OrganizationIdentifier;
import ca.magex.crm.api.system.id.PersonIdentifier;
import ca.magex.crm.api.system.id.ProvinceIdentifier;
import ca.magex.crm.api.system.id.UserIdentifier;
import ca.magex.json.model.JsonArray;
import ca.magex.json.model.JsonObject;
import ca.magex.json.model.JsonPair;

public class ModelBinderTests {

	@Test
	public void testBindLong() {
		Assert.assertEquals(5l, ModelBinder.toLong(5.0f));
		Assert.assertEquals(5l, ModelBinder.toLong(5.0));
		Assert.assertEquals(5l, ModelBinder.toLong(5));
		Assert.assertEquals(5l, ModelBinder.toLong(5l));
	}
	
	@Test
	public void testBindIdentifierList() {
		List<AuthenticationGroupIdentifier> authGroups = ModelBinder.toIdentifierList(
				AuthenticationGroupIdentifier::new, 
				new JsonArray(List.of(
						new JsonObject(new JsonPair("optionId", "/options/authentication-groups/A")),
						new JsonObject(new JsonPair("optionId", "/options/authentication-groups/B")),
						new JsonObject(new JsonPair("optionId", "/options/authentication-groups/C")))
				),
				"optionId");
		
		Assert.assertEquals(3, authGroups.size());		
		Assert.assertEquals("A", authGroups.get(0).getCode());
		Assert.assertEquals("B", authGroups.get(1).getCode());
		Assert.assertEquals("C", authGroups.get(2).getCode());
	}

	@Test
	public void testBindChoice() {
		Choice<ProvinceIdentifier> province = ModelBinder.toChoice(
				new JsonObject(
						new JsonPair("identifier", "/options/provinces/CA/ON")));
		Assert.assertEquals("CA/ON", province.getValue());

		province = ModelBinder.toChoice(
				new JsonObject(
						new JsonPair("other", "johnnuyland")));
		Assert.assertEquals("johnnuyland", province.getValue());

		province = ModelBinder.toChoice(
				new JsonObject());
		Assert.assertNull(province.getValue());
	}

	@Test
	public void testBindMailingAddress() {
		MailingAddress address = ModelBinder.toMailingAddress(
				new JsonObject(
						new JsonPair("street", "123 Main Street"),
						new JsonPair("city", "Ottawa"),
						new JsonPair("province", new JsonObject(new JsonPair("identifier", "/options/provinces/CA/ON"))),
						new JsonPair("country", new JsonObject(new JsonPair("identifier", "/options/countries/CA"))),
						new JsonPair("postalCode", "K1N6J3")));

		Assert.assertEquals("123 Main Street", address.getStreet());
		Assert.assertEquals("Ottawa", address.getCity());
		Assert.assertEquals("CA/ON", address.getProvince().getValue());
		Assert.assertEquals("CA", address.getCountry().getValue());
		Assert.assertEquals("K1N6J3", address.getPostalCode());
	}
	
	@Test
	public void testBindPersonName() {
		PersonName name = ModelBinder.toPersonName(
				new JsonObject(
						new JsonPair("firstName", "Mike"),
						new JsonPair("middleName", ""),
						new JsonPair("lastName", "Tyson"),
						new JsonPair("salutation", new JsonObject(new JsonPair("other", "Doctor")))));
		
		Assert.assertEquals("Mike", name.getFirstName());
		Assert.assertEquals("", name.getMiddleName());
		Assert.assertEquals("Tyson", name.getLastName());
		Assert.assertEquals("Doctor", name.getSalutation().getValue());
	}
	
	@Test
	public void testBindTelephone() {
		Telephone telephone = ModelBinder.toTelephone(
				new JsonObject(
						new JsonPair("number", "613-555-3434"),
						new JsonPair("extension", "007")));
		
		Assert.assertEquals("613-555-3434", telephone.getNumber());
		Assert.assertEquals("007", telephone.getExtension());
	}
	
	@Test
	public void testBindCommunication() {
		Communication communication = ModelBinder.toCommunication(
				new JsonObject(
						new JsonPair("jobTitle", "Head Honcho"),
						new JsonPair("language", new JsonObject(
								new JsonPair("identifier", "/options/languages/EN"))),
						new JsonPair("email", "head@honcho.org"),
						new JsonPair("homePhone", new JsonObject(
								new JsonPair("number", "613-555-3434"),
								new JsonPair("extension", "007"))),
						new JsonPair("faxNumber", "613-555-9999")));
						
		Assert.assertEquals("Head Honcho", communication.getJobTitle());
		Assert.assertEquals("EN", communication.getLanguage().getValue());
		Assert.assertEquals("head@honcho.org", communication.getEmail());
		Assert.assertEquals("613-555-3434", communication.getHomePhone().getNumber());
		Assert.assertEquals("007", communication.getHomePhone().getExtension());
		Assert.assertEquals("613-555-9999", communication.getFaxNumber());
	}

	@Test
	public void testBindOrganizationSummary() {
		JsonObject json = new JsonObject(
				new JsonPair("organizationId", "/organizations/Org1"),
				new JsonPair("status", "active"),
				new JsonPair("displayName", "My New Org"));

		OrganizationSummary summary = ModelBinder.toOrganizationSummary(json);

		Assert.assertEquals(new OrganizationIdentifier("Org1"), summary.getOrganizationId());
		Assert.assertEquals(Status.ACTIVE, summary.getStatus());
		Assert.assertEquals("My New Org", summary.getDisplayName());
	}

	@Test
	public void testBindOrganizationDetails() throws JSONException {
		/* null location/contact */
		JsonObject json = new JsonObject(
				new JsonPair("organizationId", "/organizations/Org1"),
				new JsonPair("status", "active"),
				new JsonPair("displayName", "My New Org"),
				new JsonPair("authenticationGroups", new JsonArray()),
				new JsonPair("businessGroups", new JsonArray()));

		OrganizationDetails details = ModelBinder.toOrganizationDetails(json);

		Assert.assertEquals(new OrganizationIdentifier("Org1"), details.getOrganizationId());
		Assert.assertEquals(Status.ACTIVE, details.getStatus());
		Assert.assertEquals("My New Org", details.getDisplayName());
		Assert.assertNull(details.getMainLocationId());
		Assert.assertNull(details.getMainContactId());
		Assert.assertEquals(0, details.getAuthenticationGroupIds().size());
		Assert.assertEquals(0, details.getBusinessGroupIds().size());

		JsonObject mainLocation = new JsonObject(
				new JsonPair("locationId", "/locations/Loc1"));

		JsonObject mainContact = new JsonObject(
				new JsonPair("personId", "/persons/Per1"));

		JsonObject authGroup1 = new JsonObject(
				new JsonPair("optionId", "/options/authentication-groups/Auth1"));

		JsonObject authGroup2 = new JsonObject(
				new JsonPair("optionId", "/options/authentication-groups/Auth2"));

		JsonObject busGroup1 = new JsonObject(
				new JsonPair("optionId", "/options/business-groups/Bus1"));

		JsonObject busGroup2 = new JsonObject(
				new JsonPair("optionId", "/options/business-groups/Bus2"));

		json = new JsonObject(
				new JsonPair("organizationId", "/organizations/Org1"),
				new JsonPair("status", "active"),
				new JsonPair("displayName", "My New Org"),
				new JsonPair("mainLocation", mainLocation),
				new JsonPair("mainContact", mainContact),
				new JsonPair("authenticationGroups", new JsonArray(List.of(authGroup1, authGroup2))),
				new JsonPair("businessGroups", new JsonArray(List.of(busGroup1, busGroup2))));

		details = ModelBinder.toOrganizationDetails(json);

		Assert.assertEquals(new OrganizationIdentifier("Org1"), details.getOrganizationId());
		Assert.assertEquals(Status.ACTIVE, details.getStatus());
		Assert.assertEquals("My New Org", details.getDisplayName());
		Assert.assertEquals(new LocationIdentifier("Loc1"), details.getMainLocationId());
		Assert.assertEquals(new PersonIdentifier("Per1"), details.getMainContactId());
		Assert.assertEquals(2, details.getAuthenticationGroupIds().size());
		Assert.assertEquals(new AuthenticationGroupIdentifier("Auth1"), details.getAuthenticationGroupIds().get(0));
		Assert.assertEquals(new AuthenticationGroupIdentifier("Auth2"), details.getAuthenticationGroupIds().get(1));
		Assert.assertEquals(2, details.getBusinessGroupIds().size());
		Assert.assertEquals(new BusinessGroupIdentifier("Bus1"), details.getBusinessGroupIds().get(0));
		Assert.assertEquals(new BusinessGroupIdentifier("Bus2"), details.getBusinessGroupIds().get(1));
	}

	@Test
	public void testBindLocationSummary() throws JSONException {
		JsonObject json = new JsonObject(
				new JsonPair("locationId", "/locations/Loc1"),
				new JsonPair("organizationId", "/organizations/Org1"),
				new JsonPair("reference", "L1"),
				new JsonPair("status", "active"),
				new JsonPair("displayName", "My New Loc"));

		LocationSummary summary = ModelBinder.toLocationSummary(json);

		Assert.assertEquals(new LocationIdentifier("Loc1"), summary.getLocationId());
		Assert.assertEquals(new OrganizationIdentifier("Org1"), summary.getOrganizationId());
		Assert.assertEquals(Status.ACTIVE, summary.getStatus());
		Assert.assertEquals("My New Loc", summary.getDisplayName());
		Assert.assertEquals("L1", summary.getReference());
	}

	@Test
	public void testBindLocationDetails() throws JSONException {
		JsonObject json = new JsonObject(
				new JsonPair("locationId", "/locations/Loc1"),
				new JsonPair("organizationId", "/organizations/Org1"),
				new JsonPair("reference", "L1"),
				new JsonPair("status", "active"),
				new JsonPair("displayName", "My New Loc"),
				new JsonPair("address", new JsonObject(
						new JsonPair("street", "123 Main Street"),
						new JsonPair("city", "Ottawa"),
						new JsonPair("province", new JsonObject(new JsonPair("identifier", "/options/provinces/CA/ON"))),
						new JsonPair("country", new JsonObject(new JsonPair("identifier", "/options/countries/CA"))),
						new JsonPair("postalCode", "K1N6J3"))));

		LocationDetails details = ModelBinder.toLocationDetails(json);
		Assert.assertEquals(new LocationIdentifier("Loc1"), details.getLocationId());
		Assert.assertEquals(new OrganizationIdentifier("Org1"), details.getOrganizationId());
		Assert.assertEquals(Status.ACTIVE, details.getStatus());
		Assert.assertEquals("My New Loc", details.getDisplayName());
		Assert.assertEquals("L1", details.getReference());

		Assert.assertEquals("123 Main Street", details.getAddress().getStreet());
		Assert.assertEquals("Ottawa", details.getAddress().getCity());
		Assert.assertEquals("CA/ON", details.getAddress().getProvince().getValue());
		Assert.assertEquals("CA", details.getAddress().getCountry().getValue());
		Assert.assertEquals("K1N6J3", details.getAddress().getPostalCode());
	}
	
	@Test
	public void testBindPersonSummary() {
		JsonObject json = new JsonObject(
				new JsonPair("personId", "/persons/Per1"),
				new JsonPair("organizationId", "/organizations/Org1"),
				new JsonPair("status", "active"),
				new JsonPair("displayName", "Michael"));
		
		PersonSummary summary = ModelBinder.toPersonSummary(json);
		Assert.assertEquals(new PersonIdentifier("Per1"), summary.getPersonId());
		Assert.assertEquals(new OrganizationIdentifier("Org1"), summary.getOrganizationId());
		Assert.assertEquals(Status.ACTIVE, summary.getStatus());
		Assert.assertEquals("Michael", summary.getDisplayName());
	}
	
	@Test
	public void testBindPersonDetails() {
		JsonObject json = new JsonObject(
				new JsonPair("personId", "/persons/Per1"),
				new JsonPair("organizationId", "/organizations/Org1"),
				new JsonPair("status", "active"),
				new JsonPair("displayName", "Michael"),
				new JsonPair("legalName", new JsonObject(
						new JsonPair("firstName", "Mike"),
						new JsonPair("middleName", ""),
						new JsonPair("lastName", "Tyson"),
						new JsonPair("salutation", new JsonObject(new JsonPair("other", "Doctor"))))),
				new JsonPair("address", new JsonObject(
						new JsonPair("street", "123 Main Street"),
						new JsonPair("city", "Ottawa"),
						new JsonPair("province", new JsonObject(new JsonPair("identifier", "/options/provinces/CA/ON"))),
						new JsonPair("country", new JsonObject(new JsonPair("identifier", "/options/countries/CA"))),
						new JsonPair("postalCode", "K1N6J3"))),
				new JsonPair("communication", new JsonObject(
						new JsonPair("jobTitle", "Head Honcho"),
						new JsonPair("language", new JsonObject(
								new JsonPair("identifier", "/options/languages/EN"))),
						new JsonPair("email", "head@honcho.org"),
						new JsonPair("homePhone", new JsonObject(
								new JsonPair("number", "613-555-3434"),
								new JsonPair("extension", "007"))),
						new JsonPair("faxNumber", "613-555-9999"))),
				new JsonPair("businessRoles", new JsonArray(List.of(
						new JsonObject(new JsonPair("optionId", "/options/business-roles/A")),
						new JsonObject(new JsonPair("optionId", "/options/business-roles/B")),
						new JsonObject(new JsonPair("optionId", "/options/business-roles/C"))))));
		
		PersonDetails details = ModelBinder.toPersonDetails(json);
		Assert.assertEquals(new PersonIdentifier("Per1"), details.getPersonId());
		Assert.assertEquals(new OrganizationIdentifier("Org1"), details.getOrganizationId());
		Assert.assertEquals(Status.ACTIVE, details.getStatus());
		Assert.assertEquals("Michael", details.getDisplayName());
		
		Assert.assertEquals("Mike", details.getLegalName().getFirstName());
		Assert.assertEquals("", details.getLegalName().getMiddleName());
		Assert.assertEquals("Tyson", details.getLegalName().getLastName());
		Assert.assertEquals("Doctor", details.getLegalName().getSalutation().getValue());
		
		Assert.assertEquals("123 Main Street", details.getAddress().getStreet());
		Assert.assertEquals("Ottawa", details.getAddress().getCity());
		Assert.assertEquals("CA/ON", details.getAddress().getProvince().getValue());
		Assert.assertEquals("CA", details.getAddress().getCountry().getValue());
		Assert.assertEquals("K1N6J3", details.getAddress().getPostalCode());
		
		Assert.assertEquals("Head Honcho", details.getCommunication().getJobTitle());
		Assert.assertEquals("EN", details.getCommunication().getLanguage().getValue());
		Assert.assertEquals("head@honcho.org", details.getCommunication().getEmail());
		Assert.assertEquals("613-555-3434", details.getCommunication().getHomePhone().getNumber());
		Assert.assertEquals("007", details.getCommunication().getHomePhone().getExtension());
		Assert.assertEquals("613-555-9999", details.getCommunication().getFaxNumber());
		
		Assert.assertEquals(3, details.getBusinessRoleIds().size());		
		Assert.assertEquals("A", details.getBusinessRoleIds().get(0).getCode());
		Assert.assertEquals("B", details.getBusinessRoleIds().get(1).getCode());
		Assert.assertEquals("C", details.getBusinessRoleIds().get(2).getCode());
	}
	
	@Test
	public void testBindUserSummary() {
		JsonObject json = new JsonObject(
				new JsonPair("userId", "/users/User1"),
				new JsonPair("organization", 
						new JsonObject(
								new JsonPair("organizationId", "/organizations/Org1"))),
				new JsonPair("status", "active"),
				new JsonPair("username", "User1"));
		
		UserSummary summary = ModelBinder.toUserSummary(json);
		Assert.assertEquals(new UserIdentifier("User1"), summary.getUserId());
		Assert.assertEquals(new OrganizationIdentifier("Org1"), summary.getOrganizationId());
		Assert.assertEquals(Status.ACTIVE, summary.getStatus());
		Assert.assertEquals("User1", summary.getUsername());
	}
	
	@Test
	public void testBindUserDetails() {
		JsonObject json = new JsonObject(
				new JsonPair("userId", "/users/User1"),
				new JsonPair("organization", 
						new JsonObject(
								new JsonPair("organizationId", "/organizations/Org1"))),
				new JsonPair("person", 
						new JsonObject(
								new JsonPair("personId", "/persons/Per1"))),
				new JsonPair("status", "active"),
				new JsonPair("username", "User1"),
				new JsonPair("authenticationRoles", new JsonArray(List.of(
						new JsonObject(new JsonPair("optionId", "/options/authentication-roles/A")),
						new JsonObject(new JsonPair("optionId", "/options/authentication-roles/B")),
						new JsonObject(new JsonPair("optionId", "/options/authentication-roles/C"))))));
		
		UserDetails details = ModelBinder.toUserDetails(json);
		Assert.assertEquals(new UserIdentifier("User1"), details.getUserId());
		Assert.assertEquals(new OrganizationIdentifier("Org1"), details.getOrganizationId());
		Assert.assertEquals(new PersonIdentifier("Per1"), details.getPersonId());
		Assert.assertEquals(Status.ACTIVE, details.getStatus());
		Assert.assertEquals("User1", details.getUsername());
		Assert.assertEquals(3, details.getAuthenticationRoleIds().size());		
		Assert.assertEquals("A", details.getAuthenticationRoleIds().get(0).getCode());
		Assert.assertEquals("B", details.getAuthenticationRoleIds().get(1).getCode());
		Assert.assertEquals("C", details.getAuthenticationRoleIds().get(2).getCode());
	}
	
	@Test
	public void testBindOption() {
		JsonObject json = new JsonObject(
				new JsonPair("optionId", "/options/provinces/CA/ON"),
				new JsonPair("parent", new JsonObject(
						new JsonPair("optionId", "/options/countries/CA"))),
				new JsonPair("type", Type.PROVINCE.getCode()),
				new JsonPair("status", Status.ACTIVE.name()),
				new JsonPair("mutable", Boolean.toString(Option.IMMUTABLE)),
				new JsonPair("name", new JsonObject(
						new JsonPair("code", "CA/ON"),
						new JsonPair("english", "Ontario"),
						new JsonPair("french",  "'ntario"))));
		
		Option option = ModelBinder.toOption(json);
		Assert.assertEquals(new ProvinceIdentifier("CA/ON"), option.getOptionId());
		Assert.assertEquals(new CountryIdentifier("CA"), option.getParentId());
		Assert.assertEquals(Type.PROVINCE, option.getType());
		Assert.assertEquals(Status.ACTIVE, option.getStatus());
		Assert.assertEquals(Option.IMMUTABLE, option.getMutable());
		Assert.assertEquals("CA/ON", option.getName().getCode());
		Assert.assertEquals("Ontario", option.getName().getEnglishName());
		Assert.assertEquals("'ntario", option.getName().getFrenchName());
	}
}
