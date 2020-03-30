package ca.magex.crm.graphql.client;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.springframework.boot.configurationprocessor.json.JSONArray;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.domain.Sort.Order;

import ca.magex.crm.api.common.BusinessPosition;
import ca.magex.crm.api.common.Communication;
import ca.magex.crm.api.common.MailingAddress;
import ca.magex.crm.api.common.PersonName;
import ca.magex.crm.api.crm.Location;
import ca.magex.crm.api.crm.Organization;
import ca.magex.crm.api.crm.Person;
import ca.magex.crm.api.filters.LocationsFilter;
import ca.magex.crm.api.filters.OrganizationsFilter;
import ca.magex.crm.api.filters.PersonsFilter;
import ca.magex.crm.api.lookup.Country;
import ca.magex.crm.api.services.OrganizationService;
import ca.magex.crm.api.system.Identifier;
import ca.magex.crm.api.system.Message;
import ca.magex.crm.api.system.Role;
import ca.magex.crm.api.system.Status;

/**
 * Implementation of the Organization Service that uses a GraphQL Server
 * 
 * @author Jonny
 */
public class OrganizationServiceGraphQLClient extends GraphQLClient implements OrganizationService {

	/**
	 * constructs a new Service for the given graphql endpoint
	 * 
	 * @param endpoint
	 */
	public OrganizationServiceGraphQLClient(String endpoint) {
		super(endpoint, "/organization-service-queries.properties");
	}

	@Override
	public Organization createOrganization(String organizationName) {
		return toOrganization(performGraphQLQuery("createOrganization", 
				organizationName));
	}

	@Override
	public Organization enableOrganization(Identifier organizationId) {
		return toOrganization(performGraphQLQuery("enableOrganization", 
				organizationId));
	}

	@Override
	public Organization disableOrganization(Identifier organizationId) {
		return toOrganization(performGraphQLQuery("disableOrganization", 
				organizationId));
	}

	@Override
	public Organization updateOrganizationName(Identifier organizationId, String name) {
		return toOrganization(performGraphQLQuery("updateOrganizationName", 
				organizationId, 
				name));
	}

	@Override
	public Organization updateOrganizationMainLocation(Identifier organizationId, Identifier locationId) {
		return toOrganization(performGraphQLQuery("updateOrganizationMainLocation", 
				organizationId, 
				locationId));
	}

	@Override
	public Organization findOrganization(Identifier organizationId) {
		return toOrganization(performGraphQLQuery("findOrganization",
				organizationId));
	}

	@Override
	public long countOrganizations(OrganizationsFilter filter) {
		return toLong(performGraphQLQuery("countOrganizations",
				filter.getDisplayName()));
	}

	@Override
	public Page<Organization> findOrganizations(OrganizationsFilter filter) {
		List<String> sortFields = new ArrayList<String>();
		List<String> sortDirections = new ArrayList<String>();
		if (filter.getPaging().getSort().isEmpty()) {
			/* default order is displayName ascending */
			sortFields.add("displayName");
			sortDirections.add(Direction.ASC.toString());
		}
		else {			
			for (Iterator<Order> iter = filter.getPaging().getSort().iterator(); iter.hasNext();) {
				Order order = iter.next();
				sortFields.add(order.getProperty());
				sortDirections.add(order.getDirection().toString());
			}
		}
		
		JSONObject response = performGraphQLQuery("findOrganizations", 
				filter.getDisplayName(),
				filter.getPaging().getPageNumber(),
				filter.getPaging().getPageSize(),
				sortFields,
				sortDirections);
		
		try {
			List<Organization> contents = new ArrayList<>();
			JSONArray content = response.getJSONArray("content");		
			for (int i=0; i<content.length(); i++) {
				contents.add(toOrganization(content.getJSONObject(i)));
			}
			PageRequest pr = PageRequest.of(response.getInt("number") - 1, response.getInt("size"), filter.getPaging().getSort());
			
			return new PageImpl<Organization>(contents, pr, response.getInt("totalElements"));
		}
		catch(Exception jsone) {
			throw new RuntimeException("Error constructing OrganizationPage from: " + response.toString(), jsone);
		}
	}

