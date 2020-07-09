package ca.magex.crm.api.services;

import java.io.OutputStream;

import ca.magex.crm.api.common.PersonName;
import ca.magex.crm.api.crm.User;
import ca.magex.crm.api.repositories.CrmRepositories;
import ca.magex.crm.api.system.Lang;
import ca.magex.crm.api.system.Localized;
import ca.magex.crm.api.system.Option;
import ca.magex.crm.api.system.Status;
import ca.magex.crm.api.system.Type;
import ca.magex.crm.api.system.id.AuthenticationGroupIdentifier;
import ca.magex.crm.api.system.id.BusinessGroupIdentifier;
import ca.magex.crm.api.system.id.CountryIdentifier;
import ca.magex.crm.api.system.id.MessageTypeIdentifier;
import ca.magex.crm.api.system.id.OptionIdentifier;

public interface CrmConfigurationService {

	boolean isInitialized();

	User initializeSystem(String organization, PersonName name, String email, String username, String password);
	
	boolean reset();
	
	void dump(OutputStream os);
	
	/**
	 * Default initialization of the system into the given repositories
	 * @param repos
	 */
	default void initialize(CrmRepositories repos) {
		createSysGroup(repos);
		createAppGroup(repos);
		createCrmGroup(repos);
		createOrgGroup(repos);
		createStatusLookup(repos);
		createLocaleLookup(repos);
		createMessageTypeLookup(repos);
		createLanguageLookup(repos);
		createSalutationsLookup(repos);
		createCountriesLookup(repos);
		createCanadianProvinces(repos);
		createAmericanStates(repos);
		createMexicanProvinces(repos);
		createBusinessPositions(repos);
		createValidationPhrases(repos);
	}
	
	default OptionIdentifier createRootOption(CrmRepositories repos, Type type, Boolean mutable, Localized name) {
		OptionIdentifier optionId = repos.generateForType(type, name.getCode());
		return repos.saveOption(new Option(optionId, null, type, Status.ACTIVE, mutable, name)).getOptionId();
	}
	
	default OptionIdentifier createNestedOption(CrmRepositories repos, OptionIdentifier parentId, Type type, Boolean mutable, Localized name) {
		Option parent = repos.findOption(parentId);
		/* update the code to prepend the parent code */
		Localized updatedName = name.withCode(parent.getCode() + "/" + name.getCode());
		OptionIdentifier optionId = repos.generateForType(type, updatedName.getCode());
		return repos.saveOption(new Option(optionId, parentId, type, Status.ACTIVE, mutable, updatedName)).getOptionId();
	}
	
	/**
	 * Create the default System group that controls the administration of the system
	 * <ul>
	 * 	<li>SYS_ADMIN - Application shutdown (os user)</li>
	 *  <li>SYS_ACTUATOR - Monitoring and services (monitoring tool)</li>
	 *  <li>SYS_ACCESS - Managing group and roles (service desk)</li>
	 * </ul>
	 */
	default void createSysGroup(CrmRepositories repos) {
		OptionIdentifier sysGroupId = createRootOption(repos, Type.AUTHENTICATION_GROUP, Option.IMMUTABLE, new Localized(AuthenticationGroupIdentifier.SYS.getCode(), "System", "Système"));
		createNestedOption(repos, sysGroupId, Type.AUTHENTICATION_ROLE, Option.IMMUTABLE, new Localized("ADMIN", "System Administrator", "Adminstrator du système"));
		createNestedOption(repos, sysGroupId, Type.AUTHENTICATION_ROLE, Option.IMMUTABLE, new Localized("ACTUATOR", "System Actuator", "Actuator du système"));
		createNestedOption(repos, sysGroupId, Type.AUTHENTICATION_ROLE, Option.IMMUTABLE, new Localized("ACCESS", "System Access", "Access du système"));
	}

	/**
	 * Create the default application group for applications to create background requests
	 * <ul>
	 * 	<li>APP_AUTH_REQUEST - Token verification (application background user)</li>
	 * </ul>
	 * @param repos
	 */
	default void createAppGroup(CrmRepositories repos) {
		OptionIdentifier appGroupId = createRootOption(repos, Type.AUTHENTICATION_GROUP, Option.IMMUTABLE, new Localized(AuthenticationGroupIdentifier.APP.getCode(), "Application", "Application"));
		createNestedOption(repos, appGroupId, Type.AUTHENTICATION_ROLE, Option.IMMUTABLE, new Localized("AUTHENTICATOR", "Authorization Requestor", "Demandeur d'Autorisation"));
	}
	
