package ca.magex.crm.mapping.json;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

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
import ca.magex.crm.api.system.Role;
import ca.magex.crm.api.system.Status;
import ca.magex.crm.mapping.data.DataArray;
import ca.magex.crm.mapping.data.DataNumber;
import ca.magex.crm.mapping.data.DataObject;
import ca.magex.crm.mapping.data.DataPair;
import ca.magex.crm.mapping.data.DataText;

public class JsonTransformer {
	
	private Locale locale;
	
	protected final CrmServices crm;
	
	public JsonTransformer(CrmServices crm, Locale locale) {
		this.crm = crm;
		this.locale = locale;
	}
	
	public DataObject formatLocationDetails(LocationDetails location) {
		List<DataPair> pairs = new ArrayList<DataPair>();
		addIdentifer(pairs, "locationId", location);
		addIdentifer(pairs, "organizationId", location);
		addStatus(pairs, "status", location);
		addText(pairs, "reference", location);
		addText(pairs, "displayName", location);
		addMailingAddress(pairs, "address", location);
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
		List<DataPair> pairs = new ArrayList<DataPair>();
		addIdentifer(pairs, "locationId", location);
		addIdentifer(pairs, "organizationId", location);
		addStatus(pairs, "status", location);
		addText(pairs, "reference", location);
		addText(pairs, "displayName", location);
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
		List<DataPair> pairs = new ArrayList<DataPair>();
		addIdentifer(pairs, "organizationId", organization);
		addStatus(pairs, "status", organization);
		addText(pairs, "displayName", organization);
		addIdentifer(pairs, "mainLocationId", organization);
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
		List<DataPair> pairs = new ArrayList<DataPair>();
		if (organization.getOrganizationId() != null)
			pairs.add(new DataPair("organizationId", new DataText(organization.getOrganizationId().toString())));
		if (organization.getStatus() != null)
			pairs.add(new DataPair("status", new DataText(organization.getStatus().toString().toLowerCase())));
		if (StringUtils.isNotBlank(organization.getDisplayName()))
			pairs.add(new DataPair("displayName", new DataText(organization.getDisplayName())));
		if (pairs.isEmpty())
			return null;
		return new DataObject(pairs);
	}

	public OrganizationSummary parseOrganizationSummary(DataObject data) {
		Identifier organizationId = data.contains("organizationId") ? new Identifier(data.getString("organizationId")) : null;
		Status status = data.contains("status") ? Status.valueOf(data.getString("status").toUpperCase()) : null;
		String displayName = data.getString("displayName");
		return new OrganizationSummary(organizationId, status, displayName);
	}
	
	public DataObject formatPersonDetails(PersonDetails person) {
		List<DataPair> pairs = new ArrayList<DataPair>();
		addIdentifer(pairs, "personId", person);
		addIdentifer(pairs, "organizationId", person);
		addStatus(pairs, "status", person);
		addText(pairs, "displayName", person);
		addPersonName(pairs, "legalName", person);
		addMailingAddress(pairs, "address", person);
		addCommunication(pairs, "communication", person);
		addBusinessPosition(pairs, "position", person);
		addUser(pairs, "user", person);
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
		User user = parseUser("user", data);
		return new PersonDetails(personId, organizationId, status, displayName, legalName, address, communication, unit, user);
	}
	
	public DataObject formatPersonSummary(PersonSummary person) {
		List<DataPair> pairs = new ArrayList<DataPair>();
		addIdentifer(pairs, "personId", person);
		addIdentifer(pairs, "organizationId", person);
		addStatus(pairs, "status", person);
		addText(pairs, "displayName", person);
		return pairs.isEmpty() ? null : new DataObject(pairs);
	}

	public PersonSummary parsePersonSummary(DataObject data) {
		Identifier personId = parseIdentifier("personId", data);
		Identifier organizationId = parseIdentifier("organizationId", data);
		Status status = parseStatus("status", data);
		String displayName = parseText("displayName", data);
		return new PersonSummary(personId, organizationId, status, displayName);
	}	

