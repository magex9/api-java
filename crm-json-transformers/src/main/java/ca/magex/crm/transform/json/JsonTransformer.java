package ca.magex.crm.transform.json;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

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
import ca.magex.crm.api.lookup.BusinessClassification;
import ca.magex.crm.api.lookup.BusinessSector;
import ca.magex.crm.api.lookup.BusinessUnit;
import ca.magex.crm.api.lookup.Country;
import ca.magex.crm.api.lookup.Language;
import ca.magex.crm.api.lookup.Province;
import ca.magex.crm.api.lookup.Salutation;
import ca.magex.crm.api.roles.Group;
import ca.magex.crm.api.roles.Role;
import ca.magex.crm.api.roles.User;
import ca.magex.crm.api.services.CrmServices;
import ca.magex.crm.api.system.Identifier;
import ca.magex.crm.api.system.Lang;
import ca.magex.crm.api.system.Localized;
import ca.magex.crm.api.system.Status;
import ca.magex.json.model.JsonArray;
import ca.magex.json.model.JsonElement;
import ca.magex.json.model.JsonObject;
import ca.magex.json.model.JsonPair;
import ca.magex.json.model.JsonText;

public class JsonTransformer {
	
	private final CrmServices crm;
	
	private final Locale locale;
	
	private final boolean linked;
	
	public JsonTransformer(CrmServices crm, Locale locale, boolean linked) {
		this.crm = crm;
		this.locale = locale;
		this.linked = linked;
	}
	
	public String getContext(Class<?> cls) {
		return "http://magex9.github.io/api/";
	}
	
	public String getType(Class<?> cls) {
		return cls.getSimpleName();
	}
	
	public JsonObject formatGroup(Group group) {
		if (group == null)
			return null;
		List<JsonPair> pairs = new ArrayList<JsonPair>();
		formatIdentifier(pairs, "groupId", group, Group.class);
		formatStatus(pairs, "status", group);
		formatText(pairs, "code", group);
		formatLocalized(pairs, "name", group);
		return pairs.isEmpty() ? null : new JsonObject(pairs);
	}
	
	public JsonObject formatRole(Role role) {
		if (role == null)
			return null;
		List<JsonPair> pairs = new ArrayList<JsonPair>();
		formatIdentifier(pairs, "roleId", role, Role.class);
		formatIdentifier(pairs, "groupId", role, Group.class);
		formatStatus(pairs, "status", role);
		formatText(pairs, "code", role);
		formatLocalized(pairs, "name", role);
		return pairs.isEmpty() ? null : new JsonObject(pairs);
	}
	
	public JsonObject formatLocationDetails(LocationDetails location) {
		if (location == null)
			return null;
		List<JsonPair> pairs = new ArrayList<JsonPair>();
		if (linked) {
			pairs.add(new JsonPair("@context", getContext(LocationDetails.class)));
			pairs.add(new JsonPair("@type", getType(LocationDetails.class)));
			pairs.add(new JsonPair("@id", location.getLocationId().toString()));
		} else {
			formatIdentifier(pairs, "locationId", location, LocationSummary.class);
		}
		formatIdentifier(pairs, "organizationId", location, OrganizationSummary.class);
		formatStatus(pairs, "status", location);
		formatText(pairs, "displayName", location);
		formatText(pairs, "reference", location);
		formatMailingAddress(pairs, "address", location);
		return pairs.isEmpty() ? null : new JsonObject(pairs);
	}

	public LocationDetails parseLocationDetails(JsonObject data) {
		Identifier locationId = parseIdentifier("locationId", data);
		Identifier organizationId = parseIdentifier("organizationId", data);
		Status status = parseStatus("status", data);
		String reference = parseText("reference", data);
		String displayName = parseText("displayName", data);
		MailingAddress address = parseMailingAddress("address", data);
		return new LocationDetails(locationId, organizationId, status, reference, displayName, address);
	}
	
	public JsonObject formatLocationSummary(LocationSummary location) {
		if (location == null)
			return null;
		List<JsonPair> pairs = new ArrayList<JsonPair>();
		if (linked) {
			pairs.add(new JsonPair("@context", getContext(LocationSummary.class)));
			pairs.add(new JsonPair("@type", getType(LocationSummary.class)));
			pairs.add(new JsonPair("@id", location.getLocationId().toString()));
		} else {
			formatIdentifier(pairs, "locationId", location, LocationSummary.class);
		}
		formatIdentifier(pairs, "organizationId", location, OrganizationSummary.class);
		formatStatus(pairs, "status", location);
		formatText(pairs, "displayName", location);
		formatText(pairs, "reference", location);
		return pairs.isEmpty() ? null : new JsonObject(pairs);
	}

	public LocationSummary parseLocationSummary(JsonObject data) {
		Identifier locationId = parseIdentifier("locationId", data);
		Identifier organizationId = parseIdentifier("organizationId", data);
		Status status = parseStatus("status", data);
		String reference = parseText("reference", data);
		String displayName = parseText("displayName", data);
		return new LocationSummary(locationId, organizationId, status, reference, displayName);
	}
	