	/**
	 * Create the default CRM group for users allowed to view or manage all of the organizations
	 * <ul>
	 * 	<li>CRM_ADMIN - Create and manage all organization</li>
	 * 	<li>CRM_USER - View all of the organization</li>
	 * </ul>
	 * @param repos
	 */
	default void createCrmGroup(CrmRepositories repos) {
		OptionIdentifier crmGroupId = createRootOption(repos, Type.AUTHENTICATION_GROUP, Option.IMMUTABLE, new Localized(AuthenticationGroupIdentifier.CRM.getCode(), "Customer Relationship Management", "Gestion de la relation client"));
		createNestedOption(repos, crmGroupId, Type.AUTHENTICATION_ROLE, Option.IMMUTABLE, new Localized("ADMIN", "CRM Admin", "Administrateur GRC"));
		createNestedOption(repos, crmGroupId, Type.AUTHENTICATION_ROLE, Option.IMMUTABLE, new Localized("USER", "CRM Viewer", "Visionneuse GRC"));
	}

	/**
	 * Create the default organization roles for single organization access
	 * <ul>
	 * 	<li>ORG_ADMIN - Full access to their organizations information</li>
	 * 	<li>ORG_USER - Limited access to their organizations information</li>
	 * </ul>
	 * @param repos
	 */
	default void createOrgGroup(CrmRepositories repos) {
		OptionIdentifier orgGroupId = createRootOption(repos, Type.AUTHENTICATION_GROUP, Option.MUTABLE, new Localized(AuthenticationGroupIdentifier.ORG.getCode(), "Organization", "Organisation"));
		createNestedOption(repos, orgGroupId, Type.AUTHENTICATION_ROLE, Option.MUTABLE, new Localized("ADMIN", "Organization Admin", "Administrateur de l'organisation"));
		createNestedOption(repos, orgGroupId, Type.AUTHENTICATION_ROLE, Option.MUTABLE, new Localized("USER", "Organization Viewer", "Visionneuse d'organisation"));
	}

	/**
	 * Create a default set of statuses that are immutable.
	 * @param repos
	 */
	default void createStatusLookup(CrmRepositories repos) {
		createRootOption(repos, Type.STATUS, Option.IMMUTABLE, Status.ACTIVE.getName());
		createRootOption(repos, Type.STATUS, Option.IMMUTABLE, Status.INACTIVE.getName());
		createRootOption(repos, Type.STATUS, Option.IMMUTABLE, Status.PENDING.getName());
	}
	
	/**
	 * Create a default set of statuses that are immutable.
	 * @param repos
	 */
	default void createLocaleLookup(CrmRepositories repos) {
		createRootOption(repos, Type.LOCALE, Option.IMMUTABLE, Lang.NAMES.get(Lang.ROOT));
		createRootOption(repos, Type.LOCALE, Option.IMMUTABLE, Lang.NAMES.get(Lang.ENGLISH));
		createRootOption(repos, Type.LOCALE, Option.IMMUTABLE, Lang.NAMES.get(Lang.FRENCH));
	}
	
	/**
	 * Create a default set of message types that are immutable.
	 * @param repos
	 */
	default void createMessageTypeLookup(CrmRepositories repos) {
		createRootOption(repos, Type.MESSAGE_TYPE, Option.MUTABLE, new Localized(MessageTypeIdentifier.ERROR.getCode(), "Error", "Erreur"));
		createRootOption(repos, Type.MESSAGE_TYPE, Option.MUTABLE, new Localized(MessageTypeIdentifier.WARN.getCode(), "Warning", "Avertissement"));
		createRootOption(repos, Type.MESSAGE_TYPE, Option.MUTABLE, new Localized(MessageTypeIdentifier.INFO.getCode(), "Notification", "Notification"));
		createRootOption(repos, Type.MESSAGE_TYPE, Option.MUTABLE, new Localized(MessageTypeIdentifier.SUCCESS.getCode(), "Success", "Succès"));
	}
	
	/**
	 * Create a default set of statuses that are immutable.
	 * @param repos
	 */
	default void createLanguageLookup(CrmRepositories repos) {
		createRootOption(repos, Type.LANGUAGE, Option.MUTABLE, Lang.NAMES.get(Lang.ROOT));
		createRootOption(repos, Type.LANGUAGE, Option.MUTABLE, Lang.NAMES.get(Lang.ENGLISH));
		createRootOption(repos, Type.LANGUAGE, Option.MUTABLE, Lang.NAMES.get(Lang.FRENCH));
	}
	
