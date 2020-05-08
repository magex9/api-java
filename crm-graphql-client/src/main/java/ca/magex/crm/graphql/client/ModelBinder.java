package ca.magex.crm.graphql.client;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Function;

import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.configurationprocessor.json.JSONArray;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.domain.Sort.Order;
import org.springframework.data.util.Pair;

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
import ca.magex.crm.api.filters.Paging;
import ca.magex.crm.api.lookup.BusinessClassification;
import ca.magex.crm.api.lookup.BusinessSector;
import ca.magex.crm.api.lookup.BusinessUnit;
import ca.magex.crm.api.lookup.Country;
import ca.magex.crm.api.lookup.Language;
import ca.magex.crm.api.lookup.Salutation;
import ca.magex.crm.api.roles.User;
import ca.magex.crm.api.system.Identifier;
import ca.magex.crm.api.system.Status;

/**
 * Binds the JSON Resonses back to the CRM Model Objects
 * 
 * @author Jonny
 *
 */
public class ModelBinder {

	public static long toLong(Number number) {
		return number.longValue();
	}

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

	public static OrganizationSummary toOrganizationSummary(JSONObject json) {
		try {
			return new OrganizationSummary(
					new Identifier(json.getString("organizationId")),
					Status.valueOf(json.getString("status")),
					json.getString("displayName"));
		} catch (JSONException jsone) {
			throw new RuntimeException("Error constructing OrganizationSummary from: " + json.toString(), jsone);
		}
	}

	public static OrganizationDetails toOrganizationDetails(JSONObject json) {
		try {
			Identifier locationId = null;
			if (json.has("mainLocation") && json.get("mainLocation") != JSONObject.NULL) {
				locationId = new Identifier(json.getJSONObject("mainLocation").getString("locationId"));
			}
			return new OrganizationDetails(
					new Identifier(json.getString("organizationId")),
					Status.valueOf(json.getString("status")),
					json.getString("displayName"),
					locationId,
					toIdentifierList(json.getJSONArray("groupIds")));
		} catch (JSONException jsone) {
			throw new RuntimeException("Error constructing OrganizationDetails from: " + json.toString(), jsone);
		}
	}

	public static LocationSummary toLocationSummary(JSONObject json) {
		try {
			return new LocationSummary(
					new Identifier(json.getString("locationId")),
					new Identifier(json.getString("organizationId")),
					Status.valueOf(json.getString("status")),
					json.getString("reference"),
					json.getString("displayName"));

		} catch (JSONException jsone) {
			throw new RuntimeException("Error constructing LocationSummary from: " + json.toString(), jsone);
		}
	}

	public static LocationDetails toLocationDetails(JSONObject json) {
		try {
			return new LocationDetails(
					new Identifier(json.getString("locationId")),
					new Identifier(json.getString("organizationId")),
					Status.valueOf(json.getString("status")),
					json.getString("reference"),
					json.getString("displayName"),
					toMailingAddress(json.getJSONObject("address")));

		} catch (JSONException jsone) {
			throw new RuntimeException("Error constructing LocationDetails from: " + json.toString(), jsone);
		}
	}
	
	public static PersonSummary toPersonSummary(JSONObject json) {
		try {
			return new PersonSummary(
					new Identifier(json.getString("personId")),
					new Identifier(json.getString("organizationId")),
					Status.valueOf(json.getString("status")),
					json.getString("displayName"));
		} catch (JSONException jsone) {
			throw new RuntimeException("Error constructing PersonDetails from: " + json.toString(), jsone);
		}
	}

	public static PersonDetails toPersonDetails(JSONObject json) {
		try {
			return new PersonDetails(
					new Identifier(json.getString("personId")),
					new Identifier(json.getString("organizationId")),
					Status.valueOf(json.getString("status")),
					json.getString("displayName"),
					toPersonName(json.getJSONObject("legalName")),
					toMailingAddress(json.getJSONObject("address")),
					toCommunication(json.getJSONObject("communication")),
					toBusinessPosition(json.getJSONObject("position")));
		} catch (JSONException jsone) {
			throw new RuntimeException("Error constructing PersonDetails from: " + json.toString(), jsone);
		}
	}

	public static MailingAddress toMailingAddress(JSONObject json) {
		try {
			return new MailingAddress(
					json.getString("street"),
					json.getString("city"),
					json.getString("province"),
					json.getString("country"),
					json.getString("postalCode"));

		} catch (JSONException jsone) {
			throw new RuntimeException("Error constructing MailingAddress from: " + json.toString(), jsone);
		}
	}

	public static PersonName toPersonName(JSONObject json) {
		try {
			return new PersonName(
					json.getString("salutation"),
					json.getString("firstName"),
					json.getString("middleName"),
					json.getString("lastName"));

		} catch (JSONException jsone) {
			throw new RuntimeException("Error constructing PersonName from: " + json.toString(), jsone);
		}
	}

	public static Communication toCommunication(JSONObject json) {
		try {
			return new Communication(
					json.getString("jobTitle"),
					json.getString("language"),
					json.getString("email"),
					toTelephone(json.getJSONObject("homePhone")), 
					json.getString("faxNumber"));
		} catch (JSONException jsone) {
			throw new RuntimeException("Error constructing Communication from: " + json.toString(), jsone);
		}
	}
	