	public JsonObject formatOrganizationDetails(OrganizationDetails organization) {
		if (organization == null)
			return null;
		List<JsonPair> pairs = new ArrayList<JsonPair>();
		if (linked) {
			pairs.add(new JsonPair("@context", getContext(OrganizationDetails.class)));
			pairs.add(new JsonPair("@type", getType(OrganizationDetails.class)));
			pairs.add(new JsonPair("@id", organization.getOrganizationId().toString()));
		} else {
			formatIdentifier(pairs, "organizationId", organization, OrganizationDetails.class);
		}
		formatStatus(pairs, "status", organization);
		formatText(pairs, "displayName", organization);
		formatIdentifier(pairs, "mainLocationId", organization, LocationDetails.class);
		formatIdentifier(pairs, "mainContactId", organization, PersonDetails.class);
		formatTexts(pairs, "groups", organization, Group.class);
		return pairs.isEmpty() ? null : new JsonObject(pairs);
	}

	public OrganizationDetails parseOrganizationDetails(JsonObject data) {
		Identifier organizationId = parseIdentifier("organizationId", data);
		Status status = parseStatus("status", data);
		String displayName = parseText("displayName", data);
		Identifier mainLocationId = parseIdentifier("mainLocationId", data);
		Identifier mainContactId = parseIdentifier("mainContactId", data);
		List<String> groups = parseTexts("groups", data);
		return new OrganizationDetails(organizationId, status, displayName, mainLocationId, mainContactId, groups);
	}
	
	public JsonObject formatOrganizationSummary(OrganizationSummary organization) {
		if (organization == null)
			return null;
		List<JsonPair> pairs = new ArrayList<JsonPair>();
		if (linked) {
			pairs.add(new JsonPair("@context", getContext(OrganizationSummary.class)));
			pairs.add(new JsonPair("@type", getType(OrganizationSummary.class)));
			pairs.add(new JsonPair("@id", organization.getOrganizationId().toString()));
		} else {
			formatIdentifier(pairs, "organizationId", organization, OrganizationSummary.class);
		}
		formatStatus(pairs, "status", organization);
		formatText(pairs, "displayName", organization);
		return pairs.isEmpty() ? null : new JsonObject(pairs);
	}

	public OrganizationSummary parseOrganizationSummary(JsonObject data) {
		Identifier organizationId = data.contains("organizationId") ? new Identifier(data.getString("organizationId")) : null;
		Status status = data.contains("status") ? Status.valueOf(data.getString("status").toUpperCase()) : null;
		String displayName = data.getString("displayName");
		return new OrganizationSummary(organizationId, status, displayName);
	}
	
	public JsonObject formatPersonDetails(PersonDetails person) {
		if (person == null)
			return null;
		List<JsonPair> pairs = new ArrayList<JsonPair>();
		if (linked) {
			pairs.add(new JsonPair("@context", getContext(PersonDetails.class)));
			pairs.add(new JsonPair("@type", getType(PersonDetails.class)));
			pairs.add(new JsonPair("@id", person.getPersonId().toString()));
		} else {
			formatIdentifier(pairs, "personId", person, PersonDetails.class);
		}
		formatIdentifier(pairs, "organizationId", person, OrganizationSummary.class);
		formatStatus(pairs, "status", person);
		formatText(pairs, "displayName", person);
		formatPersonName(pairs, "legalName", person);
		formatMailingAddress(pairs, "address", person);
		formatCommunication(pairs, "communication", person);
		formatBusinessPosition(pairs, "position", person);
		return pairs.isEmpty() ? null : new JsonObject(pairs);
	}

	public PersonDetails parsePersonDetails(JsonObject data) {
		Identifier personId = parseIdentifier("personId", data);
		Identifier organizationId = parseIdentifier("organizationId", data);
		Status status = parseStatus("status", data);
		String displayName = parseText("displayName", data);
		PersonName legalName = parsePersonName("legalName", data);
		MailingAddress address = parseMailingAddress("address", data);
		Communication communication = parseCommunication("communication", data);
		BusinessPosition unit = parseBusinessPosition("position", data);
		return new PersonDetails(personId, organizationId, status, displayName, legalName, address, communication, unit);
	}
	
	public JsonObject formatPersonSummary(PersonSummary person) {
		if (person == null)
			return null;
		List<JsonPair> pairs = new ArrayList<JsonPair>();
		if (linked) {
			pairs.add(new JsonPair("@context", getContext(PersonSummary.class)));
			pairs.add(new JsonPair("@type", getType(PersonSummary.class)));
			pairs.add(new JsonPair("@id", person.getOrganizationId().toString()));
		} else {
			formatIdentifier(pairs, "personId", person, OrganizationDetails.class);
		}
		formatIdentifier(pairs, "organizationId", person, OrganizationSummary.class);
		formatStatus(pairs, "status", person);
		formatText(pairs, "displayName", person);
		return pairs.isEmpty() ? null : new JsonObject(pairs);
	}