	public void addText(List<DataPair> parent, String key, Object obj) {
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
	
	public void addIdentifer(List<DataPair> parent, String key, Object obj) {
		if (obj != null) {
			try {
				Method m = obj.getClass().getMethod("get" + key.substring(0, 1).toUpperCase() + key.substring(1), new Class[] { });
				if (!m.getReturnType().equals(Identifier.class))
					throw new IllegalArgumentException("Unexpected return codes, expected Identifier but got: " + m.getReturnType().getName());
				Identifier identifier = (Identifier)m.invoke(obj, new Object[] { });
				if (identifier != null)
					parent.add(new DataPair(key, new DataText(identifier.toString())));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	public void addStatus(List<DataPair> parent, String key, Object obj) {
		if (obj != null) {
			try {
				Method m = obj.getClass().getMethod("get" + key.substring(0, 1).toUpperCase() + key.substring(1), new Class[] { });
				if (!m.getReturnType().equals(Status.class))
					throw new IllegalArgumentException("Unexpected return codes, expected Status but got: " + m.getReturnType().getName());
				Status status = (Status)m.invoke(obj, new Object[] { });
				if (locale == null) {
					if (status != null)
						parent.add(new DataPair(key, new DataText(status.toString().toLowerCase())));
				} else {
					if (status != null)
						parent.add(new DataPair(key, new DataText(status.getName(locale))));
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	public void addSalutation(List<DataPair> parent, String key, Object obj) {
		if (obj != null) {
			try {
				Method m = obj.getClass().getMethod("get" + key.substring(0, 1).toUpperCase() + key.substring(1), new Class[] { });
				if (!m.getReturnType().equals(Salutation.class))
					throw new IllegalArgumentException("Unexpected return codes, expected Salutation but got: " + m.getReturnType().getName());
				Salutation salutation = (Salutation)m.invoke(obj, new Object[] { });
				if (locale == null) {
					if (salutation != null && salutation.getCode() != null)
						parent.add(new DataPair(key, new DataNumber(salutation.getCode())));
				} else {
					if (salutation != null && salutation.getName(locale) != null)
						parent.add(new DataPair(key, new DataText(salutation.getName(locale))));
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public void addCountry(List<DataPair> parent, String key, Object obj) {
		if (obj != null) {
			try {
				Method m = obj.getClass().getMethod("get" + key.substring(0, 1).toUpperCase() + key.substring(1), new Class[] { });
				if (!m.getReturnType().equals(Country.class))
					throw new IllegalArgumentException("Unexpected return codes, expected Country but got: " + m.getReturnType().getName());
				Country country = (Country)m.invoke(obj, new Object[] { });
				if (locale == null) {
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

	public void addLanguage(List<DataPair> parent, String key, Object obj) {
		if (obj != null) {
			try {
				Method m = obj.getClass().getMethod("get" + key.substring(0, 1).toUpperCase() + key.substring(1), new Class[] { });
				if (!m.getReturnType().equals(Language.class))
					throw new IllegalArgumentException("Unexpected return codes, expected Language but got: " + m.getReturnType().getName());
				Language language = (Language)m.invoke(obj, new Object[] { });
				if (locale == null) {
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
	
	public void addBusinessSector(List<DataPair> parent, String key, Object obj) {
		if (obj != null) {
			try {
				Method m = obj.getClass().getMethod("get" + key.substring(0, 1).toUpperCase() + key.substring(1), new Class[] { });
				if (!m.getReturnType().equals(BusinessSector.class))
					throw new IllegalArgumentException("Unexpected return codes, expected BusinessSector but got: " + m.getReturnType().getName());
				BusinessSector sector = (BusinessSector)m.invoke(obj, new Object[] { });
				if (locale == null) {
					if (sector != null && sector.getCode() != null)
						parent.add(new DataPair(key, new DataNumber(sector.getCode())));
				} else {
					if (sector != null && sector.getName(locale) != null)
						parent.add(new DataPair(key, new DataText(sector.getName(locale))));
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	public void addBusinessUnit(List<DataPair> parent, String key, Object obj) {
		if (obj != null) {
			try {
				Method m = obj.getClass().getMethod("get" + key.substring(0, 1).toUpperCase() + key.substring(1), new Class[] { });
				if (!m.getReturnType().equals(BusinessUnit.class))
					throw new IllegalArgumentException("Unexpected return codes, expected BusinessUnit but got: " + m.getReturnType().getName());
				BusinessUnit unit = (BusinessUnit)m.invoke(obj, new Object[] { });
				if (locale == null) {
					if (unit != null && unit.getCode() != null)
						parent.add(new DataPair(key, new DataNumber(unit.getCode())));
				} else {
					if (unit != null && unit.getName(locale) != null)
						parent.add(new DataPair(key, new DataText(unit.getName(locale))));
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	public void addBusinessClassification(List<DataPair> parent, String key, Object obj) {
		if (obj != null) {
			try {
				Method m = obj.getClass().getMethod("get" + key.substring(0, 1).toUpperCase() + key.substring(1), new Class[] { });
				if (!m.getReturnType().equals(BusinessClassification.class))
					throw new IllegalArgumentException("Unexpected return codes, expected BusinessClassification but got: " + m.getReturnType().getName());
				BusinessClassification classification = (BusinessClassification)m.invoke(obj, new Object[] { });
				if (locale == null) {
					if (classification != null && classification.getCode() != null)
						parent.add(new DataPair(key, new DataNumber(classification.getCode())));
				} else {
					if (classification != null && classification.getName(locale) != null)
						parent.add(new DataPair(key, new DataText(classification.getName(locale))));
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public void addPersonName(List<DataPair> parent, String key, Object obj) {
		if (obj != null) {
			try {
				Method m = obj.getClass().getMethod("get" + key.substring(0, 1).toUpperCase() + key.substring(1), new Class[] { });
				if (!m.getReturnType().equals(PersonName.class))
					throw new IllegalArgumentException("Unexpected return codes, expected PersonName but got: " + m.getReturnType().getName());
				PersonName name = (PersonName)m.invoke(obj, new Object[] { });
				List<DataPair> pairs = new ArrayList<DataPair>();
				addSalutation(pairs, "salutation", name);
				addText(pairs, "firstName", name);
				addText(pairs, "middleName", name);
				addText(pairs, "lastName", name);
				if (!pairs.isEmpty())
					parent.add(new DataPair(key, new DataObject(pairs)));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public void addTelephone(List<DataPair> parent, String key, Object obj) {
		if (obj != null) {
			try {
				Method m = obj.getClass().getMethod("get" + key.substring(0, 1).toUpperCase() + key.substring(1), new Class[] { });
				if (!m.getReturnType().equals(Telephone.class))
					throw new IllegalArgumentException("Unexpected return codes, expected Telephone but got: " + m.getReturnType().getName());
				Telephone telephone = (Telephone)m.invoke(obj, new Object[] { });
				List<DataPair> pairs = new ArrayList<DataPair>();
				addText(pairs, "number", telephone);
				addText(pairs, "extension", telephone);
				if (!pairs.isEmpty())
					parent.add(new DataPair(key, new DataObject(pairs)));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}


	public void addMailingAddress(List<DataPair> parent, String key, Object obj) {
		if (obj != null) {
			try {
				Method m = obj.getClass().getMethod("get" + key.substring(0, 1).toUpperCase() + key.substring(1), new Class[] { });
				if (!m.getReturnType().equals(MailingAddress.class))
					throw new IllegalArgumentException("Unexpected return codes, expected MailingAddress but got: " + m.getReturnType().getName());
				MailingAddress address = (MailingAddress)m.invoke(obj, new Object[] { });
				List<DataPair> pairs = new ArrayList<DataPair>();
				addText(pairs, "street", address);
				addText(pairs, "city", address);
				addText(pairs, "province", address);
				addCountry(pairs, "country", address);
				addText(pairs, "postalCode", address);
				if (!pairs.isEmpty())
					parent.add(new DataPair(key, new DataObject(pairs)));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public void addCommunication(List<DataPair> parent, String key, Object obj) {
		if (obj != null) {
			try {
				Method m = obj.getClass().getMethod("get" + key.substring(0, 1).toUpperCase() + key.substring(1), new Class[] { });
				if (!m.getReturnType().equals(Communication.class))
					throw new IllegalArgumentException("Unexpected return codes, expected Communication but got: " + m.getReturnType().getName());
				Communication communication = (Communication)m.invoke(obj, new Object[] { });
				List<DataPair> pairs = new ArrayList<DataPair>();
				addText(pairs, "email", communication);
				addText(pairs, "jobTitle", communication);
				addLanguage(pairs, "language", communication);
				addTelephone(pairs, "homePhone", communication);
				addText(pairs, "faxNumber", communication);
				if (!pairs.isEmpty())
					parent.add(new DataPair(key, new DataObject(pairs)));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public void addBusinessPosition(List<DataPair> parent, String key, Object obj) {
		if (obj != null) {
			try {
				Method m = obj.getClass().getMethod("get" + key.substring(0, 1).toUpperCase() + key.substring(1), new Class[] { });
				if (!m.getReturnType().equals(BusinessPosition.class))
					throw new IllegalArgumentException("Unexpected return codes, expected BusinessPosition but got: " + m.getReturnType().getName());
				BusinessPosition position = (BusinessPosition)m.invoke(obj, new Object[] { });
				List<DataPair> pairs = new ArrayList<DataPair>();
				addBusinessSector(pairs, "sector", position);
				addBusinessUnit(pairs, "unit", position);
				addBusinessClassification(pairs, "classification", position);
				if (!pairs.isEmpty())
					parent.add(new DataPair(key, new DataObject(pairs)));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public void addUser(List<DataPair> parent, String key, Object obj) {
		if (obj != null) {
			try {
				Method m = obj.getClass().getMethod("get" + key.substring(0, 1).toUpperCase() + key.substring(1), new Class[] { });
				if (!m.getReturnType().equals(User.class))
					throw new IllegalArgumentException("Unexpected return codes, expected User but got: " + m.getReturnType().getName());
				User user = (User)m.invoke(obj, new Object[] { });
				List<DataPair> pairs = new ArrayList<DataPair>();
				addText(pairs, "userName", user);
				if (user.getRoles() != null) {
					pairs.add(new DataPair("roles", new DataArray(user.getRoles().stream()
							.map(r -> new DataText(r.getCode())).collect(Collectors.toList()))));
				}
				if (!pairs.isEmpty())
					parent.add(new DataPair(key, new DataObject(pairs)));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public Identifier parseIdentifier(String key, DataObject data) {
		return data.contains(key) ? new Identifier(data.getString(key)) : null;
	}
	
	public Status parseStatus(String key, DataObject data) {
		return data.contains(key) ? Status.valueOf(data.getString(key).toUpperCase()) : null;
	}
		
	public String parseText(String key, DataObject data) {
		return data.contains(key) ? data.getString(key) : null;
	}
	
	public Salutation parseSalutation(String key, DataObject data) {
		if (locale == null) {
			return data.contains(key, DataNumber.class) ? crm.findSalutationByCode(data.getInt(key)) : null;
		} else {
			return data.contains(key, DataText.class) ? crm.findSalutationByLocalizedName(locale, data.getString(key)) : null;
		}
	}
	
	public Country parseCountry(String key, DataObject data) {
		if (locale == null) {
			return data.contains(key, DataText.class) ? crm.findCountryByCode(data.getString(key)) : null;
		} else {
			return data.contains(key, DataText.class) ? crm.findCountryByLocalizedName(locale, data.getString(key)) : null;
		}
	}
	
	public Language parseLanguage(String key, DataObject data) {
		if (locale == null) {
			return data.contains(key, DataText.class) ? crm.findLanguageByCode(data.getString(key)) : null;
		} else {
			return data.contains(key, DataText.class) ? crm.findLanguageByLocalizedName(locale, data.getString(key)) : null;
		}
	}
	
	public BusinessSector parseBusinessSector(String key, DataObject data) {
		if (locale == null) {
			return data.contains(key, DataNumber.class) ? crm.findBusinessSectorByCode(data.getInt(key)) : null;
		} else {
			return data.contains(key, DataText.class) ? crm.findBusinessSectorByLocalizedName(locale, data.getString(key)) : null;
		}
	}
	
	public BusinessUnit parseBusinessUnit(String key, DataObject data) {
		if (locale == null) {
			return data.contains(key, DataNumber.class) ? crm.findBusinessUnitByCode(data.getInt(key)) : null;
		} else {
			return data.contains(key, DataText.class) ? crm.findBusinessUnitByLocalizedName(locale, data.getString(key)) : null;
		}
	}
	
	public BusinessClassification parseBusinessClassification(String key, DataObject data) {
		if (locale == null) {
			return data.contains(key, DataNumber.class) ? crm.findBusinessClassificationByCode(data.getInt(key)) : null;
		} else {
			return data.contains(key, DataText.class) ? crm.findBusinessClassificationByLocalizedName(locale, data.getString(key)) : null;
		}
	}
	
	public PersonName parsePersonName(String key, DataObject parent) {
		DataObject data = parent.getObject(key);
		if (data == null)
			return null;
		Salutation salutation = parseSalutation("salutation", data);
		String firstName = parseText("firstName", data);
		String middleName = parseText("middleName", data);
		String lastName = parseText("lastName", data);
		return new PersonName(salutation, firstName, middleName, lastName);
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
		return new MailingAddress(street, city, province, country, postalCode);	
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
		return new Communication(jobTitle, language, email, homePhone, faxNumber);
	}
	
	public BusinessPosition parseBusinessPosition(String key, DataObject parent) {
		DataObject data = parent.getObject(key);
		if (data == null)
			return null;
		BusinessSector sector = parseBusinessSector("sector", data);
		BusinessUnit unit = parseBusinessUnit("unit", data);
		BusinessClassification classification = parseBusinessClassification("classification", data);
		return new BusinessPosition(sector, unit, classification);
	}
	
	public User parseUser(String key, DataObject parent) {
		DataObject data = parent.getObject(key);
		if (data == null)
			return null;
		String userName = parseText("userName", data);
		List<Role> roles = data.contains("roles") ? 
			data.getArray("roles").stream()
				.map(r -> crm.findRoleByCode(((DataText)r).value()))
				.collect(Collectors.toList()) :
			new ArrayList<Role>();
		return new User(userName, roles);
	}

}