	public static BusinessPosition toBusinessPosition(JSONObject json) {
		try {
			return new BusinessPosition(
					json.getString("sector"),
					json.getString("unit"),
					json.getString("classification"));
		} catch (JSONException jsone) {
			throw new RuntimeException("Error constructing BusinessPosition from: " + json.toString(), jsone);
		}
	}
	
	public static User toUser(JSONObject json) {
		try {
			return new User(
					new Identifier(json.getString("userId")),
					toPersonSummary(json.getJSONObject("person")),
					Status.valueOf(json.getString("status")));
		} catch (JSONException jsone) {
			throw new RuntimeException("Error constructing User from: " + json.toString(), jsone);
		}
	}
	
	public static Telephone toTelephone(JSONObject json) {
		try {
			return new Telephone(json.getString("number"), json.getString("extension"));
		} catch (JSONException jsone) {
			throw new RuntimeException("Error constructing Telephone from: " + json.toString(), jsone);
		}
	}
	
	public static Status toStatus(JSONObject json) {
		try {
			return Status.valueOf(StringUtils.upperCase(json.getString("code")));
		} catch (JSONException jsone) {
			throw new RuntimeException("Error constructing Role from: " + json.toString(), jsone);
		}
	}
	
	public static Country toCountry(JSONObject json) {
		try {
			return new Country(json.getString("code"), json.getString("englishName"), json.getString("frenchName"));
		} catch (JSONException jsone) {
			throw new RuntimeException("Error constructing Country from: " + json.toString(), jsone);
		}
	}
	
	public static Salutation toSalutation(JSONObject json) {
		try {
			return new Salutation(json.getString("code"), json.getString("englishName"), json.getString("frenchName"));
		} catch (JSONException jsone) {
			throw new RuntimeException("Error constructing Salutation from: " + json.toString(), jsone);
		}
	}
	
	public static Language toLanguage(JSONObject json) {
		try {
			return new Language(json.getString("code"), json.getString("englishName"), json.getString("frenchName"));
		} catch (JSONException jsone) {
			throw new RuntimeException("Error constructing Language from: " + json.toString(), jsone);
		}
	}
	
	public static BusinessSector toBusinessSector(JSONObject json) {
		try {
			return new BusinessSector(json.getString("code"), json.getString("englishName"), json.getString("frenchName"));
		} catch (JSONException jsone) {
			throw new RuntimeException("Error constructing BusinessSector from: " + json.toString(), jsone);
		}
	}
	
	public static BusinessUnit toBusinessUnit(JSONObject json) {
		try {
			return new BusinessUnit(json.getString("code"), json.getString("englishName"), json.getString("frenchName"));
		} catch (JSONException jsone) {
			throw new RuntimeException("Error constructing BusinessUnit from: " + json.toString(), jsone);
		}
	}
	
	public static BusinessClassification toBusinessClassification(JSONObject json) {
		try {
			return new BusinessClassification(json.getString("code"), json.getString("englishName"), json.getString("frenchName"));
		} catch (JSONException jsone) {
			throw new RuntimeException("Error constructing BusinessClassification from: " + json.toString(), jsone);
		}
	}
	
	public static <T> List<T> toList(Function<JSONObject, T> constructor, JSONArray jsonArray) {
		try {
			List<T> list = new ArrayList<>();
			for (int i=0; i<jsonArray.length(); i++) {
				list.add(constructor.apply(jsonArray.getJSONObject(i)));
			}
			return list;
		} catch (Exception jsone) {
			throw new RuntimeException("Error constructing List from: " + jsonArray.toString(), jsone);
		}
	}
	
	public static <T> List<T> toSimpleList(Function<Object, T> constructor, JSONArray jsonArray) {
		try {
			List<T> list = new ArrayList<>();
			for (int i=0; i<jsonArray.length(); i++) {
				list.add(constructor.apply(jsonArray.get(i)));
			}
			return list;
		} catch (Exception jsone) {
			throw new RuntimeException("Error constructing List from: " + jsonArray.toString(), jsone);
		}
	}
	
	public static List<Identifier> toIdentifierList(JSONArray jsonArray) {
		try {
			List<Identifier> list = new ArrayList<>();
			for (int i=0; i<jsonArray.length(); i++) {
				list.add(new Identifier(jsonArray.getString(i)));
			}
			return list;
		} catch (Exception jsone) {
			throw new RuntimeException("Error constructing List from: " + jsonArray.toString(), jsone);
		}
	}

	public static <T> Page<T> toPage(Paging paging, Function<JSONObject, T> constructor, JSONObject json) {
		try {
			List<T> contents = new ArrayList<>();
			JSONArray content = json.getJSONArray("content");
			for (int i = 0; i < content.length(); i++) {
				contents.add(constructor.apply(content.getJSONObject(i)));
			}
			return new PageImpl<T>(contents, paging, json.getInt("totalElements"));
		} catch (Exception jsone) {
			throw new RuntimeException("Error constructing Page from: " + json.toString(), jsone);
		}
	}
}
