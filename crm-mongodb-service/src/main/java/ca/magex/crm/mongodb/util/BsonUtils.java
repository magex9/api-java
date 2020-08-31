package ca.magex.crm.mongodb.util;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.bson.conversions.Bson;
import org.springframework.data.domain.Sort.Direction;

import com.mongodb.BasicDBObject;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Sorts;

import ca.magex.crm.api.authentication.CrmPasswordDetails;
import ca.magex.crm.api.common.Communication;
import ca.magex.crm.api.common.MailingAddress;
import ca.magex.crm.api.common.PersonName;
import ca.magex.crm.api.common.Telephone;
import ca.magex.crm.api.crm.LocationDetails;
import ca.magex.crm.api.crm.PersonDetails;
import ca.magex.crm.api.crm.UserDetails;
import ca.magex.crm.api.filters.LocationsFilter;
import ca.magex.crm.api.filters.OptionsFilter;
import ca.magex.crm.api.filters.OrganizationsFilter;
import ca.magex.crm.api.filters.Paging;
import ca.magex.crm.api.filters.PersonsFilter;
import ca.magex.crm.api.filters.UsersFilter;
import ca.magex.crm.api.system.Choice;
import ca.magex.crm.api.system.Option;

public class BsonUtils {

	/**
	 * Converts the option to a Bson for persistence
	 * @param option
	 * @return
	 */
	public static Bson toBson(Option option) {
		return new BasicDBObject()
				.append("optionId", option.getOptionId().getFullIdentifier())
				.append("parentId", option.getParentId() == null ? null : option.getParentId().getFullIdentifier())
				.append("status", option.getStatus().getCode())
				.append("mutable", option.getMutable())
				.append("name", new BasicDBObject()
						.append("code", option.getName().getCode())
						.append("english", option.getName().getEnglishName())
						.append("english_searchable", TextUtils.toSearchable(option.getName().getEnglishName()))
						.append("french", option.getName().getFrenchName())
						.append("french_searchable", TextUtils.toSearchable(option.getName().getFrenchName())))
				.append("lastModified", option.getLastModified());
	}
	
	/**
	 * converts a location details to a Bson object
	 * @param location
	 * @return
	 */
	public static Bson toBson(LocationDetails location) {
		if (location == null) {
			return null;
		}
		return new BasicDBObject()
				.append("locationId", location.getLocationId().getFullIdentifier())
				.append("status", location.getStatus().getCode())
				.append("reference", location.getReference())
				.append("displayName", location.getDisplayName())
				.append("displayName_searchable", TextUtils.toSearchable(location.getDisplayName()))
				.append("address", toBson(location.getAddress()))
				.append("lastModified", location.getLastModified());
	}
	
	/**
	 * converts a person details to a Bson object
	 * @param person
	 * @return
	 */
	public static Bson toBson(PersonDetails person) {
		if (person == null) {
			return null;
		}
		return new BasicDBObject()
				.append("personId", person.getPersonId().getFullIdentifier())
				.append("status", person.getStatus().getCode())
				.append("displayName", person.getDisplayName())
				.append("displayName_searchable", TextUtils.toSearchable(person.getDisplayName()))
				.append("legalName", BsonUtils.toBson(person.getLegalName()))
				.append("address", BsonUtils.toBson(person.getAddress()))
				.append("communication", BsonUtils.toBson(person.getCommunication()))
				.append("businessRoleIds", person
						.getBusinessRoleIds()
						.stream()
						.map((id) -> id.getFullIdentifier())
						.collect(Collectors.toList()))
				.append("lastModified", person.getLastModified());
	}
	
	/**
	 * converts a user to a Bson object
	 * @param user
	 */
	public static Bson toBson(UserDetails user) {
		if (user == null) {
			return null;
		}
		return new BasicDBObject()
				.append("personId", user.getPersonId().getFullIdentifier())
				.append("userId", user.getUserId().getFullIdentifier())
				.append("status", user.getStatus().getCode())
				.append("username", user.getUsername())
				.append("username_searchable", TextUtils.toSearchable(user.getUsername()))				
				.append("authenticationRoleIds", user
						.getAuthenticationRoleIds()
						.stream()
						.map((id) -> id.getFullIdentifier())
						.collect(Collectors.toList()))
				.append("lastModified", user.getLastModified())
				.append("passwords", List.of());
	}
	