	@Override
	public Location createLocation(Identifier organizationId, String locationName, String locationReference, MailingAddress address) {
		return toLocation(performGraphQLQuery("createLocation", 
				organizationId, 
				locationName, 
				locationReference, 
				address == null ? null : address.getStreet(),
				address == null ? null : address.getCity(),
				address == null ? null : address.getProvince(),
				address == null ? null : address.getCountry().getCode(),
				address == null ? null : address.getPostalCode()));
	}

	@Override
	public Location updateLocationName(Identifier locationId, String locationName) {
		return toLocation(performGraphQLQuery("updateLocationName", 
				locationId, 
				locationName));
	}

	@Override
	public Location updateLocationAddress(Identifier locationId, MailingAddress address) {
		return toLocation(performGraphQLQuery("updateLocationAddress",  
				locationId, 
				address == null ? null : address.getStreet(),
				address == null ? null : address.getCity(),
				address == null ? null : address.getProvince(),
				address == null ? null : address.getCountry().getCode(),
				address == null ? null : address.getPostalCode()));
	}

	@Override
	public Location enableLocation(Identifier locationId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Location disableLocation(Identifier locationId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Location findLocation(Identifier locationId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long countLocations(LocationsFilter filter) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Page<Location> findLocations(LocationsFilter filter) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Person createPerson(Identifier organizationId, PersonName name, MailingAddress address, Communication communication, BusinessPosition unit) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Person updatePersonName(Identifier personId, PersonName name) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Person updatePersonAddress(Identifier personId, MailingAddress address) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Person updatePersonCommunication(Identifier personId, Communication communication) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Person updatePersonBusinessUnit(Identifier personId, BusinessPosition unit) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Person enablePerson(Identifier personId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Person disablePerson(Identifier personId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Person addUserRole(Identifier personId, Role role) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Person removeUserRole(Identifier personId, Role role) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Person findPerson(Identifier personId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long countPersons(PersonsFilter filter) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Page<Person> findPersons(PersonsFilter filter) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Message> validate(Organization organization) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Message> validate(Location location) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Message> validate(Person person) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Message> validate(List<Role> roles) {
		// TODO Auto-generated method stub
		return null;
	}
	
	private long toLong(Number number) {
		return number.longValue();
	}

	private Organization toOrganization(JSONObject json) {
		try {
			Identifier locationId = null;
			if (json.has("mainLocation") && json.get("mainLocation") != JSONObject.NULL) {
				locationId = new Identifier(json.getJSONObject("mainLocation").getString("locationId"));
			}
			return new Organization(
					new Identifier(json.getString("organizationId")),
					Status.valueOf(json.getString("status")),
					json.getString("displayName"),
					locationId);
		} catch (JSONException jsone) {
			throw new RuntimeException("Error constructing Organization from: " + json.toString(), jsone);
		}
	}
	
	private Location toLocation(JSONObject json) {
		try {			
			return new Location(
					new Identifier(json.getString("locationId")),
					new Identifier(json.getString("organizationId")),
					Status.valueOf(json.getString("status")),
					json.getString("reference"),
					json.getString("displayName"),
					toMailingAddress(json.getJSONObject("address")));
					
		} catch (JSONException jsone) {
			throw new RuntimeException("Error constructing Location from: " + json.toString(), jsone);
		}
	}
	
	private MailingAddress toMailingAddress(JSONObject json) {
		try {			
			JSONObject country = json.getJSONObject("country");
			return new MailingAddress(
					json.getString("street"),
					json.getString("city"),
					json.getString("province"),
					new Country(country.getString("code"), country.getString("name")),
					json.getString("postalCode"));
					
		} catch (JSONException jsone) {
			throw new RuntimeException("Error constructing Location from: " + json.toString(), jsone);
		}
	}
}