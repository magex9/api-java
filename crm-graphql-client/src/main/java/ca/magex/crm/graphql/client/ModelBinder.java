package ca.magex.crm.graphql.client;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Function;

import org.apache.commons.lang3.tuple.Pair;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.domain.Sort.Order;

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
import ca.magex.crm.api.filters.Paging;
import ca.magex.crm.api.system.Choice;
import ca.magex.crm.api.system.FilteredPage;
import ca.magex.crm.api.system.Identifier;
import ca.magex.crm.api.system.Localized;
import ca.magex.crm.api.system.Option;
import ca.magex.crm.api.system.Status;
import ca.magex.crm.api.system.Type;
import ca.magex.crm.api.system.id.AuthenticationGroupIdentifier;
import ca.magex.crm.api.system.id.AuthenticationRoleIdentifier;
import ca.magex.crm.api.system.id.BusinessGroupIdentifier;
import ca.magex.crm.api.system.id.BusinessRoleIdentifier;
import ca.magex.crm.api.system.id.IdentifierFactory;
import ca.magex.crm.api.system.id.LocationIdentifier;
import ca.magex.crm.api.system.id.OptionIdentifier;
import ca.magex.crm.api.system.id.OrganizationIdentifier;
import ca.magex.crm.api.system.id.PersonIdentifier;
import ca.magex.crm.api.system.id.UserIdentifier;
import ca.magex.json.model.JsonArray;
import ca.magex.json.model.JsonNumber;
import ca.magex.json.model.JsonObject;

/**
 * Binds the JSON Responses back to the CRM Model Objects
 * 
 * @author Jonny
 *
 */
public class ModelBinder {

	/**
	 * Binds a generic number element to a long value
	 * 
	 * @param number
	 * @return
	 */
	public static long toLong(JsonNumber number) {
		return number.value().longValue();
	}

	/**
	 * Binds the given jsonArray to an Identifier List using the provided constructor and id field
	 * @param <I>
	 * @param constructor
	 * @param jsonArray
	 * @param idField
	 * @return
	 */
	public static <I extends Identifier> List<I> toIdentifierList(Function<String, I> constructor, JsonArray jsonArray, String idField) {
		List<I> list = new ArrayList<>();
		for (int i = 0; i < jsonArray.size(); i++) {
			JsonObject object = (JsonObject) jsonArray.get(i);
			list.add(constructor.apply(object.getString(idField)));
		}
		return list;
	}

	/**
	 * Binds the given json object to a Choice model
	 * @param <I>
	 * @param json
	 * @return
	 */
	public static <I extends OptionIdentifier> Choice<I> toChoice(JsonObject json) {
		if (json.contains("identifier")) {
			return new Choice<>(IdentifierFactory.forOptionId(json.getString("identifier")));
		} else if (json.contains("other")) {
			return new Choice<>(json.getString("other"));
		} else {
			return new Choice<>((String) null);
		}
	}

	/**
	 * Binds the given json object to a MailingAddress model
	 * @param json
	 * @return
	 */
	public static MailingAddress toMailingAddress(JsonObject json) {
		return new MailingAddress(
				json.getString("street"),
				json.getString("city"),
				toChoice(json.getObject("province")),
				toChoice(json.getObject("country")),
				json.getString("postalCode"));
	}

	/**
	 * Binds the given json object to a PersonName model
	 * @param json
	 * @return
	 */
	public static PersonName toPersonName(JsonObject json) {
		return new PersonName(
				toChoice(json.getObject("salutation")),
				json.getString("firstName"),
				json.getString("middleName"),
				json.getString("lastName"));
	}

	/**
	 * Binds the given json object to a Telephone model
	 * @param json
	 * @return
	 */
	public static Telephone toTelephone(JsonObject json) {
		return new Telephone(
				json.getString("number"),
				json.getString("extension"));
	}