	/**
	 * converts a password to a Bson object
	 * @param password
	 * @return
	 */
	public static Bson toBson(CrmPasswordDetails password) {
		if (password == null) {
			return null;
		}
		return new BasicDBObject()
				.append("cipherText", password.getCipherText())
				.append("temporary", password.isTemporary())
				.append("expiration", password.getExpiration() == null ? null : password.getExpiration().getTime());
	}
	
	/**
	 * converts a mailing address to a Bson object
	 * @param address
	 * @return
	 */
	public static Bson toBson(MailingAddress address) {
		if (address == null) {
			return null;	
		}
		return new BasicDBObject()
				.append("street", address.getStreet())
				.append("city", address.getCity())
				.append("province", toBson(address.getProvince()))					
				.append("country", toBson(address.getCountry()))				
				.append("postalCode", address.getPostalCode());
	}
	
	/**
	 * converts a communication to a Bson object
	 * @param communication
	 * @return
	 */
	public static Bson toBson(Communication communication) {
		if (communication == null) {
			return null;	
		}
		return new BasicDBObject()
				.append("jobTitle", communication.getJobTitle())
				.append("language", toBson(communication.getLanguage()))
				.append("email", communication.getEmail())					
				.append("homePhone", toBson(communication.getHomePhone()))				
				.append("faxNumber", communication.getFaxNumber());
	}
	
	public static Bson toBson(Telephone phone) {
		if (phone == null) {
			return null;
		}
		return new BasicDBObject()
				.append("number", phone.getNumber())
				.append("extension", phone.getExtension());
	}
	
	/**
	 * converts a choice to a Bson object
	 * @param choice
	 * @return
	 */
	public static Bson toBson(Choice<?> choice) {
		if (choice == null || choice.isEmpty()) {
			return new BasicDBObject()
					.append("identifier", null)
					.append("other", null);
		}
		else if (choice.isIdentifer()) {
			return new BasicDBObject()
					.append("identifier", choice.getIdentifier().getFullIdentifier())
					.append("other", null);
		}
		else {
			return new BasicDBObject()
					.append("identifier", null)
					.append("other", choice.getOther());
		}
	}	
	
	/**
	 * converts person name to a Bson
	 * @param personName
	 * @return
	 */
	public static Bson toBson(PersonName personName) {
		if (personName == null) {
			return null;
		}
		return new BasicDBObject()
				.append("salutation", toBson(personName.getSalutation()))
				.append("firstName", personName.getFirstName())
				.append("middleName", personName.getMiddleName())
				.append("surname", personName.getLastName());
	}
	
	/**
	 * constructs a Bson based on the given Options Filter or null if no filtering provided
	 * @param filter
	 * @return
	 */
	public static Bson toBson(OptionsFilter filter) {
		List<Bson> filters = new ArrayList<>();
		if (filter.getStatus() != null) {
			filters.add(Filters.eq("options.status", filter.getStatusCode()));
		}
		if (filter.getParentId() != null) {
			filters.add(Filters.eq("options.parentId", filter.getParentId().getFullIdentifier()));
		}
		if (StringUtils.isNotBlank(filter.getCode())) {
			filters.add(Filters.or(
					Filters.eq("options.name.code", filter.getCode()), 						// matches full code
					Filters.regex("options.name.code", "/" + filter.getCode() + "$"))); 	// ends with /code
		}
		if (StringUtils.isNotBlank(filter.getEnglishName())) {
			filters.add(Filters.eq("options.name.english_searchable", TextUtils.toSearchable(filter.getEnglishName())));
		}		
		if (StringUtils.isNotBlank(filter.getFrenchName())) {
			filters.add(Filters.eq("options.name.french_searchable", TextUtils.toSearchable(filter.getFrenchName())));
		}
		return conjunction(filters);		
	}
	
