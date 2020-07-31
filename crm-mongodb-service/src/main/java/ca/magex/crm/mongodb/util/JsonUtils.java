package ca.magex.crm.mongodb.util;

import java.util.stream.Collectors;

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
import ca.magex.crm.api.system.Localized;
import ca.magex.crm.api.system.Option;
import ca.magex.crm.api.system.Status;
import ca.magex.crm.api.system.Type;
import ca.magex.crm.api.system.id.AuthenticationGroupIdentifier;
import ca.magex.crm.api.system.id.AuthenticationRoleIdentifier;
import ca.magex.crm.api.system.id.BusinessGroupIdentifier;
import ca.magex.crm.api.system.id.BusinessRoleIdentifier;
import ca.magex.crm.api.system.id.CountryIdentifier;
import ca.magex.crm.api.system.id.IdentifierFactory;
import ca.magex.crm.api.system.id.LanguageIdentifier;
import ca.magex.crm.api.system.id.LocationIdentifier;
import ca.magex.crm.api.system.id.OrganizationIdentifier;
import ca.magex.crm.api.system.id.PersonIdentifier;
import ca.magex.crm.api.system.id.ProvinceIdentifier;
import ca.magex.crm.api.system.id.SalutationIdentifier;
import ca.magex.crm.api.system.id.UserIdentifier;
import ca.magex.json.model.JsonObject;

public class JsonUtils {

	/**
	 * Converts the json response back to an Option for persistence
	 * @param option
	 * @param type
	 * @return
	 */
	public static Option toOption(JsonObject option, Type type) {
		return new Option(
				type.generateId(option.getString("optionId")),
				option.contains("parentId") ? IdentifierFactory.forOptionId(option.getString("parentId")) : null,
				type,
				Status.of(option.getString("status")),
				option.getBoolean("mutable"),
				new Localized(
						option.getObject("name").getString("code"),
						option.getObject("name").getString("english"),
						option.getObject("name").getString("french")));
	}
	
	/**
	 * converts to a summary
	 * @param json
	 * @return
	 */
	public static OrganizationSummary toOrganizationSummary(JsonObject json) {
		return new OrganizationSummary(
				new OrganizationIdentifier(json.getString("organizationId")),
				Status.of(json.getString("status")),
				json.getString("displayName"));
	}
	
	/**
	 * converts to a details
	 * @param json
	 * @return
	 */
	public static OrganizationDetails toOrganizationDetails(JsonObject json) {
		return new OrganizationDetails(
				new OrganizationIdentifier(json.getString("organizationId")),
				Status.of(json.getString("status")),
				json.getString("displayName"),
				IdentifierFactory.forId(json.getString("mainLocationId")),
				IdentifierFactory.forId(json.getString("mainContactId")),
				json.getArray("authenticationGroupIds", String.class).stream().map(AuthenticationGroupIdentifier::new).collect(Collectors.toList()),
				json.getArray("businessGroupIds", String.class).stream().map(BusinessGroupIdentifier::new).collect(Collectors.toList()));
	}
	
	/**
	 * converts to a summary
	 * @param json
	 * @return
	 */
	public static LocationSummary toLocationSummary(JsonObject json, OrganizationIdentifier organizationId) {
		return new LocationSummary(
				new LocationIdentifier(json.getString("locationId")),
				organizationId,
				Status.of(json.getString("status")),
				json.getString("reference"),
				json.getString("displayName"));
	}
	
	/**
	 * converts to a summary
	 * @param json
	 * @return
	 */
	public static LocationDetails toLocationDetails(JsonObject json, OrganizationIdentifier organizationId) {
		return new LocationDetails(
				new LocationIdentifier(json.getString("locationId")),
				organizationId,
				Status.of(json.getString("status")),
				json.getString("reference"),
				json.getString("displayName"),
				toMailingAddress(json.getObject("address")));
	}
	
	/**
	 * converts to a person details
	 * @param json
	 * @param organizationId
	 * @return
	 */
	public static PersonSummary toPersonSummary(JsonObject json, OrganizationIdentifier organizationId) {
		return new PersonSummary(
				new PersonIdentifier(json.getString("personId")),
				organizationId,
				Status.of(json.getString("status")),
				json.getString("displayName"));
	}
	
	/**
	 * converts to a person details
	 * @param json
	 * @param organizationId
	 * @return
	 */
	public static PersonDetails toPersonDetails(JsonObject json, OrganizationIdentifier organizationId) {
		return new PersonDetails(
				new PersonIdentifier(json.getString("personId")),
				organizationId,
				Status.of(json.getString("status")),
				json.getString("displayName"),
				toPersonName(json.getObject("legalName")),
				toMailingAddress(json.getObject("address")),
				toCommunication(json.getObject("communication")),
				json.getArray("businessRoleIds", String.class).stream().map(BusinessRoleIdentifier::new).collect(Collectors.toList()));
	}
	