	/**
	 * Binds the given json object to a Communication model
	 * @param json
	 * @return
	 */
	public static Communication toCommunication(JsonObject json) {
		return new Communication(
				json.getString("jobTitle"),
				toChoice(json.getObject("language")),
				json.getString("email"),
				toTelephone(json.getObject("homePhone")),
				json.getString("faxNumber"));
	}

	/**
	 * Binds the given json object to an OrganizationSummary model
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
	 * Binds the given json object to an OrganizationDetails model
	 * @param json
	 * @return
	 */
	public static OrganizationDetails toOrganizationDetails(JsonObject json) {
		LocationIdentifier locationId = null;
		PersonIdentifier contactId = null;
		if (json.contains("mainLocation")) {
			locationId = new LocationIdentifier(json.getObject("mainLocation").getString("locationId"));
		}
		if (json.contains("mainContact")) {
			contactId = new PersonIdentifier(json.getObject("mainContact").getString("personId"));
		}
		return new OrganizationDetails(
				new OrganizationIdentifier(json.getString("organizationId")),
				Status.of(json.getString("status")),
				json.getString("displayName"),
				locationId,
				contactId,
				toIdentifierList(AuthenticationGroupIdentifier::new, json.getArray("authenticationGroups"), "optionId"),
				toIdentifierList(BusinessGroupIdentifier::new, json.getArray("businessGroups"), "optionId"));
	}

	/**
	 * Binds the given json object to a LocationSummary model
	 * @param json
	 * @return
	 */
	public static LocationSummary toLocationSummary(JsonObject json) {
		return new LocationSummary(
				new LocationIdentifier(json.getString("locationId")),
				new OrganizationIdentifier(json.getString("organizationId")),
				Status.of(json.getString("status")),
				json.getString("reference"),
				json.getString("displayName"));
	}

	/**
	 * Binds the given json object to a LocationDetails model
	 * @param json
	 * @return
	 */
	public static LocationDetails toLocationDetails(JsonObject json) {
		return new LocationDetails(
				new LocationIdentifier(json.getString("locationId")),
				new OrganizationIdentifier(json.getObject("organization").getString("organizationId")),
				Status.of(json.getString("status")),
				json.getString("reference"),
				json.getString("displayName"),
				toMailingAddress(json.getObject("address")));
	}

	/**
	 * Binds the given json object to a PersonSummary model
	 * @param json
	 * @return
	 */
	public static PersonSummary toPersonSummary(JsonObject json) {
		return new PersonSummary(
				new PersonIdentifier(json.getString("personId")),
				new OrganizationIdentifier(json.getString("organizationId")),
				Status.of(json.getString("status")),
				json.getString("displayName"));
	}

	/**
	 * Binds the given json object to a PersonDetails model
	 * @param json
	 * @return
	 */
	public static PersonDetails toPersonDetails(JsonObject json) {
		return new PersonDetails(
				new PersonIdentifier(json.getString("personId")),
				new OrganizationIdentifier(json.getString("organizationId")),
				Status.of(json.getString("status")),
				json.getString("displayName"),
				toPersonName(json.getObject("legalName")),
				toMailingAddress(json.getObject("address")),
				toCommunication(json.getObject("communication")),
				toIdentifierList(BusinessRoleIdentifier::new, json.getArray("businessRoles"), "optionId"));
	}

	/**
	 * Binds the given json object to a UserSummary model
	 * @param json
	 * @return
	 */
	public static UserSummary toUserSummary(JsonObject json) {
		return new UserSummary(
				new UserIdentifier(json.getString("userId")),
				new OrganizationIdentifier(json.getObject("organization").getString("organizationId")),
				json.getString("username"),
				Status.of(json.getString("status")));
	}
	
	/**
	 * Binds the given json object to a UserDetails model
	 * @param json
	 * @return
	 */
	public static UserDetails toUserDetails(JsonObject json) {
		return new UserDetails(
				new UserIdentifier(json.getString("userId")),
				new OrganizationIdentifier(json.getObject("organization").getString("organizationId")),
				new PersonIdentifier(json.getObject("person").getString("personId")),				
				json.getString("username"),
				Status.of(json.getString("status")),
				toIdentifierList(AuthenticationRoleIdentifier::new, json.getArray("authenticationRoles"), "optionId"));
	}

