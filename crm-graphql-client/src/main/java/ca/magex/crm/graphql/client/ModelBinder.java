package ca.magex.crm.graphql.client;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Function;

import org.springframework.boot.configurationprocessor.json.JSONArray;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.domain.Sort.Order;
import org.springframework.data.util.Pair;

import ca.magex.crm.api.common.BusinessPosition;
import ca.magex.crm.api.common.Communication;
import ca.magex.crm.api.common.MailingAddress;
import ca.magex.crm.api.common.PersonName;
import ca.magex.crm.api.common.Telephone;
import ca.magex.crm.api.common.User;
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
import ca.magex.crm.api.system.Identifier;
import ca.magex.crm.api.system.Role;
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
					locationId);
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
					toBusinessPosition(json.getJSONObject("position")),
					toUser(json.get("user") == JSONObject.NULL ? null : json.getJSONObject("user")));
		} catch (JSONException jsone) {
			throw new RuntimeException("Error constructing PersonDetails from: " + json.toString(), jsone);
		}
	}

	public static MailingAddress toMailingAddress(JSONObject json) {
		try {
			JSONObject country = json.getJSONObject("country");
			return new MailingAddress(
					json.getString("street"),
					json.getString("city"),
					json.getString("province"),
					new Country(country.getString("code"), country.getString("englishName"), country.getString("frenchName")),
					json.getString("postalCode"));

		} catch (JSONException jsone) {
			throw new RuntimeException("Error constructing MailingAddress from: " + json.toString(), jsone);
		}
	}

	public static PersonName toPersonName(JSONObject json) {
		try {
			JSONObject salutation = json.getJSONObject("salutation");
			return new PersonName(
					new Salutation(salutation.getInt("code"), salutation.getString("englishName"), salutation.getString("frenchName")),
					json.getString("firstName"),
					json.getString("middleName"),
					json.getString("lastName"));

		} catch (JSONException jsone) {
			throw new RuntimeException("Error constructing PersonName from: " + json.toString(), jsone);
		}
	}

	public static Communication toCommunication(JSONObject json) {
		try {
			JSONObject language = json.getJSONObject("language");
			JSONObject telephone = json.getJSONObject("homePhone");
			return new Communication(
					json.getString("jobTitle"),
					new Language(language.getString("code"), language.getString("englishName"), language.getString("frenchName")),
					json.getString("email"),
					new Telephone(telephone.getString("number"), telephone.getString("extension")),
					json.getString("faxNumber"));
		} catch (JSONException jsone) {
			throw new RuntimeException("Error constructing Communication from: " + json.toString(), jsone);
		}
	}
	
	public static BusinessPosition toBusinessPosition(JSONObject json) {
		try {
			JSONObject sector = json.getJSONObject("sector");
			JSONObject unit = json.getJSONObject("unit");
			JSONObject classification = json.getJSONObject("classification");
			return new BusinessPosition(
					new BusinessSector(sector.getInt("code"), sector.getString("englishName"), sector.getString("frenchName")),
					new BusinessUnit(unit.getInt("code"), unit.getString("englishName"), unit.getString("frenchName")),
					new BusinessClassification(classification.getInt("code"), classification.getString("englishName"), classification.getString("frenchName")));
		} catch (JSONException jsone) {
			throw new RuntimeException("Error constructing BusinessPosition from: " + json.toString(), jsone);
		}
	}
	
	public static User toUser(JSONObject json) {
		try {
			if (json == null) {
				return null;
			}
			JSONArray roleArray = json.getJSONArray("roles");
			List<Role> roles = new ArrayList<>();
			for (int i=0; i<roleArray.length(); i++) {
				JSONObject role = roleArray.getJSONObject(i);
				roles.add(new Role(role.getString("code"), role.getString("englishName"), role.getString("frenchName")));
			}
			return new User(
					json.getString("userName"),
					roles);
		} catch (JSONException jsone) {
			throw new RuntimeException("Error constructing User from: " + json.toString(), jsone);
		}
	}

	public static <T> Page<T> toPage(Paging paging, Function<JSONObject, T> constructor, JSONObject json) {
		try {
			List<T> contents = new ArrayList<>();
			JSONArray content = json.getJSONArray("content");
			for (int i = 0; i < content.length(); i++) {
				contents.add(constructor.apply(content.getJSONObject(i)));
			}
			PageRequest pr = PageRequest.of(json.getInt("number") - 1, json.getInt("size"), paging.getSort());

			return new PageImpl<T>(contents, pr, json.getInt("totalElements"));
		} catch (Exception jsone) {
			throw new RuntimeException("Error constructing Page from: " + json.toString(), jsone);
		}
	}
}
