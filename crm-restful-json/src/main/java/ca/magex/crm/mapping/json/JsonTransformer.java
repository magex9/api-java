package ca.magex.crm.mapping.json;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

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
import ca.magex.crm.api.lookup.BusinessClassification;
import ca.magex.crm.api.lookup.BusinessSector;
import ca.magex.crm.api.lookup.BusinessUnit;
import ca.magex.crm.api.lookup.Country;
import ca.magex.crm.api.lookup.Language;
import ca.magex.crm.api.lookup.Salutation;
import ca.magex.crm.api.services.CrmServices;
import ca.magex.crm.api.system.Identifier;
import ca.magex.crm.api.system.Lang;
import ca.magex.crm.api.system.Role;
import ca.magex.crm.api.system.Status;
import ca.magex.crm.mapping.data.DataArray;
import ca.magex.crm.mapping.data.DataElement;
import ca.magex.crm.mapping.data.DataObject;
import ca.magex.crm.mapping.data.DataPair;
import ca.magex.crm.mapping.data.DataText;

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
	
	public DataObject formatLocationDetails(LocationDetails location) {
		if (location == null)
			return null;
		List<DataPair> pairs = new ArrayList<DataPair>();
		if (linked) {
			pairs.add(new DataPair("@context", getContext(LocationDetails.class)));
			pairs.add(new DataPair("@type", getType(LocationDetails.class)));
			pairs.add(new DataPair("@id", location.getLocationId().toString()));
		} else {
			formatIdentifer(pairs, "locationId", location, LocationSummary.class);
		}
		formatIdentifer(pairs, "organizationId", location, OrganizationSummary.class);
		formatStatus(pairs, "status", location);
		formatText(pairs, "reference", location);
		formatText(pairs, "displayName", location);
		formatMailingAddress(pairs, "address", location);
		return pairs.isEmpty() ? null : new DataObject(pairs);
	}

	public LocationDetails parseLocationDetails(DataObject data) {
		Identifier locationId = parseIdentifier("locationId", data);
		Identifier organizationId = parseIdentifier("organizationId", data);
		Status status = parseStatus("status", data);
		String reference = parseText("reference", data);
		String displayName = parseText("displayName", data);
		MailingAddress address = parseMailingAddress("address", data);
		return new LocationDetails(locationId, organizationId, status, reference, displayName, address);
	}
	
	public DataObject formatLocationSummary(LocationSummary location) {
		if (location == null)
			return null;
		List<DataPair> pairs = new ArrayList<DataPair>();
		if (linked) {
			pairs.add(new DataPair("@context", getContext(LocationSummary.class)));
			pairs.add(new DataPair("@type", getType(LocationSummary.class)));
			pairs.add(new DataPair("@id", location.getLocationId().toString()));
		} else {
			formatIdentifer(pairs, "locationId", location, LocationSummary.class);
		}
		formatIdentifer(pairs, "organizationId", location, OrganizationSummary.class);
		formatStatus(pairs, "status", location);
		formatText(pairs, "reference", location);
		formatText(pairs, "displayName", location);
		return pairs.isEmpty() ? null : new DataObject(pairs);
	}

	public LocationSummary parseLocationSummary(DataObject data) {
		Identifier locationId = parseIdentifier("locationId", data);
		Identifier organizationId = parseIdentifier("organizationId", data);
		Status status = parseStatus("status", data);
		String reference = parseText("reference", data);
		String displayName = parseText("displayName", data);
		return new LocationSummary(locationId, organizationId, status, reference, displayName);
	}
	
	public DataObject formatOrganizationDetails(OrganizationDetails organization) {
		if (organization == null)
			return null;
		List<DataPair> pairs = new ArrayList<DataPair>();
		if (linked) {
			pairs.add(new DataPair("@context", getContext(OrganizationDetails.class)));
			pairs.add(new DataPair("@type", getType(OrganizationDetails.class)));
			pairs.add(new DataPair("@id", organization.getOrganizationId().toString()));
		} else {
			formatIdentifer(pairs, "organizationId", organization, OrganizationDetails.class);
		}
		formatStatus(pairs, "status", organization);
		formatText(pairs, "displayName", organization);
		formatIdentifer(pairs, "mainLocationId", organization, LocationDetails.class);
		return pairs.isEmpty() ? null : new DataObject(pairs);
	}

	public OrganizationDetails parseOrganizationDetails(DataObject data) {
		Identifier organizationId = parseIdentifier("organizationId", data);
		Status status = parseStatus("status", data);
		String displayName = parseText("displayName", data);
		Identifier mainLocationId = parseIdentifier("mainLocationId", data);
		return new OrganizationDetails(organizationId, status, displayName, mainLocationId);
	}
	
	public DataObject formatOrganizationSummary(OrganizationSummary organization) {
		if (organization == null)
			return null;
		List<DataPair> pairs = new ArrayList<DataPair>();
		if (linked) {
			pairs.add(new DataPair("@context", getContext(OrganizationSummary.class)));
			pairs.add(new DataPair("@type", getType(OrganizationSummary.class)));
			pairs.add(new DataPair("@id", organization.getOrganizationId().toString()));
		} else {
			formatIdentifer(pairs, "organizationId", organization, OrganizationSummary.class);
		}
		formatStatus(pairs, "status", organization);
		formatText(pairs, "displayName", organization);
		return pairs.isEmpty() ? null : new DataObject(pairs);
	}

	public OrganizationSummary parseOrganizationSummary(DataObject data) {
		Identifier organizationId = data.contains("organizationId") ? new Identifier(data.getString("organizationId")) : null;
		Status status = data.contains("status") ? Status.valueOf(data.getString("status").toUpperCase()) : null;
		String displayName = data.getString("displayName");
		return new OrganizationSummary(organizationId, status, displayName);
	}
	
	public DataObject formatPersonDetails(PersonDetails person) {
		if (person == null)
			return null;
		List<DataPair> pairs = new ArrayList<DataPair>();
		if (linked) {
			pairs.add(new DataPair("@context", getContext(PersonDetails.class)));
			pairs.add(new DataPair("@type", getType(PersonDetails.class)));
			pairs.add(new DataPair("@id", person.getPersonId().toString()));
		} else {
			formatIdentifer(pairs, "personId", person, PersonDetails.class);
		}
		formatIdentifer(pairs, "organizationId", person, OrganizationSummary.class);
		formatStatus(pairs, "status", person);
		formatText(pairs, "displayName", person);
		formatPersonName(pairs, "legalName", person);
		formatMailingAddress(pairs, "address", person);
		formatCommunication(pairs, "communication", person);
		formatBusinessPosition(pairs, "position", person);
		formatUser(pairs, "user", person);
		return pairs.isEmpty() ? null : new DataObject(pairs);
	}

	public PersonDetails parsePersonDetails(DataObject data) {
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
	
	public DataObject formatPersonSummary(PersonSummary person) {
		if (person == null)
			return null;
		List<DataPair> pairs = new ArrayList<DataPair>();
		if (linked) {
			pairs.add(new DataPair("@context", getContext(PersonSummary.class)));
			pairs.add(new DataPair("@type", getType(PersonSummary.class)));
			pairs.add(new DataPair("@id", person.getOrganizationId().toString()));
		} else {
			formatIdentifer(pairs, "personId", person, OrganizationDetails.class);
		}
		formatIdentifer(pairs, "organizationId", person, OrganizationSummary.class);
		formatStatus(pairs, "status", person);
		formatText(pairs, "displayName", person);
		return pairs.isEmpty() ? null : new DataObject(pairs);
	}

	public PersonSummary parsePersonSummary(DataObject data) {
		Identifier personId = parseIdentifier("personId", data);
		Identifier organizationId = parseIdentifier("organizationId", data);
		Status status = parseStatus("status", data);
		String displayName = parseText("displayName", data);
		return new PersonSummary(personId, organizationId, status, displayName);
	}
	
	public User parseUser(String key, DataObject parent) {
		DataObject data = parent.getObject(key);
		if (data == null)
			return null;
		Identifier userId = parseIdentifier("userId", data);
		Identifier personId = parseIdentifier("personId", data);
		Identifier organizationId = parseIdentifier("organizationId", data);
		String userName = parseText("userName", data);
		List<String> roles = data.contains("roles") ? 
			data.getArray("roles").stream()
				.map(r -> parseRole(r).getName(locale))
				.collect(Collectors.toList()) :
			new ArrayList<String>();
		return new User(userId, organizationId, personId, userName, roles);
	}	

	public void formatText(List<DataPair> parent, String key, Object obj) {
		if (obj != null) {
			try {
				Method m = obj.getClass().getMethod("get" + key.substring(0, 1).toUpperCase() + key.substring(1), new Class[] { });
				if (!m.getReturnType().equals(String.class))
					throw new IllegalArgumentException("Unexpected return codes, expected String but got: " + m.getReturnType().getName());
				String text = (String)m.invoke(obj, new Object[] { });
				if (text != null)
					parent.add(new DataPair(key, new DataText(text.toString())));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	public void formatIdentifer(List<DataPair> parent, String key, Object obj, Class<?> type) {
		if (obj != null) {
			try {
				Method m = obj.getClass().getMethod("get" + key.substring(0, 1).toUpperCase() + key.substring(1), new Class[] { });
				if (!m.getReturnType().equals(Identifier.class))
					throw new IllegalArgumentException("Unexpected return codes, expected Identifier but got: " + m.getReturnType().getName());
				Identifier identifier = (Identifier)m.invoke(obj, new Object[] { });
				if (identifier != null) {
					if (linked) {
						List<DataPair> pairs = new ArrayList<DataPair>();
						pairs.add(new DataPair("@context", getContext(type)));
						pairs.add(new DataPair("@type", getType(type)));
						pairs.add(new DataPair("@id", identifier.toString()));
						parent.add(new DataPair(key, new DataObject(pairs)));
					} else {
						parent.add(new DataPair(key, new DataText(identifier.toString())));
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	public void formatStatus(List<DataPair> parent, String key, Object obj) {
		if (obj != null) {
			try {
				Method m = obj.getClass().getMethod("get" + key.substring(0, 1).toUpperCase() + key.substring(1), new Class[] { });
				if (!m.getReturnType().equals(Status.class))
					throw new IllegalArgumentException("Unexpected return codes, expected Status but got: " + m.getReturnType().getName());
				Status status = (Status)m.invoke(obj, new Object[] { });
				if (status != null) {
					if (linked) {
						List<DataPair> pairs = new ArrayList<DataPair>();
						pairs.add(new DataPair("@context", getContext(Status.class)));
						pairs.add(new DataPair("@type", getType(Status.class)));
						pairs.add(new DataPair("@value", status.getCode()));
						pairs.add(new DataPair("@en", status.getName(Lang.ENGLISH)));
						pairs.add(new DataPair("@fr", status.getName(Lang.FRENCH)));
						parent.add(new DataPair(key, new DataObject(pairs)));
					} else if (locale == null) {
						parent.add(new DataPair(key, new DataText(status.getCode())));
					} else {
						parent.add(new DataPair(key, new DataText(status.getName(locale))));
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	public void formatSalutation(List<DataPair> parent, String key, Object obj) {
		if (obj != null) {
			try {
				Method m = obj.getClass().getMethod("get" + key.substring(0, 1).toUpperCase() + key.substring(1), new Class[] { });
				if (!m.getReturnType().equals(String.class))
					throw new IllegalArgumentException("Unexpected return codes, expected String but got: " + m.getReturnType().getName());
				Salutation salutation = crm.findSalutationByLocalizedName(locale, (String)m.invoke(obj, new Object[] { }));
				if (linked) {
					if (salutation != null) {
						List<DataPair> pairs = new ArrayList<DataPair>();
						pairs.add(new DataPair("@context", getContext(Salutation.class)));
						pairs.add(new DataPair("@type", getType(Salutation.class)));
						pairs.add(new DataPair("@value", salutation.getCode()));
						pairs.add(new DataPair("@en", salutation.getName(Lang.ENGLISH)));
						pairs.add(new DataPair("@fr", salutation.getName(Lang.FRENCH)));
						parent.add(new DataPair(key, new DataObject(pairs)));
					}
				} else if (locale == null) {
					if (salutation != null && salutation.getCode() != null)
						parent.add(new DataPair(key, new DataText(salutation.getCode())));
				} else {
					if (salutation != null && salutation.getName(locale) != null)
						parent.add(new DataPair(key, new DataText(salutation.getName(locale))));
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public void formatCountry(List<DataPair> parent, String key, Object obj) {
		if (obj != null) {
			try {
				Method m = obj.getClass().getMethod("get" + key.substring(0, 1).toUpperCase() + key.substring(1), new Class[] { });
				if (!m.getReturnType().equals(String.class))
					throw new IllegalArgumentException("Unexpected return codes, expected String but got: " + m.getReturnType().getName());
				Country country = crm.findCountryByLocalizedName(locale, (String)m.invoke(obj, new Object[] { }));
				if (linked) {
					if (country != null) {
						List<DataPair> pairs = new ArrayList<DataPair>();
						pairs.add(new DataPair("@context", getContext(Country.class)));
						pairs.add(new DataPair("@type", getType(Country.class)));
						pairs.add(new DataPair("@value", country.getCode()));
						pairs.add(new DataPair("@en", country.getName(Lang.ENGLISH)));
						pairs.add(new DataPair("@fr", country.getName(Lang.FRENCH)));
						parent.add(new DataPair(key, new DataObject(pairs)));
					}
				} else if (locale == null) {
					if (country != null && country.getCode() != null)
						parent.add(new DataPair(key, new DataText(country.getCode())));
				} else {
					if (country != null && country.getName(locale) != null)
						parent.add(new DataPair(key, new DataText(country.getName(locale))));
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public void formatLanguage(List<DataPair> parent, String key, Object obj) {
		if (obj != null) {
			try {
				Method m = obj.getClass().getMethod("get" + key.substring(0, 1).toUpperCase() + key.substring(1), new Class[] { });
				if (!m.getReturnType().equals(String.class))
					throw new IllegalArgumentException("Unexpected return codes, expected String but got: " + m.getReturnType().getName());
				Language language = crm.findLanguageByLocalizedName(locale, (String)m.invoke(obj, new Object[] { }));
				if (linked) {
					if (language != null) {
						List<DataPair> pairs = new ArrayList<DataPair>();
						pairs.add(new DataPair("@context", getContext(Language.class)));
						pairs.add(new DataPair("@type", getType(Language.class)));
						pairs.add(new DataPair("@value", language.getCode()));
						pairs.add(new DataPair("@en", language.getName(Lang.ENGLISH)));
						pairs.add(new DataPair("@fr", language.getName(Lang.FRENCH)));
						parent.add(new DataPair(key, new DataObject(pairs)));
					}
				} else if (locale == null) {
					if (language != null && language.getCode() != null)
						parent.add(new DataPair(key, new DataText(language.getCode())));
				} else {
					if (language != null && language.getName(locale) != null)
						parent.add(new DataPair(key, new DataText(language.getName(locale))));
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	public void formatBusinessSector(List<DataPair> parent, String key, Object obj) {
		if (obj != null) {
			try {
				Method m = obj.getClass().getMethod("get" + key.substring(0, 1).toUpperCase() + key.substring(1), new Class[] { });
				if (!m.getReturnType().equals(String.class))
					throw new IllegalArgumentException("Unexpected return codes, expected String but got: " + m.getReturnType().getName());
				BusinessSector sector = crm.findBusinessSectorByLocalizedName(locale, (String)m.invoke(obj, new Object[] { }));
				if (linked) {
					if (sector != null) {
						List<DataPair> pairs = new ArrayList<DataPair>();
						pairs.add(new DataPair("@context", getContext(BusinessSector.class)));
						pairs.add(new DataPair("@type", getType(BusinessSector.class)));
						pairs.add(new DataPair("@value", sector.getCode()));
						pairs.add(new DataPair("@en", sector.getName(Lang.ENGLISH)));
						pairs.add(new DataPair("@fr", sector.getName(Lang.FRENCH)));
						parent.add(new DataPair(key, new DataObject(pairs)));
					}
				} else if (locale == null) {
					if (sector != null && sector.getCode() != null)
						parent.add(new DataPair(key, new DataText(sector.getCode())));
				} else {
					if (sector != null && sector.getName(locale) != null)
						parent.add(new DataPair(key, new DataText(sector.getName(locale))));
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	public void formatBusinessUnit(List<DataPair> parent, String key, Object obj) {
		if (obj != null) {
			try {
				Method m = obj.getClass().getMethod("get" + key.substring(0, 1).toUpperCase() + key.substring(1), new Class[] { });
				if (!m.getReturnType().equals(String.class))
					throw new IllegalArgumentException("Unexpected return codes, expected String but got: " + m.getReturnType().getName());
				BusinessUnit unit = (BusinessUnit)m.invoke(obj, new Object[] { });
				if (linked) {
					if (unit != null) {
						List<DataPair> pairs = new ArrayList<DataPair>();
						pairs.add(new DataPair("@context", getContext(BusinessUnit.class)));
						pairs.add(new DataPair("@type", getType(BusinessUnit.class)));
						pairs.add(new DataPair("@value", unit.getCode()));
						pairs.add(new DataPair("@en", unit.getName(Lang.ENGLISH)));
						pairs.add(new DataPair("@fr", unit.getName(Lang.FRENCH)));
						parent.add(new DataPair(key, new DataObject(pairs)));
					}
				} else if (locale == null) {
					if (unit != null && unit.getCode() != null)
						parent.add(new DataPair(key, new DataText(unit.getCode())));
				} else {
					if (unit != null && unit.getName(locale) != null)
						parent.add(new DataPair(key, new DataText(unit.getName(locale))));
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	public void formatBusinessClassification(List<DataPair> parent, String key, Object obj) {
		if (obj != null) {
			try {
				Method m = obj.getClass().getMethod("get" + key.substring(0, 1).toUpperCase() + key.substring(1), new Class[] { });
				if (!m.getReturnType().equals(String.class))
					throw new IllegalArgumentException("Unexpected return codes, expected String but got: " + m.getReturnType().getName());
				BusinessClassification classification = (BusinessClassification)m.invoke(obj, new Object[] { });
				if (linked) {
					if (classification != null) {
						List<DataPair> pairs = new ArrayList<DataPair>();
						pairs.add(new DataPair("@context", getContext(BusinessClassification.class)));
						pairs.add(new DataPair("@type", getType(BusinessClassification.class)));
						pairs.add(new DataPair("@value", classification.getCode()));
						pairs.add(new DataPair("@en", classification.getName(Lang.ENGLISH)));
						pairs.add(new DataPair("@fr", classification.getName(Lang.FRENCH)));
						parent.add(new DataPair(key, new DataObject(pairs)));
					}
				} else if (locale == null) {
					if (classification != null && classification.getCode() != null)
						parent.add(new DataPair(key, new DataText(classification.getCode())));
				} else {
					if (classification != null && classification.getName(locale) != null)
						parent.add(new DataPair(key, new DataText(classification.getName(locale))));
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public void formatPersonName(List<DataPair> parent, String key, Object obj) {
		if (obj != null) {
			try {
				Method m = obj.getClass().getMethod("get" + key.substring(0, 1).toUpperCase() + key.substring(1), new Class[] { });
				if (!m.getReturnType().equals(PersonName.class))
					throw new IllegalArgumentException("Unexpected return codes, expected PersonName but got: " + m.getReturnType().getName());
				PersonName name = (PersonName)m.invoke(obj, new Object[] { });
				List<DataPair> pairs = new ArrayList<DataPair>();
				if (linked) {
					pairs.add(new DataPair("@context", getContext(PersonName.class)));
					pairs.add(new DataPair("@type", getType(PersonName.class)));
				}
				formatSalutation(pairs, "salutation", name);
				formatText(pairs, "firstName", name);
				formatText(pairs, "middleName", name);
				formatText(pairs, "lastName", name);
				if (!pairs.isEmpty())
					parent.add(new DataPair(key, new DataObject(pairs)));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public void formatTelephone(List<DataPair> parent, String key, Object obj) {
		if (obj != null) {
			try {
				Method m = obj.getClass().getMethod("get" + key.substring(0, 1).toUpperCase() + key.substring(1), new Class[] { });
				if (!m.getReturnType().equals(Telephone.class))
					throw new IllegalArgumentException("Unexpected return codes, expected Telephone but got: " + m.getReturnType().getName());
				Telephone telephone = (Telephone)m.invoke(obj, new Object[] { });
				List<DataPair> pairs = new ArrayList<DataPair>();
				if (linked) {
					pairs.add(new DataPair("@context", getContext(Telephone.class)));
					pairs.add(new DataPair("@type", getType(Telephone.class)));
				}
				formatText(pairs, "number", telephone);
				formatText(pairs, "extension", telephone);
				if (!pairs.isEmpty())
					parent.add(new DataPair(key, new DataObject(pairs)));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}


	public void formatMailingAddress(List<DataPair> parent, String key, Object obj) {
		if (obj != null) {
			try {
				Method m = obj.getClass().getMethod("get" + key.substring(0, 1).toUpperCase() + key.substring(1), new Class[] { });
				if (!m.getReturnType().equals(MailingAddress.class))
					throw new IllegalArgumentException("Unexpected return codes, expected MailingAddress but got: " + m.getReturnType().getName());
				MailingAddress address = (MailingAddress)m.invoke(obj, new Object[] { });
				List<DataPair> pairs = new ArrayList<DataPair>();
				if (linked) {
					pairs.add(new DataPair("@context", getContext(MailingAddress.class)));
					pairs.add(new DataPair("@type", getType(MailingAddress.class)));
				}
				formatText(pairs, "street", address);
				formatText(pairs, "city", address);
				formatText(pairs, "province", address);
				formatCountry(pairs, "country", address);
				formatText(pairs, "postalCode", address);
				if (!pairs.isEmpty())
					parent.add(new DataPair(key, new DataObject(pairs)));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public void formatCommunication(List<DataPair> parent, String key, Object obj) {
		if (obj != null) {
			try {
				Method m = obj.getClass().getMethod("get" + key.substring(0, 1).toUpperCase() + key.substring(1), new Class[] { });
				if (!m.getReturnType().equals(Communication.class))
					throw new IllegalArgumentException("Unexpected return codes, expected Communication but got: " + m.getReturnType().getName());
				Communication communication = (Communication)m.invoke(obj, new Object[] { });
				List<DataPair> pairs = new ArrayList<DataPair>();
				if (linked) {
					pairs.add(new DataPair("@context", getContext(Communication.class)));
					pairs.add(new DataPair("@type", getType(Communication.class)));
				}
				formatText(pairs, "email", communication);
				formatText(pairs, "jobTitle", communication);
				formatLanguage(pairs, "language", communication);
				formatTelephone(pairs, "homePhone", communication);
				formatText(pairs, "faxNumber", communication);
				if (!pairs.isEmpty())
					parent.add(new DataPair(key, new DataObject(pairs)));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public void formatBusinessPosition(List<DataPair> parent, String key, Object obj) {
		if (obj != null) {
			try {
				Method m = obj.getClass().getMethod("get" + key.substring(0, 1).toUpperCase() + key.substring(1), new Class[] { });
				if (!m.getReturnType().equals(BusinessPosition.class))
					throw new IllegalArgumentException("Unexpected return codes, expected BusinessPosition but got: " + m.getReturnType().getName());
				BusinessPosition position = (BusinessPosition)m.invoke(obj, new Object[] { });
				List<DataPair> pairs = new ArrayList<DataPair>();
				if (linked) {
					pairs.add(new DataPair("@context", getContext(BusinessPosition.class)));
					pairs.add(new DataPair("@type", getType(BusinessPosition.class)));
				}
				formatBusinessSector(pairs, "sector", position);
				formatBusinessUnit(pairs, "unit", position);
				formatBusinessClassification(pairs, "classification", position);
				if (!pairs.isEmpty())
					parent.add(new DataPair(key, new DataObject(pairs)));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public void formatUser(List<DataPair> parent, String key, Object obj) {
		if (obj != null) {
			try {
				Method m = obj.getClass().getMethod("get" + key.substring(0, 1).toUpperCase() + key.substring(1), new Class[] { });
				if (!m.getReturnType().equals(User.class))
					throw new IllegalArgumentException("Unexpected return codes, expected User but got: " + m.getReturnType().getName());
				User user = (User)m.invoke(obj, new Object[] { });
				List<DataPair> pairs = new ArrayList<DataPair>();
				if (linked) {
					pairs.add(new DataPair("@context", getContext(User.class)));
					pairs.add(new DataPair("@type", getType(User.class)));
				}
				formatText(pairs, "userName", user);
				if (user.getRoles() != null) {
					if (linked) {
						pairs.add(new DataPair("roles", new DataArray(user.getRoles().stream()
							.map(r -> {
								Role role = crm.findRoleByLocalizedName(locale, r);
								List<DataPair> elements = new ArrayList<DataPair>();
								elements.add(new DataPair("@context", getContext(Role.class)));
								elements.add(new DataPair("@type", getType(Role.class)));
								elements.add(new DataPair("@value", role.getCode()));
								elements.add(new DataPair("@en", role.getName(Lang.ENGLISH)));
								elements.add(new DataPair("@fr", role.getName(Lang.FRENCH)));
								return new DataObject(elements);	
							}).collect(Collectors.toList()))));
					} else {
						pairs.add(new DataPair("roles", new DataArray(user.getRoles().stream()
							.map(r -> new DataText(crm.findRoleByLocalizedName(locale, r).getName(locale)))
							.collect(Collectors.toList())
						)));
					}
				}
				if (!pairs.isEmpty())
					parent.add(new DataPair(key, new DataObject(pairs)));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public Identifier parseIdentifier(String key, DataObject data) {
		if (data.contains(key, DataText.class)) {
			return new Identifier(data.getString(key));
		} else if (data.contains(key, DataObject.class)) {
			return new Identifier(data.getObject(key).getString("@id"));
		} else if (data.contains(key)) {
			throw new IllegalArgumentException("Unexpected data type for status: " + data.get(key).getClass());
		} else if (data.contains("@id")) {
			return new Identifier(data.getString("@id"));
		}
		return null;
	}
	
	public Status parseStatus(String key, DataObject data) {
		if (data.contains(key, DataText.class)) {
			return Status.valueOf(data.getString(key).toUpperCase());
		} else if (data.contains(key, DataObject.class)) {
			DataObject ld = data.getObject(key);
			if (!ld.getString("@type").equals("Status"))
				throw new IllegalArgumentException("Unexpected link data type for Status: " + ld.getString("@type"));
			return Status.valueOf(ld.getString("@value").toUpperCase());
		} else if (data.contains(key)) {
			throw new IllegalArgumentException("Unexpected data type for status: " + data.get(key).getClass());
		}
		return null;
	}
		
	public String parseText(String key, DataObject data) {
		return data.contains(key) ? data.getString(key) : null;
	}
	
	public Salutation parseSalutation(String key, DataObject data) {
		if (data.contains(key, DataText.class)) {
			if (locale == null)
				return crm.findSalutationByCode(data.getString(key));
			return crm.findSalutationByLocalizedName(locale, data.getString(key));
		} else if (data.contains(key, DataObject.class)) {
			DataObject ld = data.getObject(key);
			if (!ld.getString("@type").equals("Salutation"))
				throw new IllegalArgumentException("Unexpected link data type for Salutation: " + ld.getString("@type"));
			return crm.findSalutationByCode(ld.getString("@value")); 
		}
		return null;
	}
	
	public Country parseCountry(String key, DataObject data) {
		if (data.contains(key, DataText.class)) {
			if (locale == null)
				return crm.findCountryByCode(data.getString(key));
			return crm.findCountryByLocalizedName(locale, data.getString(key));
		} else if (data.contains(key, DataObject.class)) {
			DataObject ld = data.getObject(key);
			if (!ld.getString("@type").equals("Country"))
				throw new IllegalArgumentException("Unexpected link data type for Country: " + ld.getString("@type"));
			return crm.findCountryByCode(ld.getString("@value")); 
		}
		return null;
	}
	
	public Language parseLanguage(String key, DataObject data) {
		if (data.contains(key, DataText.class)) {
			if (locale == null)
				return crm.findLanguageByCode(data.getString(key));
			return crm.findLanguageByLocalizedName(locale, data.getString(key));
		} else if (data.contains(key, DataObject.class)) {
			DataObject ld = data.getObject(key);
			if (!ld.getString("@type").equals("Language"))
				throw new IllegalArgumentException("Unexpected link data type for Language: " + ld.getString("@type"));
			return crm.findLanguageByCode(ld.getString("@value")); 
		}
		return null;
	}
	
	public BusinessSector parseBusinessSector(String key, DataObject data) {
		if (data.contains(key, DataText.class)) {
			if (locale == null)
				return crm.findBusinessSectorByCode(data.getString(key));
			return crm.findBusinessSectorByLocalizedName(locale, data.getString(key));
		} else if (data.contains(key, DataObject.class)) {
			DataObject ld = data.getObject(key);
			if (!ld.getString("@type").equals("BusinessSector"))
				throw new IllegalArgumentException("Unexpected link data type for BusinessSector: " + ld.getString("@type"));
			return crm.findBusinessSectorByCode(ld.getString("@value")); 
		}
		return null;
	}
	
	public BusinessUnit parseBusinessUnit(String key, DataObject data) {
		if (data.contains(key, DataText.class)) {
			if (locale == null)
				return crm.findBusinessUnitByCode(data.getString(key));
			return crm.findBusinessUnitByLocalizedName(locale, data.getString(key));
		} else if (data.contains(key, DataObject.class)) {
			DataObject ld = data.getObject(key);
			if (!ld.getString("@type").equals("BusinessUnit"))
				throw new IllegalArgumentException("Unexpected link data type for BusinessUnit: " + ld.getString("@type"));
			return crm.findBusinessUnitByCode(ld.getString("@value")); 
		}
		return null;
	}
	
	public BusinessClassification parseBusinessClassification(String key, DataObject data) {
		if (data.contains(key, DataText.class)) {
			if (locale == null)
				return crm.findBusinessClassificationByCode(data.getString(key));
			return crm.findBusinessClassificationByLocalizedName(locale, data.getString(key));
		} else if (data.contains(key, DataObject.class)) {
			DataObject ld = data.getObject(key);
			if (!ld.getString("@type").equals("BusinessClassification"))
				throw new IllegalArgumentException("Unexpected link data type for BusinessClassification: " + ld.getString("@type"));
			return crm.findBusinessClassificationByCode(ld.getString("@value")); 
		}
		return null;
	}
	
	public PersonName parsePersonName(String key, DataObject parent) {
		DataObject data = parent.getObject(key);
		if (data == null)
			return null;
		Salutation salutation = parseSalutation("salutation", data);
		String firstName = parseText("firstName", data);
		String middleName = parseText("middleName", data);
		String lastName = parseText("lastName", data);
		return new PersonName(salutation.getName(locale), firstName, middleName, lastName);
	}
	
	public Telephone parseTelephone(String key, DataObject parent) {
		DataObject data = parent.getObject(key);
		if (data == null)
			return null;
		String number = parseText("number", data);
		String extension = parseText("extension", data);
		return new Telephone(number, extension);
	}
	
	public MailingAddress parseMailingAddress(String key, DataObject parent) {
		DataObject data = parent.getObject(key);
		if (data == null)
			return null;
		String street = parseText("street", data);
		String city = parseText("city", data);
		String province = parseText("province", data);
		Country country = parseCountry("country", data);
		String postalCode = parseText("postalCode", data);
		return new MailingAddress(street, city, province, country.getName(locale), postalCode);	
	}
	
	public Communication parseCommunication(String key, DataObject parent) {
		DataObject data = parent.getObject(key);
		if (data == null)
			return null;
		String email = parseText("email", data);
		String jobTitle = parseText("jobTitle", data);
		Language language = parseLanguage("language", data);
		Telephone homePhone = parseTelephone("homePhone", data);
		String faxNumber = parseText("faxNumber", data);
		return new Communication(jobTitle, language.getName(locale), email, homePhone, faxNumber);
	}
	
	public BusinessPosition parseBusinessPosition(String key, DataObject parent) {
		DataObject data = parent.getObject(key);
		if (data == null)
			return null;
		BusinessSector sector = parseBusinessSector("sector", data);
		BusinessUnit unit = parseBusinessUnit("unit", data);
		BusinessClassification classification = parseBusinessClassification("classification", data);
		return new BusinessPosition(sector == null ? null : sector.getName(locale), unit == null ? null : unit.getName(locale), classification == null ? null : classification.getName(locale));
	}
	
	public Role parseRole(DataElement data) {
		if (data instanceof DataText) {
			return crm.findRoleByLocalizedName(locale, ((DataText)data).value());
		} else if (data instanceof DataObject) {
			DataObject ld = (DataObject)data;
			if (!ld.getString("@type").equals("Role"))
				throw new IllegalArgumentException("Unexpected link data type for Role: " + ld.getString("@type"));
			return crm.findRoleByCode(ld.getString("@value")); 
		}
		return null;
	}

}