	/**
	 * Create a default set of business positions.
	 * @param repos
	 */
	default void createBusinessPositions(CrmRepositories repos) {
		OptionIdentifier execsId = createRootOption(repos, Type.BUSINESS_GROUP, Option.MUTABLE, new Localized(BusinessGroupIdentifier.EXECS.getCode(), "Executives", "Cadres"));
		createNestedOption(repos, execsId, Type.BUSINESS_ROLE, Option.MUTABLE, new Localized("CEO", "Chief Executive Officer", "Directeur général"));
		createNestedOption(repos, execsId, Type.BUSINESS_ROLE, Option.MUTABLE, new Localized("CFO", "Chief Financial Officer", "Directeur financier"));
		createNestedOption(repos, execsId, Type.BUSINESS_ROLE, Option.MUTABLE, new Localized("COO", "Chief Operations Officer", "Directeur des opérations"));
		createNestedOption(repos, execsId, Type.BUSINESS_ROLE, Option.MUTABLE, new Localized("CIO", "Chief Information Officer", "Directeur de l'information"));
		createNestedOption(repos, execsId, Type.BUSINESS_ROLE, Option.MUTABLE, new Localized("CHRO", "Chief Human Resources Officer", "Directeur des ressources humaines"));
		createNestedOption(repos, execsId, Type.BUSINESS_ROLE, Option.MUTABLE, new Localized("CTO", "Chief Technology Officer", "Directeur de la technologie"));
		createNestedOption(repos, execsId, Type.BUSINESS_ROLE, Option.MUTABLE, new Localized("CSO", "Chief Security Officer", "Directeur de sécurité"));

		OptionIdentifier imitId = createRootOption(repos, Type.BUSINESS_GROUP, Option.MUTABLE, new Localized(BusinessGroupIdentifier.IMIT.getCode(), "IM/IT", "GI/TI"));
		createNestedOption(repos, imitId, Type.BUSINESS_ROLE, Option.MUTABLE, new Localized("DIRECTOR", "Director", "Réalisateur"));

		OptionIdentifier opsId = createNestedOption(repos, imitId, Type.BUSINESS_GROUP, Option.MUTABLE, new Localized("OPS", "Operations", "Operations"));
		createNestedOption(repos, opsId, Type.BUSINESS_ROLE, Option.MUTABLE, new Localized("MANAGER", "Manager", "Gestionnaire"));
		
		OptionIdentifier hdId = createNestedOption(repos, opsId, Type.BUSINESS_GROUP, Option.MUTABLE, new Localized("HD", "Help Desk", "Bureau d'aide"));
		createNestedOption(repos, hdId, Type.BUSINESS_ROLE, Option.MUTABLE, new Localized("TEAMLEAD", "Team Lead", "Chef d'équipe"));
		createNestedOption(repos, hdId, Type.BUSINESS_ROLE, Option.MUTABLE, new Localized("TECHNICIAN", "Help Desk Technician", "Technicien de bureau d'aide"));
		
		OptionIdentifier infraId = createNestedOption(repos, opsId, Type.BUSINESS_GROUP, Option.MUTABLE, new Localized("INFRA", "Infrastructure", "Infrastructure"));
		createNestedOption(repos, infraId, Type.BUSINESS_ROLE, Option.MUTABLE, new Localized("TEAMLEAD", "Team Lead", "Chef d'équipe"));
		createNestedOption(repos, infraId, Type.BUSINESS_ROLE, Option.MUTABLE, new Localized("ADMIN", "System Administrator", "Administrateur du système"));
		
		OptionIdentifier devId = createNestedOption(repos, imitId, Type.BUSINESS_GROUP, Option.MUTABLE, new Localized("DEV", "Application Development", "Développement d'applications"));
		createNestedOption(repos, devId, Type.BUSINESS_ROLE, Option.MUTABLE, new Localized("MANAGER", "Manager", "Gestionnaire"));

		OptionIdentifier dmId = createNestedOption(repos, devId, Type.BUSINESS_GROUP, Option.MUTABLE, new Localized("DM", "Data Management", "Gestion de données"));
		createNestedOption(repos, dmId, Type.BUSINESS_ROLE, Option.MUTABLE, new Localized("TEAMLEAD", "Team Lead", "Chef d'équipe"));
		createNestedOption(repos, dmId, Type.BUSINESS_ROLE, Option.MUTABLE, new Localized("DBA", "Database Administrator", "Administrateur de base de données"));
		
		OptionIdentifier appsId = createNestedOption(repos, devId, Type.BUSINESS_GROUP, Option.MUTABLE, new Localized("APPS", "Applications", "Applications"));
		createNestedOption(repos, appsId, Type.BUSINESS_ROLE, Option.MUTABLE, new Localized("TEAMLEAD", "Team Lead", "Chef d'équipe"));
		createNestedOption(repos, appsId, Type.BUSINESS_ROLE, Option.MUTABLE, new Localized("DEV", "Developer", "Développeur"));
		
		OptionIdentifier qaId = createNestedOption(repos, devId, Type.BUSINESS_GROUP, Option.MUTABLE, new Localized("QA", "Quality Assurance", "Assurance qualité"));
		createNestedOption(repos, qaId, Type.BUSINESS_ROLE, Option.MUTABLE, new Localized("TEAMLEAD", "Team Lead", "Chef d'équipe"));
		createNestedOption(repos, qaId, Type.BUSINESS_ROLE, Option.MUTABLE, new Localized("TESTER", "Quality Tester", "Testeur de qualité"));

		OptionIdentifier externalId = createRootOption(repos, Type.BUSINESS_GROUP, Option.MUTABLE, new Localized(BusinessGroupIdentifier.EXTERNAL.getCode(), "External", "Externe"));
		createNestedOption(repos, externalId, Type.BUSINESS_ROLE, Option.MUTABLE, new Localized("OWNER", "Owner", "Propriétaire"));
		createNestedOption(repos, externalId, Type.BUSINESS_ROLE, Option.MUTABLE, new Localized("EMPLOYEE", "Employee", "Employé"));
		createNestedOption(repos, externalId, Type.BUSINESS_ROLE, Option.MUTABLE, new Localized("CONTACT", "Contact", "Contact"));
	}
	