	/**
	 * Binds the given json object to an Option model
	 * @param json
	 * @return
	 */
	public static Option toOption(JsonObject json) {
		OptionIdentifier parentId = null;
		if (json.contains("parent")) {
			parentId = IdentifierFactory.forOptionId(json.getObject("parent").getString("optionId"));
		}
		return new Option(
				IdentifierFactory.forOptionId(json.getString("optionId")), 
				parentId, 
				Type.of(json.getString("type")), 
				Status.of(json.getString("status")), 
				Boolean.valueOf(json.getString("mutable")),
				new Localized(
						json.getObject("name").getString("code"), 
						json.getObject("name").getString("english"), 
						json.getObject("name").getString("french")));
	}
	
	/**
	 * Returns the SortFields and SortInfo from the paging object
	 * @param paging
	 * @return
	 */
	public static Pair<List<String>, List<String>> getSortInfo(Paging paging) {
		List<String> sortFields = new ArrayList<String>();
		List<String> sortDirections = new ArrayList<String>();
		if (paging.getSort().isEmpty()) {
			/* default order is displayName ascending */
			sortFields.add("displayName");
			sortDirections.add(Direction.ASC.toString());
		} else {
			for (Iterator<Order> iter = paging.getSort().iterator(); iter.hasNext();) {
				Order order = iter.next();
				sortFields.add(order.getProperty());
				sortDirections.add(order.getDirection().toString());
			}
		}
		return Pair.of(sortFields, sortDirections);
	}
	
	public static <T> FilteredPage<T> toPage(Serializable filter, Paging paging, Function<JsonObject, T> constructor, JsonObject json) {
		try {
			List<T> contents = new ArrayList<>();
			JsonArray content = json.getArray("content");
			for (int i = 0; i < content.size(); i++) {
				contents.add(constructor.apply(content.getObject(i)));
			}
			return new FilteredPage<T>(filter, paging, contents, json.getInt("totalElements"));
		} catch (Exception jsone) {
			throw new RuntimeException("Error constructing Page from: " + json.toString(), jsone);
		}
	}
	

	//	public static <T> List<T> toList(Function<JSONObject, T> constructor, JSONArray jsonArray) {
	//		try {
	//			List<T> list = new ArrayList<>();
	//			for (int i = 0; i < jsonArray.length(); i++) {
	//				list.add(constructor.apply(jsonArray.getJSONObject(i)));
	//			}
	//			return list;
	//		} catch (Exception jsone) {
	//			throw new RuntimeException("Error constructing List from: " + jsonArray.toString(), jsone);
	//		}
	//	}
	//
	//	public static List<String> toStringList(JSONArray jsonArray, String fieldId) {
	//		try {
	//			List<String> list = new ArrayList<>();
	//			for (int i = 0; i < jsonArray.length(); i++) {
	//				if (fieldId == null) {
	//					list.add(jsonArray.getString(i));
	//				} else {
	//					list.add(jsonArray.getJSONObject(i).getString(fieldId));
	//				}
	//			}
	//			return list;
	//		} catch (Exception jsone) {
	//			throw new RuntimeException("Error constructing List from: " + jsonArray.toString(), jsone);
	//		}
	//	}

	//	public static List<String> toIdentifierList(JSONArray jsonArray) {
	//		try {
	//			List<String> list = new ArrayList<>();
	//			for (int i = 0; i < jsonArray.length(); i++) {
	//				list.add(jsonArray.getString(i));
	//			}
	//			return list;
	//		} catch (Exception jsone) {
	//			throw new RuntimeException("Error constructing List from: " + jsonArray.toString(), jsone);
	//		}
	//	}

		
}