	/**
	 * converts to a user details
	 * @param json
	 * @param organizationId
	 * @return
	 */
	public static UserSummary toUserSummary(JsonObject json, OrganizationIdentifier organizationId) {
		return new UserSummary(
				new UserIdentifier("userId"), 
				organizationId, 
				json.getString("userName"), 
				Status.of(json.getString("status")));
				
	}
	
	/**
	 * converts to a user details
	 * @param json
	 * @param organizationId
	 * @return
	 */
	public static UserDetails toUserDetails(JsonObject json, OrganizationIdentifier organizationId) {
		return new UserDetails(
				new UserIdentifier(json.getString("userId")), 
				organizationId, 
				new PersonIdentifier(json.getString("personId")), 
				json.getString("username"), 
				Status.of(json.getString("status")), 
				json.getArray("authenticationRoleIds", String.class).stream().map(AuthenticationRoleIdentifier::new).collect(Collectors.toList()));
				
	}
	
	/**
	 * converts the json to a person name
	 * @param json
	 * @return
	 */
	public static PersonName toPersonName(JsonObject json) {
		return new PersonName(
				toSalutation(json.getObject("salutation", null)), 
				json.getString("firstName"),
				json.getString("middleName", null),
				json.getString("surname"));
	}
	
	/**
	 * converts the json to a Mailing Address
	 * @param json
	 * @return
	 */
	public static MailingAddress toMailingAddress(JsonObject json) {
		if (json == null) {
			return null;
		}
		return new MailingAddress(
				json.getString("street"),
				json.getString("city"),
				toProvince(json.getObject("province")),
				toCountry(json.getObject("country")),
				json.getString("postalCode"));
	}
	
	/**
	 * converts the json to a Communication
	 * @param json
	 * @return
	 */
	public static Communication toCommunication(JsonObject json) {
		if (json == null) {
			return null;
		}
		return new Communication(
				json.getString("jobTitle", null),
				toLanguage(json.getObject("language")),
				json.getString("email"),
				toTelephone(json.getObject("homePhone", null)),
				json.getString("faxNumber", null));
	}
	
	/**
	 * converts the json to a Telephone
	 * @param json
	 * @return
	 */
	public static Telephone toTelephone(JsonObject json) {
		if (json == null) {
			return null;
		}
		return new Telephone(
				json.getString("number", null),
				json.getString("extension", null));
	}
	
	/**
	 * converts the json to a salutation choice
	 * @param json
	 * @return
	 */
	public static Choice<SalutationIdentifier> toSalutation(JsonObject json) {
		if (json == null) {
			return null;
		}
		if (json.contains("identifier")) {
			return new Choice<>(new SalutationIdentifier(json.getString("identifier")));
		}
		else if (json.contains("other")) {
			return new Choice<>(json.getString("other"));
		}
		else {
			return new Choice<>();
		}
	}
	
	/**
	 * converts the json to a salutation choice
	 * @param json
	 * @return
	 */
	public static Choice<LanguageIdentifier> toLanguage(JsonObject json) {
		if (json == null) {
			return null;
		}
		if (json.contains("identifier")) {
			return new Choice<>(new LanguageIdentifier(json.getString("identifier")));
		}
		else if (json.contains("other")) {
			return new Choice<>(json.getString("other"));
		}
		else {
			return new Choice<>();
		}
	}
	
	/**
	 * converts the json to a province choice
	 * @param json
	 * @return
	 */
	public static Choice<ProvinceIdentifier> toProvince(JsonObject json) {
		if (json == null) {
			return null;
		}
		if (json.contains("identifier")) {
			return new Choice<>(new ProvinceIdentifier(json.getString("identifier")));
		}
		else if (json.contains("other")) {
			return new Choice<>(json.getString("other"));
		}
		else {
			return new Choice<>();
		}
	}
	
	/**
	 * converts the json to a country choice
	 * @param json
	 * @return
	 */
	public static Choice<CountryIdentifier> toCountry(JsonObject json) {
		if (json == null) {
			return null;
		}
		if (json.contains("identifier")) {
			return new Choice<>(new CountryIdentifier(json.getString("identifier")));
		}
		else if (json.contains("other")) {
			return new Choice<>(json.getString("other"));
		}
		else {
			return new Choice<>();
		}
	}
}