	/**
	 * Create a default set of validation phrases
	 * @param repos
	 */
	default void createValidationPhrases(CrmRepositories repos) {
		OptionIdentifier validationId = createRootOption(repos, Type.DICTIONARY, Option.IMMUTABLE, new Localized("VALIDATION", "Validation Messages", "Messages de validation"));

		OptionIdentifier fieldValidationId = createNestedOption(repos, validationId, Type.DICTIONARY, Option.IMMUTABLE, new Localized("FIELD", "Field Validation", "Validation sur le terrain"));
		createNestedOption(repos, fieldValidationId, Type.PHRASE, true, new Localized("REQUIRED", "Field is required", "Champ requis"));
		createNestedOption(repos, fieldValidationId, Type.PHRASE, true, new Localized("FORBIDDEN", "Field is forbidden", "Le champ est interdit"));
		createNestedOption(repos, fieldValidationId, Type.PHRASE, true, new Localized("INVALID", "Field is invalid", "Le champ n'est pas valide"));
		createNestedOption(repos, fieldValidationId, Type.PHRASE, true, new Localized("FORMAT", "Format is invalid", "Le format n'est pas valide"));
		createNestedOption(repos, fieldValidationId, Type.PHRASE, true, new Localized("MINLENGTH", "Field too short", "Champ trop court"));
		createNestedOption(repos, fieldValidationId, Type.PHRASE, true, new Localized("MAXLENGTH", "Field too long", "Champ trop long"));
		createNestedOption(repos, fieldValidationId, Type.PHRASE, true, new Localized("INACTIVE", "Field is inactive", "Champ est inactive"));

		OptionIdentifier statusValidationId = createNestedOption(repos, validationId, Type.DICTIONARY, Option.IMMUTABLE, new Localized("STATUS", "Status Validation", "Validation du statut"));
		createNestedOption(repos, statusValidationId, Type.PHRASE, true, new Localized("PENDING", "Entity should not have a pending status with an identifier", "L'entité ne doit pas avoir de statut en attente avec un identifiant"));

		OptionIdentifier optionValidationId = createNestedOption(repos, validationId, Type.DICTIONARY, Option.IMMUTABLE, new Localized("OPTION", "Option Validation", "Validation des options"));
		createNestedOption(repos, optionValidationId, Type.PHRASE, true, new Localized("IMMUTABLE", "Option is immutable", "L'option est immuable"));
		createNestedOption(repos, optionValidationId, Type.PHRASE, true, new Localized("DUPLICATE", "Option is a duplicate", "L'option est un doublon"));
		createNestedOption(repos, optionValidationId, Type.PHRASE, true, new Localized("INVALID", "Option is an invalid type", "L'option est un type non valide"));

		
	}
	