	/**
	 * Constructs a Bson based on the given organizations filter or null if no filtering provided
	 * 
	 * @param filter
	 * @return
	 */
	public static Bson toBson(OrganizationsFilter filter) {
		List<Bson> filters = new ArrayList<>();
		if (filter.getStatus() != null) {
			filters.add(Filters.eq("status", filter.getStatusCode()));
		}
		if (StringUtils.isNotBlank(filter.getDisplayName())) {
			filters.add(Filters.eq("displayName_searchable", TextUtils.toSearchable(filter.getDisplayName())));
		}
		if (filter.getAuthenticationGroupId() != null) {
			filters.add(Filters.eq("authenticationGroupIds", filter.getAuthenticationGroupId().getFullIdentifier()));
		}
		if (filter.getBusinessGroupId() != null) {
			filters.add(Filters.eq("businessGroupIds", filter.getBusinessGroupId().getFullIdentifier()));
		}

		return conjunction(filters);
	}
	
	/**
	 * constructs a Bson based on the given Options Filter or null if no filtering provided
	 * @param filter
	 * @return
	 */
	public static Bson toBson(LocationsFilter filter) {
		List<Bson> filters = new ArrayList<>();
		if (filter.getStatus() != null) {
			filters.add(Filters.eq("locations.status", filter.getStatusCode()));
		}		
		if (StringUtils.isNotBlank(filter.getReference())) {
			filters.add(Filters.eq("locations.reference", filter.getReference()));
		}
		if (StringUtils.isNotBlank(filter.getDisplayName())) {
			filters.add(Filters.eq("locations.displayName_searchable", TextUtils.toSearchable(filter.getDisplayName())));
		}
		return conjunction(filters);		
	}
	
	/**
	 * constructs a Bson based on the given person Filter or null if no filtering provided
	 * @param filter
	 * @return
	 */
	public static Bson toBson(PersonsFilter filter) {
		List<Bson> filters = new ArrayList<>();
		if (filter.getStatus() != null) {
			filters.add(Filters.eq("persons.status", filter.getStatusCode()));
		}		
		if (StringUtils.isNotBlank(filter.getDisplayName())) {
			filters.add(Filters.eq("persons.displayName_searchable", TextUtils.toSearchable(filter.getDisplayName())));
		}
		return conjunction(filters);		
	}
	
	/**
	 * constructs a Bson based on the given users filter or null if no filtering provided
	 * @param filter
	 * @return
	 */
	public static Bson toBson(UsersFilter filter) {
		List<Bson> filters = new ArrayList<>();
		if (filter.getStatus() != null) {
			filters.add(Filters.eq("users.status", filter.getStatusCode()));
		}		
		if (StringUtils.isNotBlank(filter.getUsername())) {
			filters.add(Filters.eq("users.username_searchable", TextUtils.toSearchable(filter.getUsername())));
		}
		if (StringUtils.isNotBlank(filter.getPersonId())) {
			filters.add(Filters.eq("users.personId", filter.getPersonId().getFullIdentifier()));
		}
		if (StringUtils.isNotBlank(filter.getAuthenticationRoleId())) {
			filters.add(Filters.eq("users.authenticationRoleIds", filter.getAuthenticationRoleId().getFullIdentifier()));
		}
		return conjunction(filters);
	}
	
	/**
	 * returns a conjunction of the given components or null of list is empty
	 * @param components
	 * @return
	 */
	public static Bson conjunction(List<Bson> components) {
		if (components.size() == 0) {
			return null;
		}
		else if (components.size() == 1) {
			return components.get(0);
		}
		else {
			return Filters.and(components);
		}
	}
	
	/**
	 * returns a sorting component based on the paging required
	 * @param paging
	 * @return
	 */
	public static Bson toBson(Paging paging) {
		return toBson(paging, null);
	}

	/**
	 * returns a sorting component based on the paging required and the prefix for each field
	 * @param paging
	 * @return
	 */
	public static Bson toBson(Paging paging, String prefix) {
		List<Bson> bsonSorts = new ArrayList<>();
		if (paging.getSort().isEmpty()) {
			return null;
		}
		else {
			paging.getSort().get().forEach((sort) -> {
				if (sort.getDirection() == Direction.ASC) {
					bsonSorts.add(Sorts.ascending(prefix == null ? sort.getProperty() : prefix + "." + sort.getProperty()));
				}
				else {
					bsonSorts.add(Sorts.descending(prefix == null ? sort.getProperty() :  prefix + "." + sort.getProperty()));
				}
			});
		}
		return Sorts.orderBy(bsonSorts);
	}
}