	public PersonSummary parsePersonSummary(JsonObject data) {
		Identifier personId = parseIdentifier("personId", data);
		Identifier organizationId = parseIdentifier("organizationId", data);
		Status status = parseStatus("status", data);
		String displayName = parseText("displayName", data);
		return new PersonSummary(personId, organizationId, status, displayName);
	}
	
	public User parseUser(String key, JsonObject parent) {
		JsonObject data = parent.getObject(key);
		if (data == null)
			return null;
		Identifier userId = parseIdentifier("userId", data);
		Identifier personId = parseIdentifier("personId", data);
		String username = parseText("username", data);
		List<String> roles = parseTexts("roles", data);
		return new User(userId, username, crm.findPersonSummary(personId), Status.ACTIVE, roles);
	}	

	public void formatText(List<JsonPair> parent, String key, Object obj) {
		if (obj != null) {
			try {
				Method m = obj.getClass().getMethod("get" + key.substring(0, 1).toUpperCase() + key.substring(1), new Class[] { });
				if (!m.getReturnType().equals(String.class))
					throw new IllegalArgumentException("Unexpected return codes, expected String but got: " + m.getReturnType().getName());
				String text = (String)m.invoke(obj, new Object[] { });
				if (text != null)
					parent.add(new JsonPair(key, new JsonText(text.toString())));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	public void formatIdentifier(List<JsonPair> parent, String key, Object obj, Class<?> type) {
		if (obj != null) {
			try {
				Method m = obj.getClass().getMethod("get" + key.substring(0, 1).toUpperCase() + key.substring(1), new Class[] { });
				if (!m.getReturnType().equals(Identifier.class))
					throw new IllegalArgumentException("Unexpected return codes, expected Identifier but got: " + m.getReturnType().getName());
				Identifier identifier = (Identifier)m.invoke(obj, new Object[] { });
				if (identifier != null) {
					if (linked) {
						List<JsonPair> pairs = new ArrayList<JsonPair>();
						pairs.add(new JsonPair("@context", getContext(type)));
						pairs.add(new JsonPair("@type", getType(type)));
						pairs.add(new JsonPair("@id", identifier.toString()));
						parent.add(new JsonPair(key, new JsonObject(pairs)));
					} else {
						parent.add(new JsonPair(key, new JsonText(identifier.toString())));
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	@SuppressWarnings("unchecked")
	public void formatTexts(List<JsonPair> parent, String key, Object obj, Class<?> type) {
		if (obj != null) {
			try {
				Method m = obj.getClass().getMethod("get" + key.substring(0, 1).toUpperCase() + key.substring(1), new Class[] { });
				if (!m.getReturnType().equals(List.class))
					throw new IllegalArgumentException("Unexpected return codes, expected List but got: " + m.getReturnType().getName());
				List<String> list = (List<String>)m.invoke(obj, new Object[] { });
				List<JsonElement> elements = new ArrayList<JsonElement>();
				if (list != null) {
					for (String text : list) {
						elements.add(new JsonText(text));
					}
				}
				parent.add(new JsonPair(key, new JsonArray(elements)));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	@SuppressWarnings("unchecked")
	public void formatIdentifiers(List<JsonPair> parent, String key, Object obj, Class<?> type) {
		if (obj != null) {
			try {
				Method m = obj.getClass().getMethod("get" + key.substring(0, 1).toUpperCase() + key.substring(1), new Class[] { });
				if (!m.getReturnType().equals(List.class))
					throw new IllegalArgumentException("Unexpected return codes, expected List but got: " + m.getReturnType().getName());
				List<Identifier> list = (List<Identifier>)m.invoke(obj, new Object[] { });
				List<JsonElement> elements = new ArrayList<JsonElement>();
				if (list != null) {
					for (Identifier identifier : list) {
						if (linked) {
							List<JsonPair> pairs = new ArrayList<JsonPair>();
							pairs.add(new JsonPair("@context", getContext(type)));
							pairs.add(new JsonPair("@type", getType(type)));
							pairs.add(new JsonPair("@id", identifier.toString()));
							elements.add(new JsonObject(pairs));
						} else {
							elements.add(new JsonText(identifier.toString()));
						}
					}
				}
				parent.add(new JsonPair(key, new JsonArray(elements)));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	public void formatStatus(List<JsonPair> parent, String key, Object obj) {
		if (obj != null) {
			try {
				Method m = obj.getClass().getMethod("get" + key.substring(0, 1).toUpperCase() + key.substring(1), new Class[] { });
				if (!m.getReturnType().equals(Status.class))
					throw new IllegalArgumentException("Unexpected return codes, expected Status but got: " + m.getReturnType().getName());
				Status status = (Status)m.invoke(obj, new Object[] { });
				if (status != null) {
					if (linked) {
						List<JsonPair> pairs = new ArrayList<JsonPair>();
						pairs.add(new JsonPair("@context", getContext(Status.class)));
						pairs.add(new JsonPair("@type", getType(Status.class)));
						pairs.add(new JsonPair("@value", status.getCode()));
						pairs.add(new JsonPair("@en", status.getName(Lang.ENGLISH)));
						pairs.add(new JsonPair("@fr", status.getName(Lang.FRENCH)));
						parent.add(new JsonPair(key, new JsonObject(pairs)));
					} else if (locale == null) {
						parent.add(new JsonPair(key, new JsonText(status.getCode())));
					} else {
						parent.add(new JsonPair(key, new JsonText(status.getName(locale))));
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	public void formatLocalized(List<JsonPair> parent, String key, Object obj) {
		if (obj != null) {
			try {
				Method m = obj.getClass().getMethod("get" + key.substring(0, 1).toUpperCase() + key.substring(1), new Class[] { });
				if (!m.getReturnType().equals(Localized.class))
					throw new IllegalArgumentException("Unexpected return codes, expected Status but got: " + m.getReturnType().getName());
				Localized localized = (Localized)m.invoke(obj, new Object[] { });
				if (localized != null) {
					parent.add(new JsonPair(key, new JsonText(localized.get(locale))));
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	public void formatSalutation(List<JsonPair> parent, String key, Object obj) {
		if (obj != null) {
			try {
				Method m = obj.getClass().getMethod("get" + key.substring(0, 1).toUpperCase() + key.substring(1), new Class[] { });
				if (!m.getReturnType().equals(String.class))
					throw new IllegalArgumentException("Unexpected return codes, expected String but got: " + m.getReturnType().getName());
				Salutation salutation = crm.findSalutationByLocalizedName(locale, (String)m.invoke(obj, new Object[] { }));
				if (linked) {
					if (salutation != null) {
						List<JsonPair> pairs = new ArrayList<JsonPair>();
						pairs.add(new JsonPair("@context", getContext(Salutation.class)));
						pairs.add(new JsonPair("@type", getType(Salutation.class)));
						pairs.add(new JsonPair("@value", salutation.getCode()));
						pairs.add(new JsonPair("@en", salutation.getName(Lang.ENGLISH)));
						pairs.add(new JsonPair("@fr", salutation.getName(Lang.FRENCH)));
						parent.add(new JsonPair(key, new JsonObject(pairs)));
					}
				} else if (locale == null) {
					if (salutation != null && salutation.getCode() != null)
						parent.add(new JsonPair(key, new JsonText(salutation.getCode())));
				} else {
					if (salutation != null && salutation.getName(locale) != null)
						parent.add(new JsonPair(key, new JsonText(salutation.getName(locale))));
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public void formatProvince(List<JsonPair> parent, String provinceKey, String countryKey, Object obj) {
		if (obj != null) {
			try {
				Method countryMethod = obj.getClass().getMethod("get" + countryKey.substring(0, 1).toUpperCase() + countryKey.substring(1), new Class[] { });
				if (!countryMethod.getReturnType().equals(String.class))
					throw new IllegalArgumentException("Unexpected return codes, expected String but got: " + countryMethod.getReturnType().getName());
				String countryValue = (String)countryMethod.invoke(obj, new Object[] { });
				
				Method provinceMethod = obj.getClass().getMethod("get" + provinceKey.substring(0, 1).toUpperCase() + provinceKey.substring(1), new Class[] { });
				if (!provinceMethod.getReturnType().equals(String.class))
					throw new IllegalArgumentException("Unexpected return codes, expected String but got: " + provinceMethod.getReturnType().getName());
				String provinceValue = (String)provinceMethod.invoke(obj, new Object[] { });
				Province province = crm.findProvinceByCode(provinceValue, countryValue);
				
				if (linked) {
					if (province != null) {
						List<JsonPair> pairs = new ArrayList<JsonPair>();
						pairs.add(new JsonPair("@context", getContext(Country.class)));
						pairs.add(new JsonPair("@type", getType(Country.class)));
						pairs.add(new JsonPair("@value", province.getCode()));
						pairs.add(new JsonPair("@en", province.getName(Lang.ENGLISH)));
						pairs.add(new JsonPair("@fr", province.getName(Lang.FRENCH)));
						parent.add(new JsonPair(provinceKey, new JsonObject(pairs)));
					}
				} else if (locale == null) {
					if (province != null && province.getCode() != null)
						parent.add(new JsonPair(provinceKey, new JsonText(province.getCode())));
				} else {
					if (province != null && province.getName(locale) != null)
						parent.add(new JsonPair(provinceKey, new JsonText(province.getName(locale))));
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public void formatCountry(List<JsonPair> parent, String key, Object obj) {
		if (obj != null) {
			try {
				Method m = obj.getClass().getMethod("get" + key.substring(0, 1).toUpperCase() + key.substring(1), new Class[] { });
				if (!m.getReturnType().equals(String.class))
					throw new IllegalArgumentException("Unexpected return codes, expected String but got: " + m.getReturnType().getName());
				Country country = crm.findCountryByCode((String)m.invoke(obj, new Object[] { }));
				if (linked) {
					if (country != null) {
						List<JsonPair> pairs = new ArrayList<JsonPair>();
						pairs.add(new JsonPair("@context", getContext(Country.class)));
						pairs.add(new JsonPair("@type", getType(Country.class)));
						pairs.add(new JsonPair("@value", country.getCode()));
						pairs.add(new JsonPair("@en", country.getName(Lang.ENGLISH)));
						pairs.add(new JsonPair("@fr", country.getName(Lang.FRENCH)));
						parent.add(new JsonPair(key, new JsonObject(pairs)));
					}
				} else if (locale == null) {
					if (country != null && country.getCode() != null)
						parent.add(new JsonPair(key, new JsonText(country.getCode())));
				} else {
					if (country != null && country.getName(locale) != null)
						parent.add(new JsonPair(key, new JsonText(country.getName(locale))));
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public void formatLanguage(List<JsonPair> parent, String key, Object obj) {
		if (obj != null) {
			try {
				Method m = obj.getClass().getMethod("get" + key.substring(0, 1).toUpperCase() + key.substring(1), new Class[] { });
				if (!m.getReturnType().equals(String.class))
					throw new IllegalArgumentException("Unexpected return codes, expected String but got: " + m.getReturnType().getName());
				Language language = crm.findLanguageByLocalizedName(locale, (String)m.invoke(obj, new Object[] { }));
				if (linked) {
					if (language != null) {
						List<JsonPair> pairs = new ArrayList<JsonPair>();
						pairs.add(new JsonPair("@context", getContext(Language.class)));
						pairs.add(new JsonPair("@type", getType(Language.class)));
						pairs.add(new JsonPair("@value", language.getCode()));
						pairs.add(new JsonPair("@en", language.getName(Lang.ENGLISH)));
						pairs.add(new JsonPair("@fr", language.getName(Lang.FRENCH)));
						parent.add(new JsonPair(key, new JsonObject(pairs)));
					}
				} else if (locale == null) {
					if (language != null && language.getCode() != null)
						parent.add(new JsonPair(key, new JsonText(language.getCode())));
				} else {
					if (language != null && language.getName(locale) != null)
						parent.add(new JsonPair(key, new JsonText(language.getName(locale))));
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	public void formatBusinessSector(List<JsonPair> parent, String key, Object obj) {
		if (obj != null) {
			try {
				Method m = obj.getClass().getMethod("get" + key.substring(0, 1).toUpperCase() + key.substring(1), new Class[] { });
				if (!m.getReturnType().equals(String.class))
					throw new IllegalArgumentException("Unexpected return codes, expected String but got: " + m.getReturnType().getName());
				BusinessSector sector = crm.findBusinessSectorByLocalizedName(locale, (String)m.invoke(obj, new Object[] { }));
				if (linked) {
					if (sector != null) {
						List<JsonPair> pairs = new ArrayList<JsonPair>();
						pairs.add(new JsonPair("@context", getContext(BusinessSector.class)));
						pairs.add(new JsonPair("@type", getType(BusinessSector.class)));
						pairs.add(new JsonPair("@value", sector.getCode()));
						pairs.add(new JsonPair("@en", sector.getName(Lang.ENGLISH)));
						pairs.add(new JsonPair("@fr", sector.getName(Lang.FRENCH)));
						parent.add(new JsonPair(key, new JsonObject(pairs)));
					}
				} else if (locale == null) {
					if (sector != null && sector.getCode() != null)
						parent.add(new JsonPair(key, new JsonText(sector.getCode())));
				} else {
					if (sector != null && sector.getName(locale) != null)
						parent.add(new JsonPair(key, new JsonText(sector.getName(locale))));
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	public void formatBusinessUnit(List<JsonPair> parent, String key, Object obj) {
		if (obj != null) {
			try {
				Method m = obj.getClass().getMethod("get" + key.substring(0, 1).toUpperCase() + key.substring(1), new Class[] { });
				if (!m.getReturnType().equals(String.class))
					throw new IllegalArgumentException("Unexpected return codes, expected String but got: " + m.getReturnType().getName());
				BusinessUnit unit = (BusinessUnit)m.invoke(obj, new Object[] { });
				if (linked) {
					if (unit != null) {
						List<JsonPair> pairs = new ArrayList<JsonPair>();
						pairs.add(new JsonPair("@context", getContext(BusinessUnit.class)));
						pairs.add(new JsonPair("@type", getType(BusinessUnit.class)));
						pairs.add(new JsonPair("@value", unit.getCode()));
						pairs.add(new JsonPair("@en", unit.getName(Lang.ENGLISH)));
						pairs.add(new JsonPair("@fr", unit.getName(Lang.FRENCH)));
						parent.add(new JsonPair(key, new JsonObject(pairs)));
					}
				} else if (locale == null) {
					if (unit != null && unit.getCode() != null)
						parent.add(new JsonPair(key, new JsonText(unit.getCode())));
				} else {
					if (unit != null && unit.getName(locale) != null)
						parent.add(new JsonPair(key, new JsonText(unit.getName(locale))));
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	public void formatBusinessClassification(List<JsonPair> parent, String key, Object obj) {
		if (obj != null) {
			try {
				Method m = obj.getClass().getMethod("get" + key.substring(0, 1).toUpperCase() + key.substring(1), new Class[] { });
				if (!m.getReturnType().equals(String.class))
					throw new IllegalArgumentException("Unexpected return codes, expected String but got: " + m.getReturnType().getName());
				BusinessClassification classification = (BusinessClassification)m.invoke(obj, new Object[] { });
				if (linked) {
					if (classification != null) {
						List<JsonPair> pairs = new ArrayList<JsonPair>();
						pairs.add(new JsonPair("@context", getContext(BusinessClassification.class)));
						pairs.add(new JsonPair("@type", getType(BusinessClassification.class)));
						pairs.add(new JsonPair("@value", classification.getCode()));
						pairs.add(new JsonPair("@en", classification.getName(Lang.ENGLISH)));
						pairs.add(new JsonPair("@fr", classification.getName(Lang.FRENCH)));
						parent.add(new JsonPair(key, new JsonObject(pairs)));
					}
				} else if (locale == null) {
					if (classification != null && classification.getCode() != null)
						parent.add(new JsonPair(key, new JsonText(classification.getCode())));
				} else {
					if (classification != null && classification.getName(locale) != null)
						parent.add(new JsonPair(key, new JsonText(classification.getName(locale))));
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public void formatPersonName(List<JsonPair> parent, String key, Object obj) {
		if (obj != null) {
			try {
				Method m = obj.getClass().getMethod("get" + key.substring(0, 1).toUpperCase() + key.substring(1), new Class[] { });
				if (!m.getReturnType().equals(PersonName.class))
					throw new IllegalArgumentException("Unexpected return codes, expected PersonName but got: " + m.getReturnType().getName());
				PersonName name = (PersonName)m.invoke(obj, new Object[] { });
				List<JsonPair> pairs = new ArrayList<JsonPair>();
				if (linked) {
					pairs.add(new JsonPair("@context", getContext(PersonName.class)));
					pairs.add(new JsonPair("@type", getType(PersonName.class)));
				}
				formatSalutation(pairs, "salutation", name);
				formatText(pairs, "firstName", name);
				formatText(pairs, "middleName", name);
				formatText(pairs, "lastName", name);
				if (!pairs.isEmpty())
					parent.add(new JsonPair(key, new JsonObject(pairs)));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public void formatTelephone(List<JsonPair> parent, String key, Object obj) {
		if (obj != null) {
			try {
				Method m = obj.getClass().getMethod("get" + key.substring(0, 1).toUpperCase() + key.substring(1), new Class[] { });
				if (!m.getReturnType().equals(Telephone.class))
					throw new IllegalArgumentException("Unexpected return codes, expected Telephone but got: " + m.getReturnType().getName());
				Telephone telephone = (Telephone)m.invoke(obj, new Object[] { });
				List<JsonPair> pairs = new ArrayList<JsonPair>();
				if (linked) {
					pairs.add(new JsonPair("@context", getContext(Telephone.class)));
					pairs.add(new JsonPair("@type", getType(Telephone.class)));
				}
				formatText(pairs, "number", telephone);
				formatText(pairs, "extension", telephone);
				if (!pairs.isEmpty())
					parent.add(new JsonPair(key, new JsonObject(pairs)));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}


	public void formatMailingAddress(List<JsonPair> parent, String key, Object obj) {
		if (obj != null) {
			try {
				Method m = obj.getClass().getMethod("get" + key.substring(0, 1).toUpperCase() + key.substring(1), new Class[] { });
				if (!m.getReturnType().equals(MailingAddress.class))
					throw new IllegalArgumentException("Unexpected return codes, expected MailingAddress but got: " + m.getReturnType().getName());
				MailingAddress address = (MailingAddress)m.invoke(obj, new Object[] { });
				List<JsonPair> pairs = new ArrayList<JsonPair>();
				if (linked) {
					pairs.add(new JsonPair("@context", getContext(MailingAddress.class)));
					pairs.add(new JsonPair("@type", getType(MailingAddress.class)));
				}
				formatText(pairs, "street", address);
				formatText(pairs, "city", address);
				formatProvince(pairs, "province", "country", address);
				formatCountry(pairs, "country", address);
				formatText(pairs, "postalCode", address);
				if (!pairs.isEmpty())
					parent.add(new JsonPair(key, new JsonObject(pairs)));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public void formatCommunication(List<JsonPair> parent, String key, Object obj) {
		if (obj != null) {
			try {
				Method m = obj.getClass().getMethod("get" + key.substring(0, 1).toUpperCase() + key.substring(1), new Class[] { });
				if (!m.getReturnType().equals(Communication.class))
					throw new IllegalArgumentException("Unexpected return codes, expected Communication but got: " + m.getReturnType().getName());
				Communication communication = (Communication)m.invoke(obj, new Object[] { });
				List<JsonPair> pairs = new ArrayList<JsonPair>();
				if (linked) {
					pairs.add(new JsonPair("@context", getContext(Communication.class)));
					pairs.add(new JsonPair("@type", getType(Communication.class)));
				}
				formatText(pairs, "jobTitle", communication);
				formatLanguage(pairs, "language", communication);
				formatText(pairs, "email", communication);
				formatTelephone(pairs, "homePhone", communication);
				formatText(pairs, "faxNumber", communication);
				if (!pairs.isEmpty())
					parent.add(new JsonPair(key, new JsonObject(pairs)));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public void formatBusinessPosition(List<JsonPair> parent, String key, Object obj) {
		if (obj != null) {
			try {
				Method m = obj.getClass().getMethod("get" + key.substring(0, 1).toUpperCase() + key.substring(1), new Class[] { });
				if (!m.getReturnType().equals(BusinessPosition.class))
					throw new IllegalArgumentException("Unexpected return codes, expected BusinessPosition but got: " + m.getReturnType().getName());
				BusinessPosition position = (BusinessPosition)m.invoke(obj, new Object[] { });
				List<JsonPair> pairs = new ArrayList<JsonPair>();
				if (linked) {
					pairs.add(new JsonPair("@context", getContext(BusinessPosition.class)));
					pairs.add(new JsonPair("@type", getType(BusinessPosition.class)));
				}
				formatBusinessSector(pairs, "sector", position);
				formatBusinessUnit(pairs, "unit", position);
				formatBusinessClassification(pairs, "classification", position);
				if (!pairs.isEmpty())
					parent.add(new JsonPair(key, new JsonObject(pairs)));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public Identifier parseIdentifier(String key, JsonObject data) {
		if (data.contains(key, JsonText.class)) {
			return new Identifier(data.getString(key));
		} else if (data.contains(key, JsonObject.class)) {
			return new Identifier(data.getObject(key).getString("@id"));
		} else if (data.contains(key)) {
			throw new IllegalArgumentException("Unexpected data type for Identifier: " + data.get(key).getClass());
		} else if (data.contains("@id")) {
			return new Identifier(data.getString("@id"));
		}
		return null;
	}

	public List<Identifier> parseIdentifiers(String key, JsonObject data) {
		List<Identifier> ids = new ArrayList<Identifier>();
		for (JsonElement child : data.getArray(key).values()) {
			if (child instanceof JsonText) {
				ids.add(new Identifier(((JsonText)child).value()));
			} else if (child instanceof JsonObject) {
				ids.add(new Identifier(((JsonObject)child).getString("@id")));
			} else {
				throw new IllegalArgumentException("Unexpected data type for child: " + child.getClass());
			}
		}
		return ids;
	}
	
	public Status parseStatus(String key, JsonObject data) {
		if (data.contains(key, JsonText.class)) {
			return Status.valueOf(data.getString(key).toUpperCase());
		} else if (data.contains(key, JsonObject.class)) {
			JsonObject ld = data.getObject(key);
			if (!ld.getString("@type").equals("Status"))
				throw new IllegalArgumentException("Unexpected link data type for Status: " + ld.getString("@type"));
			return Status.valueOf(ld.getString("@value").toUpperCase());
		} else if (data.contains(key)) {
			throw new IllegalArgumentException("Unexpected data type for status: " + data.get(key).getClass());
		}
		return null;
	}
		
	public String parseText(String key, JsonObject data) {
		return data.contains(key) ? data.getString(key) : null;
	}
	
	public List<String> parseTexts(String key, JsonObject data) {
		List<String> list = new ArrayList<String>();
		for (JsonElement child : data.getArray(key).values()) {
			if (child instanceof JsonText) {
				list.add(((JsonText)child).value());
			} else if (child instanceof JsonObject) {
				list.add(((JsonObject)child).getString("@id"));
			} else {
				throw new IllegalArgumentException("Unexpected data type for child: " + child.getClass());
			}
		}
		return list;
	}
	
	public Salutation parseSalutation(String key, JsonObject data) {
		if (data.contains(key, JsonText.class)) {
			if (locale == null)
				return crm.findSalutationByCode(data.getString(key));
			return crm.findSalutationByLocalizedName(locale, data.getString(key));
		} else if (data.contains(key, JsonObject.class)) {
			JsonObject ld = data.getObject(key);
			if (!ld.getString("@type").equals("Salutation"))
				throw new IllegalArgumentException("Unexpected link data type for Salutation: " + ld.getString("@type"));
			return crm.findSalutationByCode(ld.getString("@value")); 
		}
		return null;
	}
	
	public Localized parseProvince(String key, JsonObject data) {
		if (data.contains(key, JsonText.class)) {
			if (locale == null)
				return new Localized(Lang.ROOT, data.getString(key));
			return crm.findCountryByLocalizedName(locale, data.getString(key));
		} else if (data.contains(key, JsonObject.class)) {
			JsonObject ld = data.getObject(key);
			if (!ld.getString("@type").equals("Country"))
				throw new IllegalArgumentException("Unexpected link data type for Country: " + ld.getString("@type"));
			return new Localized(Lang.ROOT, ld.getString("@value")); 
		}
		return null;
	}
	
	public Country parseCountry(String key, JsonObject data) {
		if (data.contains(key, JsonText.class)) {
			if (locale == null)
				return crm.findCountryByCode(data.getString(key));
			return crm.findCountryByLocalizedName(locale, data.getString(key));
		} else if (data.contains(key, JsonObject.class)) {
			JsonObject ld = data.getObject(key);
			if (!ld.getString("@type").equals("Country"))
				throw new IllegalArgumentException("Unexpected link data type for Country: " + ld.getString("@type"));
			return crm.findCountryByCode(ld.getString("@value")); 
		}
		return null;
	}
	
	public Language parseLanguage(String key, JsonObject data) {
		if (data.contains(key, JsonText.class)) {
			if (locale == null)
				return crm.findLanguageByCode(data.getString(key));
			return crm.findLanguageByLocalizedName(locale, data.getString(key));
		} else if (data.contains(key, JsonObject.class)) {
			JsonObject ld = data.getObject(key);
			if (!ld.getString("@type").equals("Language"))
				throw new IllegalArgumentException("Unexpected link data type for Language: " + ld.getString("@type"));
			return crm.findLanguageByCode(ld.getString("@value")); 
		}
		return null;
	}
	
	public BusinessSector parseBusinessSector(String key, JsonObject data) {
		if (data.contains(key, JsonText.class)) {
			if (locale == null)
				return crm.findBusinessSectorByCode(data.getString(key));
			return crm.findBusinessSectorByLocalizedName(locale, data.getString(key));
		} else if (data.contains(key, JsonObject.class)) {
			JsonObject ld = data.getObject(key);
			if (!ld.getString("@type").equals("BusinessSector"))
				throw new IllegalArgumentException("Unexpected link data type for BusinessSector: " + ld.getString("@type"));
			return crm.findBusinessSectorByCode(ld.getString("@value")); 
		}
		return null;
	}
	
	public BusinessUnit parseBusinessUnit(String key, JsonObject data) {
		if (data.contains(key, JsonText.class)) {
			if (locale == null)
				return crm.findBusinessUnitByCode(data.getString(key));
			return crm.findBusinessUnitByLocalizedName(locale, data.getString(key));
		} else if (data.contains(key, JsonObject.class)) {
			JsonObject ld = data.getObject(key);
			if (!ld.getString("@type").equals("BusinessUnit"))
				throw new IllegalArgumentException("Unexpected link data type for BusinessUnit: " + ld.getString("@type"));
			return crm.findBusinessUnitByCode(ld.getString("@value")); 
		}
		return null;
	}
	
	public BusinessClassification parseBusinessClassification(String key, JsonObject data) {
		if (data.contains(key, JsonText.class)) {
			if (locale == null)
				return crm.findBusinessClassificationByCode(data.getString(key));
			return crm.findBusinessClassificationByLocalizedName(locale, data.getString(key));
		} else if (data.contains(key, JsonObject.class)) {
			JsonObject ld = data.getObject(key);
			if (!ld.getString("@type").equals("BusinessClassification"))
				throw new IllegalArgumentException("Unexpected link data type for BusinessClassification: " + ld.getString("@type"));
			return crm.findBusinessClassificationByCode(ld.getString("@value")); 
		}
		return null;
	}
	
	public PersonName parsePersonName(String key, JsonObject parent) {
		JsonObject data = parent.getObject(key);
		if (data == null)
			return null;
		Salutation salutation = parseSalutation("salutation", data);
		String firstName = parseText("firstName", data);
		String middleName = parseText("middleName", data);
		String lastName = parseText("lastName", data);
		return new PersonName(salutation.getName(locale), firstName, middleName, lastName);
	}
	
	public Telephone parseTelephone(String key, JsonObject parent) {
		JsonObject data = parent.getObject(key);
		if (data == null)
			return null;
		String number = parseText("number", data);
		String extension = parseText("extension", data);
		return new Telephone(number, extension);
	}
	
	public MailingAddress parseMailingAddress(String key, JsonObject parent) {
		JsonObject data = parent.getObject(key);
		if (data == null)
			return null;
		String street = parseText("street", data);
		String city = parseText("city", data);
		//Localized province = parseProvince("province", data);
		String province = parseText("province", data);
		Country country = parseCountry("country", data);
		String postalCode = parseText("postalCode", data);
		return new MailingAddress(street, city, province, country.get(locale), postalCode);	
	}
	
	public Communication parseCommunication(String key, JsonObject parent) {
		JsonObject data = parent.getObject(key);
		if (data == null)
			return null;
		String email = parseText("email", data);
		String jobTitle = parseText("jobTitle", data);
		Language language = parseLanguage("language", data);
		Telephone homePhone = parseTelephone("homePhone", data);
		String faxNumber = parseText("faxNumber", data);
		return new Communication(jobTitle, language.get(locale), email, homePhone, faxNumber);
	}
	
	public BusinessPosition parseBusinessPosition(String key, JsonObject parent) {
		JsonObject data = parent.getObject(key);
		if (data == null)
			return null;
		BusinessSector sector = parseBusinessSector("sector", data);
		BusinessUnit unit = parseBusinessUnit("unit", data);
		BusinessClassification classification = parseBusinessClassification("classification", data);
		return new BusinessPosition(sector == null ? null : sector.getName(locale), unit == null ? null : unit.getName(locale), classification == null ? null : classification.getName(locale));
	}

}