	/**
	 * Create a default set of statuses that are immutable.
	 * @param repos
	 */
	default void createSalutationsLookup(CrmRepositories repos) {
		createRootOption(repos, Type.SALUTATION, Option.MUTABLE, new Localized("MISS", "Miss", "Mlle."));
		createRootOption(repos, Type.SALUTATION, Option.MUTABLE, new Localized("MRS", "Mrs.", "Mme."));
		createRootOption(repos, Type.SALUTATION, Option.MUTABLE, new Localized("MR", "Mr.", "M."));
	}
	
	/**
	 * Create a default set of countries for the system that can be extended to a larger set later
	 * @param repos
	 */
	default void createCountriesLookup(CrmRepositories repos) {
		createRootOption(repos, Type.COUNTRY, Option.MUTABLE, new Localized("CA", "Canada", "Canada"));
		createRootOption(repos, Type.COUNTRY, Option.MUTABLE, new Localized("GB", "United Kingdom", "Royaume-Uni"));
		createRootOption(repos, Type.COUNTRY, Option.MUTABLE, new Localized("DE", "Germany", "Allemagne"));
		createRootOption(repos, Type.COUNTRY, Option.MUTABLE, new Localized("FR", "France", "France"));
		createRootOption(repos, Type.COUNTRY, Option.MUTABLE, new Localized("IT", "Italy", "Italie"));
		createRootOption(repos, Type.COUNTRY, Option.MUTABLE, new Localized("MX", "Mexico", "Mexique"));
		createRootOption(repos, Type.COUNTRY, Option.MUTABLE, new Localized("US", "United States", "États-Unis d'Amérique"));
	}
	
	/**
	 * Create a set of Canadian Provinces
	 * @param repos
	 */
	default void createCanadianProvinces(CrmRepositories repos) {
		Option ca = repos.findOption(new CountryIdentifier("CA"));
		createNestedOption(repos, ca.getOptionId(), Type.PROVINCE, Option.MUTABLE, new Localized("AB", "Alberta", "Alberta"));
		createNestedOption(repos, ca.getOptionId(), Type.PROVINCE, Option.MUTABLE, new Localized("BC", "British Columbia", "Colombie-Britannique"));
		createNestedOption(repos, ca.getOptionId(), Type.PROVINCE, Option.MUTABLE, new Localized("MB", "Manitoba", "Manitoba"));
		createNestedOption(repos, ca.getOptionId(), Type.PROVINCE, Option.MUTABLE, new Localized("NB", "New Brunswick", "Nouveau-Brunswick"));
		createNestedOption(repos, ca.getOptionId(), Type.PROVINCE, Option.MUTABLE, new Localized("NL", "Newfoundland and Labrador", "Terre-Neuve et Labrador"));
		createNestedOption(repos, ca.getOptionId(), Type.PROVINCE, Option.MUTABLE, new Localized("NS", "Nova Scotia", "Nouvelle-Ècosse"));
		createNestedOption(repos, ca.getOptionId(), Type.PROVINCE, Option.MUTABLE, new Localized("NT", "Northwest Territories", "Territoires du Nord-Ouest"));
		createNestedOption(repos, ca.getOptionId(), Type.PROVINCE, Option.MUTABLE, new Localized("NU", "Nunavut", "Nunavut"));
		createNestedOption(repos, ca.getOptionId(), Type.PROVINCE, Option.MUTABLE, new Localized("ON", "Ontario", "Ontario"));
		createNestedOption(repos, ca.getOptionId(), Type.PROVINCE, Option.MUTABLE, new Localized("PE", "Prince Edward Island", "Île-du-Prince-Èdouard"));
		createNestedOption(repos, ca.getOptionId(), Type.PROVINCE, Option.MUTABLE, new Localized("QC", "Quebec", "Québec"));
		createNestedOption(repos, ca.getOptionId(), Type.PROVINCE, Option.MUTABLE, new Localized("SK", "Saskatchewan", "Saskatchewan"));
		createNestedOption(repos, ca.getOptionId(), Type.PROVINCE, Option.MUTABLE, new Localized("YT", "Yukon", "Yukon"));
	}
	
	/**
	 * Create a set of American States
	 * @param repos
	 */
	default void createAmericanStates(CrmRepositories repos) {
		Option us = repos.findOption(new CountryIdentifier("US"));
		createNestedOption(repos, us.getOptionId(), Type.PROVINCE, Option.MUTABLE, new Localized("AK", "Alaska", "Alaska"));
		createNestedOption(repos, us.getOptionId(), Type.PROVINCE, Option.MUTABLE, new Localized("AL", "Alabama", "Alabama"));
		createNestedOption(repos, us.getOptionId(), Type.PROVINCE, Option.MUTABLE, new Localized("AR", "Arkansas", "Arkansas"));
		createNestedOption(repos, us.getOptionId(), Type.PROVINCE, Option.MUTABLE, new Localized("AZ", "Arizona", "Arizona"));
		createNestedOption(repos, us.getOptionId(), Type.PROVINCE, Option.MUTABLE, new Localized("CA", "California", "Californie"));
		createNestedOption(repos, us.getOptionId(), Type.PROVINCE, Option.MUTABLE, new Localized("CO", "Colorado", "Colorado"));
		createNestedOption(repos, us.getOptionId(), Type.PROVINCE, Option.MUTABLE, new Localized("CT", "Connecticut", "Connecticut"));
		createNestedOption(repos, us.getOptionId(), Type.PROVINCE, Option.MUTABLE, new Localized("DC", "District of Columbia", "District fédéral de Columbia"));
		createNestedOption(repos, us.getOptionId(), Type.PROVINCE, Option.MUTABLE, new Localized("DE", "Delaware", "Delaware"));
		createNestedOption(repos, us.getOptionId(), Type.PROVINCE, Option.MUTABLE, new Localized("FL", "Florida", "Floride"));
		createNestedOption(repos, us.getOptionId(), Type.PROVINCE, Option.MUTABLE, new Localized("GA", "Georgia", "Géorgie"));
		createNestedOption(repos, us.getOptionId(), Type.PROVINCE, Option.MUTABLE, new Localized("HI", "Hawaii", "Hawai"));
		createNestedOption(repos, us.getOptionId(), Type.PROVINCE, Option.MUTABLE, new Localized("IA", "Iowa", "Iowa"));
		createNestedOption(repos, us.getOptionId(), Type.PROVINCE, Option.MUTABLE, new Localized("ID", "Idaho", "Idaho"));
		createNestedOption(repos, us.getOptionId(), Type.PROVINCE, Option.MUTABLE, new Localized("IL", "Illinois", "Illinois"));
		createNestedOption(repos, us.getOptionId(), Type.PROVINCE, Option.MUTABLE, new Localized("IN", "Indiana", "Indiana"));
		createNestedOption(repos, us.getOptionId(), Type.PROVINCE, Option.MUTABLE, new Localized("KS", "Kansas", "Kansas"));
		createNestedOption(repos, us.getOptionId(), Type.PROVINCE, Option.MUTABLE, new Localized("KY", "Kentucky", "Kentucky"));
		createNestedOption(repos, us.getOptionId(), Type.PROVINCE, Option.MUTABLE, new Localized("LA", "Louisiana", "Louisiane"));
		createNestedOption(repos, us.getOptionId(), Type.PROVINCE, Option.MUTABLE, new Localized("MA", "Massachusetts", "Massachusetts"));
		createNestedOption(repos, us.getOptionId(), Type.PROVINCE, Option.MUTABLE, new Localized("MD", "Maryland", "Maryland"));
		createNestedOption(repos, us.getOptionId(), Type.PROVINCE, Option.MUTABLE, new Localized("ME", "Maine", "Maine"));
		createNestedOption(repos, us.getOptionId(), Type.PROVINCE, Option.MUTABLE, new Localized("MI", "Michigan", "Michigan"));
		createNestedOption(repos, us.getOptionId(), Type.PROVINCE, Option.MUTABLE, new Localized("MN", "Minnesota", "Minnesota"));
		createNestedOption(repos, us.getOptionId(), Type.PROVINCE, Option.MUTABLE, new Localized("MO", "Missouri", "Missouri"));
		createNestedOption(repos, us.getOptionId(), Type.PROVINCE, Option.MUTABLE, new Localized("MS", "Mississippi", "Mississippi"));
		createNestedOption(repos, us.getOptionId(), Type.PROVINCE, Option.MUTABLE, new Localized("MT", "Montana", "Montana"));
		createNestedOption(repos, us.getOptionId(), Type.PROVINCE, Option.MUTABLE, new Localized("NC", "North Carolina", "Caroline du Nord"));
		createNestedOption(repos, us.getOptionId(), Type.PROVINCE, Option.MUTABLE, new Localized("ND", "North Dakota", "Dakota du Nord"));
		createNestedOption(repos, us.getOptionId(), Type.PROVINCE, Option.MUTABLE, new Localized("NE", "Nebraska", "Nebraska"));
		createNestedOption(repos, us.getOptionId(), Type.PROVINCE, Option.MUTABLE, new Localized("NH", "New Hampshire", "New Hampshire"));
		createNestedOption(repos, us.getOptionId(), Type.PROVINCE, Option.MUTABLE, new Localized("NJ", "New Jersey", "New Jersey"));
		createNestedOption(repos, us.getOptionId(), Type.PROVINCE, Option.MUTABLE, new Localized("NM", "New Mexico", "Nouveau-Mexique"));
		createNestedOption(repos, us.getOptionId(), Type.PROVINCE, Option.MUTABLE, new Localized("NV", "Nevada", "Nevada"));
		createNestedOption(repos, us.getOptionId(), Type.PROVINCE, Option.MUTABLE, new Localized("NY", "New York", "New York"));
		createNestedOption(repos, us.getOptionId(), Type.PROVINCE, Option.MUTABLE, new Localized("OH", "Ohio", "Ohio"));
		createNestedOption(repos, us.getOptionId(), Type.PROVINCE, Option.MUTABLE, new Localized("OK", "Oklahoma", "Oklahoma"));
		createNestedOption(repos, us.getOptionId(), Type.PROVINCE, Option.MUTABLE, new Localized("OR", "Oregon", "Orégon"));
		createNestedOption(repos, us.getOptionId(), Type.PROVINCE, Option.MUTABLE, new Localized("PA", "Pennsylvania", "Pennsylvanie"));
		createNestedOption(repos, us.getOptionId(), Type.PROVINCE, Option.MUTABLE, new Localized("RI", "Rhode Island", "Rhode Island"));
		createNestedOption(repos, us.getOptionId(), Type.PROVINCE, Option.MUTABLE, new Localized("SC", "South Carolina", "Carolina du Sud"));
		createNestedOption(repos, us.getOptionId(), Type.PROVINCE, Option.MUTABLE, new Localized("SD", "South Dakota", "Dakota du Sud"));
		createNestedOption(repos, us.getOptionId(), Type.PROVINCE, Option.MUTABLE, new Localized("TN", "Tennessee", "Tennessee"));
		createNestedOption(repos, us.getOptionId(), Type.PROVINCE, Option.MUTABLE, new Localized("TX", "Texas", "Texas"));
		createNestedOption(repos, us.getOptionId(), Type.PROVINCE, Option.MUTABLE, new Localized("UT", "Utah", "Utah"));
		createNestedOption(repos, us.getOptionId(), Type.PROVINCE, Option.MUTABLE, new Localized("VA", "Virginia", "Virginie"));
		createNestedOption(repos, us.getOptionId(), Type.PROVINCE, Option.MUTABLE, new Localized("VT", "Vermont", "Vermont"));
		createNestedOption(repos, us.getOptionId(), Type.PROVINCE, Option.MUTABLE, new Localized("WA", "Washington", "Washington"));
		createNestedOption(repos, us.getOptionId(), Type.PROVINCE, Option.MUTABLE, new Localized("WI", "Wisconsin", "Wisconsin"));
		createNestedOption(repos, us.getOptionId(), Type.PROVINCE, Option.MUTABLE, new Localized("WV", "West Virginia", "Virginie-Occidentale"));
		createNestedOption(repos, us.getOptionId(), Type.PROVINCE, Option.MUTABLE, new Localized("WY", "Wyoming", "Wyoming"));
	}

	/**
	 * Create a set of Mexican Provinces
	 * @param repos
	 */
	default void createMexicanProvinces(CrmRepositories repos) {
		Option mx = repos.findOption(new CountryIdentifier("MX"));
		createNestedOption(repos, mx.getOptionId(), Type.PROVINCE, Option.MUTABLE, new Localized("AG", "Aguascalientas", "Aguascalientas"));
		createNestedOption(repos, mx.getOptionId(), Type.PROVINCE, Option.MUTABLE, new Localized("BA", "Baja California (North)", "Baja California (Nord)"));
		createNestedOption(repos, mx.getOptionId(), Type.PROVINCE, Option.MUTABLE, new Localized("BJ", "Baja California (South)", "Baja California (Sud)"));
		createNestedOption(repos, mx.getOptionId(), Type.PROVINCE, Option.MUTABLE, new Localized("CE", "Campeche", "Campeche"));
		createNestedOption(repos, mx.getOptionId(), Type.PROVINCE, Option.MUTABLE, new Localized("CH", "Chihuahua", "Chihuahua"));
		createNestedOption(repos, mx.getOptionId(), Type.PROVINCE, Option.MUTABLE, new Localized("CI", "Chiapas", "Chiapas"));
		createNestedOption(repos, mx.getOptionId(), Type.PROVINCE, Option.MUTABLE, new Localized("CL", "Colima", "Colima"));
		createNestedOption(repos, mx.getOptionId(), Type.PROVINCE, Option.MUTABLE, new Localized("CU", "Coahuila de Zaragoza", "Coahuila de Zaragoza"));
		createNestedOption(repos, mx.getOptionId(), Type.PROVINCE, Option.MUTABLE, new Localized("DF", "Distrito", "Distrito (Federal)"));
		createNestedOption(repos, mx.getOptionId(), Type.PROVINCE, Option.MUTABLE, new Localized("DO", "Durango", "Durango"));
		createNestedOption(repos, mx.getOptionId(), Type.PROVINCE, Option.MUTABLE, new Localized("GR", "Guerreo", "Guerreo"));
		createNestedOption(repos, mx.getOptionId(), Type.PROVINCE, Option.MUTABLE, new Localized("GU", "Guanajuato", "Guanajuato"));
		createNestedOption(repos, mx.getOptionId(), Type.PROVINCE, Option.MUTABLE, new Localized("HL", "Hidalgo", "Hidalgo"));
		createNestedOption(repos, mx.getOptionId(), Type.PROVINCE, Option.MUTABLE, new Localized("JL", "Jalisco", "Jalisco"));
		createNestedOption(repos, mx.getOptionId(), Type.PROVINCE, Option.MUTABLE, new Localized("MC", "Michoacan de Ocampo", "Michoacan de Ocampo"));
		createNestedOption(repos, mx.getOptionId(), Type.PROVINCE, Option.MUTABLE, new Localized("MR", "Morelos", "Morelos"));
		createNestedOption(repos, mx.getOptionId(), Type.PROVINCE, Option.MUTABLE, new Localized("MX", "Mexico (State)", "Mexico (Ètat)"));
		createNestedOption(repos, mx.getOptionId(), Type.PROVINCE, Option.MUTABLE, new Localized("NA", "Nayarit", "Nayarit"));
		createNestedOption(repos, mx.getOptionId(), Type.PROVINCE, Option.MUTABLE, new Localized("NL", "Nuevo Leon", "Nuevo Leon"));
		createNestedOption(repos, mx.getOptionId(), Type.PROVINCE, Option.MUTABLE, new Localized("OA", "Oaxaca", "Oaxaca"));
		createNestedOption(repos, mx.getOptionId(), Type.PROVINCE, Option.MUTABLE, new Localized("PB", "Puebla", "Puebla"));
		createNestedOption(repos, mx.getOptionId(), Type.PROVINCE, Option.MUTABLE, new Localized("QR", "Quintana Roo", "Quintana Roo"));
		createNestedOption(repos, mx.getOptionId(), Type.PROVINCE, Option.MUTABLE, new Localized("QU", "Queretaro de Arteaga", "Queretaro de Arteaga"));
		createNestedOption(repos, mx.getOptionId(), Type.PROVINCE, Option.MUTABLE, new Localized("SI", "Sinaloa", "Sinaloa"));
		createNestedOption(repos, mx.getOptionId(), Type.PROVINCE, Option.MUTABLE, new Localized("SL", "San Luis Potosi", "San Luis Potosi"));
		createNestedOption(repos, mx.getOptionId(), Type.PROVINCE, Option.MUTABLE, new Localized("SO", "Sonora", "Sonora"));
		createNestedOption(repos, mx.getOptionId(), Type.PROVINCE, Option.MUTABLE, new Localized("TA", "Tamaulipas", "Tamaulipas"));
		createNestedOption(repos, mx.getOptionId(), Type.PROVINCE, Option.MUTABLE, new Localized("TB", "Tabasco", "Tabasco"));
		createNestedOption(repos, mx.getOptionId(), Type.PROVINCE, Option.MUTABLE, new Localized("TL", "Tlaxcala", "Tlaxcala"));
		createNestedOption(repos, mx.getOptionId(), Type.PROVINCE, Option.MUTABLE, new Localized("VC", "Veracruz-Llave", "Veracruz-Llave"));
		createNestedOption(repos, mx.getOptionId(), Type.PROVINCE, Option.MUTABLE, new Localized("YU", "Yucatan", "Yucatan"));
		createNestedOption(repos, mx.getOptionId(), Type.PROVINCE, Option.MUTABLE, new Localized("ZA", "Zacatecas", "Zacatecas"));
	}
	
}
