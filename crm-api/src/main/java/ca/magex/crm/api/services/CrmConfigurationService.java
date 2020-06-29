package ca.magex.crm.api.services;

import java.io.OutputStream;

import ca.magex.crm.api.common.PersonName;
import ca.magex.crm.api.crm.User;
import ca.magex.crm.api.dictionary.CrmDictionary;
import ca.magex.crm.api.filters.OptionsFilter;
import ca.magex.crm.api.repositories.CrmRepositories;
import ca.magex.crm.api.system.Lang;
import ca.magex.crm.api.system.Localized;
import ca.magex.crm.api.system.Option;
import ca.magex.crm.api.system.Status;
import ca.magex.crm.api.system.Type;
import ca.magex.crm.api.system.id.OptionIdentifier;

public interface CrmConfigurationService {

	boolean isInitialized();

	User initializeSystem(String organization, PersonName name, String email, String username, String password);
	
	CrmDictionary getDictionary();
	
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
		createLanguageLookup(repos);
		createSalutationsLookup(repos);
		createCountriesLookup(repos);
		createCanadianProvinces(repos);
		createAmericanStates(repos);
		createMexicanProvinces(repos);
		createBusinessPositions(repos);
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
		OptionIdentifier sysGroupId = repos.saveOption(new Option(repos.generateAuthenticationGroupId(), null, Type.AUTHENTICATION_GROUP, Status.ACTIVE, false, new Localized("SYS", "System", "Système"))).getOptionId();
		repos.saveOption(new Option(repos.generateAuthenticationRoleId(), sysGroupId, Type.AUTHENTICATION_ROLE, Status.ACTIVE, false, new Localized("ADMIN", "System Administrator", "Adminstrator du système"))).getOptionId();
		repos.saveOption(new Option(repos.generateAuthenticationRoleId(), sysGroupId, Type.AUTHENTICATION_ROLE, Status.ACTIVE, false, new Localized("ACTUATOR", "System Actuator", "Actuator du système"))).getOptionId();
		repos.saveOption(new Option(repos.generateAuthenticationRoleId(), sysGroupId, Type.AUTHENTICATION_ROLE, Status.ACTIVE, false, new Localized("ACCESS", "System Access", "Access du système"))).getOptionId();
	}

	/**
	 * Create the default application group for applications to create background requests
	 * <ul>
	 * 	<li>APP_AUTH_REQUEST - Token verification (application background user)</li>
	 * </ul>
	 * @param repos
	 */
	default void createAppGroup(CrmRepositories repos) {
		OptionIdentifier appGroupId = repos.saveOption(new Option(repos.generateAuthenticationGroupId(), null, Type.AUTHENTICATION_GROUP, Status.ACTIVE, false, new Localized("APP", "Application", "Application"))).getOptionId();
		repos.saveOption(new Option(repos.generateAuthenticationRoleId(), appGroupId, Type.AUTHENTICATION_ROLE, Status.ACTIVE, false, new Localized("AUTHENTICATOR", "Authorization Requestor", "Demandeur d'Autorisation"))).getOptionId();
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
		OptionIdentifier crmGroupId = repos.saveOption(new Option(repos.generateAuthenticationGroupId(), null, Type.AUTHENTICATION_GROUP, Status.ACTIVE, false, new Localized("CRM", "Customer Relationship Management", "Gestion de la relation client"))).getOptionId();
		repos.saveOption(new Option(repos.generateAuthenticationRoleId(), crmGroupId, Type.AUTHENTICATION_ROLE, Status.ACTIVE, false, new Localized("ADMIN", "CRM Admin", "Administrateur GRC"))).getOptionId();
		repos.saveOption(new Option(repos.generateAuthenticationRoleId(), crmGroupId, Type.AUTHENTICATION_ROLE, Status.ACTIVE, false, new Localized("USER", "CRM Viewer", "Visionneuse GRC"))).getOptionId();
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
		OptionIdentifier orgGroupId = repos.saveOption(new Option(repos.generateAuthenticationGroupId(), null, Type.AUTHENTICATION_GROUP, Status.ACTIVE, true, new Localized("ORG", "Organization", "Organisation"))).getOptionId();
		repos.saveOption(new Option(repos.generateAuthenticationRoleId(), orgGroupId, Type.AUTHENTICATION_ROLE, Status.ACTIVE, true, new Localized("ADMIN", "Organization Admin", "Administrateur GRC"))).getOptionId();
		repos.saveOption(new Option(repos.generateAuthenticationRoleId(), orgGroupId, Type.AUTHENTICATION_ROLE, Status.ACTIVE, true, new Localized("USER", "Organization Viewer", "Visionneuse GRC"))).getOptionId();
	}

	/**
	 * Create a default set of statuses that are immutable.
	 * @param repos
	 */
	default void createStatusLookup(CrmRepositories repos) {
		repos.saveOption(new Option(repos.generateStatusId(), null, Type.STATUS, Status.ACTIVE, false, Status.ACTIVE.getName())).getOptionId();
		repos.saveOption(new Option(repos.generateStatusId(), null, Type.STATUS, Status.ACTIVE, false, Status.INACTIVE.getName())).getOptionId();
		repos.saveOption(new Option(repos.generateStatusId(), null, Type.STATUS, Status.ACTIVE, false, Status.PENDING.getName())).getOptionId();
	}
	
	/**
	 * Create a default set of statuses that are immutable.
	 * @param repos
	 */
	default void createLocaleLookup(CrmRepositories repos) {
		repos.saveOption(new Option(repos.generateLocaleId(), null, Type.LOCALE, Status.ACTIVE, false, Lang.NAMES.get(Lang.ROOT))).getOptionId();
		repos.saveOption(new Option(repos.generateLocaleId(), null, Type.LOCALE, Status.ACTIVE, false, Lang.NAMES.get(Lang.ENGLISH))).getOptionId();
		repos.saveOption(new Option(repos.generateLocaleId(), null, Type.LOCALE, Status.ACTIVE, false, Lang.NAMES.get(Lang.FRENCH))).getOptionId();
	}
	
	/**
	 * Create a default set of statuses that are immutable.
	 * @param repos
	 */
	default void createLanguageLookup(CrmRepositories repos) {
		repos.saveOption(new Option(repos.generateLanguageId(), null, Type.LANGUAGE, Status.ACTIVE, true, Lang.NAMES.get(Lang.ROOT))).getOptionId();
		repos.saveOption(new Option(repos.generateLanguageId(), null, Type.LANGUAGE, Status.ACTIVE, true, Lang.NAMES.get(Lang.ENGLISH))).getOptionId();
	}
	
	/**
	 * Create a default set of business positions.
	 * @param repos
	 */
	default void createBusinessPositions(CrmRepositories repos) {
		OptionIdentifier execsId = repos.saveOption(new Option(repos.generateBusinessGroupId(), null, Type.BUSINESS_GROUP, Status.ACTIVE, true, new Localized("EXECS", "Executives", "Cadres"))).getOptionId();
		repos.saveOption(new Option(repos.generateBusinessRoleId(), execsId, Type.BUSINESS_ROLE, Status.ACTIVE, true, new Localized("CEO", "Chief Executive Officer", "Directeur général"))).getOptionId();
		repos.saveOption(new Option(repos.generateBusinessRoleId(), execsId, Type.BUSINESS_ROLE, Status.ACTIVE, true, new Localized("CFO", "Chief Financial Officer", "Directeur financier"))).getOptionId();
		repos.saveOption(new Option(repos.generateBusinessRoleId(), execsId, Type.BUSINESS_ROLE, Status.ACTIVE, true, new Localized("COO", "Chief Operations Officer", "Directeur des opérations"))).getOptionId();
		repos.saveOption(new Option(repos.generateBusinessRoleId(), execsId, Type.BUSINESS_ROLE, Status.ACTIVE, true, new Localized("CIO", "Chief Information Officer", "Directeur de l'information"))).getOptionId();
		repos.saveOption(new Option(repos.generateBusinessRoleId(), execsId, Type.BUSINESS_ROLE, Status.ACTIVE, true, new Localized("CHRO", "Chief Human Resources Officer", "Directeur des ressources humaines"))).getOptionId();
		repos.saveOption(new Option(repos.generateBusinessRoleId(), execsId, Type.BUSINESS_ROLE, Status.ACTIVE, true, new Localized("CTO", "Chief Technology Officer", "Directeur de la technologie"))).getOptionId();
		repos.saveOption(new Option(repos.generateBusinessRoleId(), execsId, Type.BUSINESS_ROLE, Status.ACTIVE, true, new Localized("CSO", "Chief Security Officer", "Directeur de sécurité"))).getOptionId();

		OptionIdentifier imitId = repos.saveOption(new Option(repos.generateBusinessGroupId(), null, Type.BUSINESS_GROUP, Status.ACTIVE, true, new Localized("IMIT", "IM/IT", "GI / TI"))).getOptionId();
		repos.saveOption(new Option(repos.generateBusinessRoleId(), imitId, Type.BUSINESS_ROLE, Status.ACTIVE, true, new Localized("DIRECTOR", "Director", "Réalisateur")));

		OptionIdentifier opsId = repos.saveOption(new Option(repos.generateBusinessGroupId(), imitId, Type.BUSINESS_GROUP, Status.ACTIVE, true, new Localized("OPS", "Operations", "Operations"))).getOptionId();
		repos.saveOption(new Option(repos.generateBusinessRoleId(), opsId, Type.BUSINESS_ROLE, Status.ACTIVE, true, new Localized("MANAGER", "Manager", "Gestionnaire")));
		
		OptionIdentifier hdId = repos.saveOption(new Option(repos.generateBusinessGroupId(), opsId, Type.BUSINESS_GROUP, Status.ACTIVE, true, new Localized("HD", "Help Desk", "Bureau d'aide"))).getOptionId();
		repos.saveOption(new Option(repos.generateBusinessRoleId(), hdId, Type.BUSINESS_ROLE, Status.ACTIVE, true, new Localized("TEAMLEAD", "Team Lead", "Chef d'équipe")));
		repos.saveOption(new Option(repos.generateBusinessRoleId(), hdId, Type.BUSINESS_ROLE, Status.ACTIVE, true, new Localized("TECHNICIAN", "Help Desk Technician", "Technicien de bureau d'aide")));
		
		OptionIdentifier infraId = repos.saveOption(new Option(repos.generateBusinessGroupId(), opsId, Type.BUSINESS_GROUP, Status.ACTIVE, true, new Localized("INFRA", "Infrastructure", "Infrastructure"))).getOptionId();
		repos.saveOption(new Option(repos.generateBusinessRoleId(), infraId, Type.BUSINESS_ROLE, Status.ACTIVE, true, new Localized("TEAMLEAD", "Team Lead", "Chef d'équipe")));
		repos.saveOption(new Option(repos.generateBusinessRoleId(), infraId, Type.BUSINESS_ROLE, Status.ACTIVE, true, new Localized("ADMIN", "System Administrator", "Administrateur du système")));
		
		OptionIdentifier devId = repos.saveOption(new Option(repos.generateBusinessGroupId(), imitId, Type.BUSINESS_GROUP, Status.ACTIVE, true, new Localized("DEV", "Application Development", "Directeur général"))).getOptionId();
		repos.saveOption(new Option(repos.generateBusinessRoleId(), devId, Type.BUSINESS_ROLE, Status.ACTIVE, true, new Localized("MANAGER", "Manager", "Gestionnaire")));

		OptionIdentifier dmId = repos.saveOption(new Option(repos.generateBusinessGroupId(), devId, Type.BUSINESS_GROUP, Status.ACTIVE, true, new Localized("DM", "Data Management", "Gestion de données"))).getOptionId();
		repos.saveOption(new Option(repos.generateBusinessRoleId(), dmId, Type.BUSINESS_ROLE, Status.ACTIVE, true, new Localized("TEAMLEAD", "Team Lead", "Chef d'équipe")));
		repos.saveOption(new Option(repos.generateBusinessRoleId(), dmId, Type.BUSINESS_ROLE, Status.ACTIVE, true, new Localized("DBA", "Database Administrator", "Administrateur de base de données")));
		
		OptionIdentifier appsId = repos.saveOption(new Option(repos.generateBusinessGroupId(), devId, Type.BUSINESS_GROUP, Status.ACTIVE, true, new Localized("APPS", "Applications", "Applications"))).getOptionId();
		repos.saveOption(new Option(repos.generateBusinessRoleId(), appsId, Type.BUSINESS_ROLE, Status.ACTIVE, true, new Localized("TEAMLEAD", "Team Lead", "Chef d'équipe")));
		repos.saveOption(new Option(repos.generateBusinessRoleId(), appsId, Type.BUSINESS_ROLE, Status.ACTIVE, true, new Localized("DEV", "Developer", "Développeur")));
		
		OptionIdentifier qaId = repos.saveOption(new Option(repos.generateBusinessGroupId(), devId, Type.BUSINESS_GROUP, Status.ACTIVE, true, new Localized("QA", "Quality Assurance", "Assurance qualité"))).getOptionId();
		repos.saveOption(new Option(repos.generateBusinessRoleId(), qaId, Type.BUSINESS_ROLE, Status.ACTIVE, true, new Localized("TEAMLEAD", "Team Lead", "Chef d'équipe")));
		repos.saveOption(new Option(repos.generateBusinessRoleId(), qaId, Type.BUSINESS_ROLE, Status.ACTIVE, true, new Localized("TESTER", "Quality Tester", "Testeur de qualité")));
	}
	
	/**
	 * Create a default set of statuses that are immutable.
	 * @param repos
	 */
	default void createSalutationsLookup(CrmRepositories repos) {
		repos.saveOption(new Option(repos.generateSalutationId(), null, Type.SALUTATION, Status.ACTIVE, true, new Localized("MISS", "Miss", "Mlle.")));
		repos.saveOption(new Option(repos.generateSalutationId(), null, Type.SALUTATION, Status.ACTIVE, true, new Localized("MRS", "Mrs.", "Mme.")));
		repos.saveOption(new Option(repos.generateSalutationId(), null, Type.SALUTATION, Status.ACTIVE, true, new Localized("MR", "Mr.", "M.")));
	}
	
	/**
	 * Create a default set of countries for the system that can be extended to a larger set later
	 * @param repos
	 */
	default void createCountriesLookup(CrmRepositories repos) {
		repos.saveOption(new Option(repos.generateCountryId(), null, Type.COUNTRY, Status.ACTIVE, true, new Localized("CA", "Canada", "Canada")));
		repos.saveOption(new Option(repos.generateCountryId(), null, Type.COUNTRY, Status.ACTIVE, true, new Localized("GB", "United Kingdom", "Royaume-Uni")));
		repos.saveOption(new Option(repos.generateCountryId(), null, Type.COUNTRY, Status.ACTIVE, true, new Localized("DE", "Germany", "Allemagne")));
		repos.saveOption(new Option(repos.generateCountryId(), null, Type.COUNTRY, Status.ACTIVE, true, new Localized("FR", "France", "France")));
		repos.saveOption(new Option(repos.generateCountryId(), null, Type.COUNTRY, Status.ACTIVE, true, new Localized("IT", "Italy", "Italie")));
		repos.saveOption(new Option(repos.generateCountryId(), null, Type.COUNTRY, Status.ACTIVE, true, new Localized("MX", "Mexico", "Mexique")));
		repos.saveOption(new Option(repos.generateCountryId(), null, Type.COUNTRY, Status.ACTIVE, true, new Localized("US", "United States", "États-Unis d'Amérique")));
	}
	
	/**
	 * Create a set of Canadian Provinces
	 * @param repos
	 */
	default void createCanadianProvinces(CrmRepositories repos) {
		Option ca = repos.findOptions(new OptionsFilter().withType(Type.COUNTRY).withOptionCode("CA"), OptionsFilter.getDefaultPaging()).getSingleItem();
		repos.saveOption(new Option(repos.generateProvinceId(), ca.getOptionId(), Type.PROVINCE, Status.ACTIVE, true, new Localized("AB", "Alberta", "Alberta")));
		repos.saveOption(new Option(repos.generateProvinceId(), ca.getOptionId(), Type.PROVINCE, Status.ACTIVE, true, new Localized("BC", "British Columbia", "Colombie-Britannique")));
		repos.saveOption(new Option(repos.generateProvinceId(), ca.getOptionId(), Type.PROVINCE, Status.ACTIVE, true, new Localized("MB", "Manitoba", "Manitoba")));
		repos.saveOption(new Option(repos.generateProvinceId(), ca.getOptionId(), Type.PROVINCE, Status.ACTIVE, true, new Localized("NB", "New Brunswick", "Nouveau-Brunswick")));
		repos.saveOption(new Option(repos.generateProvinceId(), ca.getOptionId(), Type.PROVINCE, Status.ACTIVE, true, new Localized("NL", "Newfoundland and Labrador", "Terre-Neuve et Labrador")));
		repos.saveOption(new Option(repos.generateProvinceId(), ca.getOptionId(), Type.PROVINCE, Status.ACTIVE, true, new Localized("NS", "Nova Scotia", "Nouvelle-Ècosse")));
		repos.saveOption(new Option(repos.generateProvinceId(), ca.getOptionId(), Type.PROVINCE, Status.ACTIVE, true, new Localized("NT", "Northwest Territories", "Territoires du Nord-Ouest")));
		repos.saveOption(new Option(repos.generateProvinceId(), ca.getOptionId(), Type.PROVINCE, Status.ACTIVE, true, new Localized("NU", "Nunavut", "Nunavut")));
		repos.saveOption(new Option(repos.generateProvinceId(), ca.getOptionId(), Type.PROVINCE, Status.ACTIVE, true, new Localized("ON", "Ontario", "Ontario")));
		repos.saveOption(new Option(repos.generateProvinceId(), ca.getOptionId(), Type.PROVINCE, Status.ACTIVE, true, new Localized("PE", "Prince Edward Island", "Île-du-Prince-Èdouard")));
		repos.saveOption(new Option(repos.generateProvinceId(), ca.getOptionId(), Type.PROVINCE, Status.ACTIVE, true, new Localized("QC", "Quebec", "Québec")));
		repos.saveOption(new Option(repos.generateProvinceId(), ca.getOptionId(), Type.PROVINCE, Status.ACTIVE, true, new Localized("SK", "Saskatchewan", "Saskatchewan")));
		repos.saveOption(new Option(repos.generateProvinceId(), ca.getOptionId(), Type.PROVINCE, Status.ACTIVE, true, new Localized("YT", "Yukon", "Yukon")));
	}
	
	/**
	 * Create a set of American States
	 * @param repos
	 */
	default void createAmericanStates(CrmRepositories repos) {
		Option us = repos.findOptions(new OptionsFilter().withType(Type.COUNTRY).withOptionCode("US"), OptionsFilter.getDefaultPaging()).getSingleItem();
		repos.saveOption(new Option(repos.generateProvinceId(), us.getOptionId(), Type.PROVINCE, Status.ACTIVE, true, new Localized("AK", "Alaska", "Alaska")));
		repos.saveOption(new Option(repos.generateProvinceId(), us.getOptionId(), Type.PROVINCE, Status.ACTIVE, true, new Localized("AL", "Alabama", "Alabama")));
		repos.saveOption(new Option(repos.generateProvinceId(), us.getOptionId(), Type.PROVINCE, Status.ACTIVE, true, new Localized("AR", "Arkansas", "Arkansas")));
		repos.saveOption(new Option(repos.generateProvinceId(), us.getOptionId(), Type.PROVINCE, Status.ACTIVE, true, new Localized("AZ", "Arizona", "Arizona")));
		repos.saveOption(new Option(repos.generateProvinceId(), us.getOptionId(), Type.PROVINCE, Status.ACTIVE, true, new Localized("CA", "California", "Californie")));
		repos.saveOption(new Option(repos.generateProvinceId(), us.getOptionId(), Type.PROVINCE, Status.ACTIVE, true, new Localized("CO", "Colorado", "Colorado")));
		repos.saveOption(new Option(repos.generateProvinceId(), us.getOptionId(), Type.PROVINCE, Status.ACTIVE, true, new Localized("CT", "Connecticut", "Connecticut")));
		repos.saveOption(new Option(repos.generateProvinceId(), us.getOptionId(), Type.PROVINCE, Status.ACTIVE, true, new Localized("DC", "District of Columbia", "District fédéral de Columbia")));
		repos.saveOption(new Option(repos.generateProvinceId(), us.getOptionId(), Type.PROVINCE, Status.ACTIVE, true, new Localized("DE", "Delaware", "Delaware")));
		repos.saveOption(new Option(repos.generateProvinceId(), us.getOptionId(), Type.PROVINCE, Status.ACTIVE, true, new Localized("FL", "Florida", "Floride")));
		repos.saveOption(new Option(repos.generateProvinceId(), us.getOptionId(), Type.PROVINCE, Status.ACTIVE, true, new Localized("GA", "Georgia", "Géorgie")));
		repos.saveOption(new Option(repos.generateProvinceId(), us.getOptionId(), Type.PROVINCE, Status.ACTIVE, true, new Localized("HI", "Hawaii", "Hawai")));
		repos.saveOption(new Option(repos.generateProvinceId(), us.getOptionId(), Type.PROVINCE, Status.ACTIVE, true, new Localized("IA", "Iowa", "Iowa")));
		repos.saveOption(new Option(repos.generateProvinceId(), us.getOptionId(), Type.PROVINCE, Status.ACTIVE, true, new Localized("ID", "Idaho", "Idaho")));
		repos.saveOption(new Option(repos.generateProvinceId(), us.getOptionId(), Type.PROVINCE, Status.ACTIVE, true, new Localized("IL", "Illinois", "Illinois")));
		repos.saveOption(new Option(repos.generateProvinceId(), us.getOptionId(), Type.PROVINCE, Status.ACTIVE, true, new Localized("IN", "Indiana", "Indiana")));
		repos.saveOption(new Option(repos.generateProvinceId(), us.getOptionId(), Type.PROVINCE, Status.ACTIVE, true, new Localized("KS", "Kansas", "Kansas")));
		repos.saveOption(new Option(repos.generateProvinceId(), us.getOptionId(), Type.PROVINCE, Status.ACTIVE, true, new Localized("KY", "Kentucky", "Kentucky")));
		repos.saveOption(new Option(repos.generateProvinceId(), us.getOptionId(), Type.PROVINCE, Status.ACTIVE, true, new Localized("LA", "Louisiana", "Louisiane")));
		repos.saveOption(new Option(repos.generateProvinceId(), us.getOptionId(), Type.PROVINCE, Status.ACTIVE, true, new Localized("MA", "Massachusetts", "Massachusetts")));
		repos.saveOption(new Option(repos.generateProvinceId(), us.getOptionId(), Type.PROVINCE, Status.ACTIVE, true, new Localized("MD", "Maryland", "Maryland")));
		repos.saveOption(new Option(repos.generateProvinceId(), us.getOptionId(), Type.PROVINCE, Status.ACTIVE, true, new Localized("ME", "Maine", "Maine")));
		repos.saveOption(new Option(repos.generateProvinceId(), us.getOptionId(), Type.PROVINCE, Status.ACTIVE, true, new Localized("MI", "Michigan", "Michigan")));
		repos.saveOption(new Option(repos.generateProvinceId(), us.getOptionId(), Type.PROVINCE, Status.ACTIVE, true, new Localized("MN", "Minnesota", "Minnesota")));
		repos.saveOption(new Option(repos.generateProvinceId(), us.getOptionId(), Type.PROVINCE, Status.ACTIVE, true, new Localized("MO", "Missouri", "Missouri")));
		repos.saveOption(new Option(repos.generateProvinceId(), us.getOptionId(), Type.PROVINCE, Status.ACTIVE, true, new Localized("MS", "Mississippi", "Mississippi")));
		repos.saveOption(new Option(repos.generateProvinceId(), us.getOptionId(), Type.PROVINCE, Status.ACTIVE, true, new Localized("MT", "Montana", "Montana")));
		repos.saveOption(new Option(repos.generateProvinceId(), us.getOptionId(), Type.PROVINCE, Status.ACTIVE, true, new Localized("NC", "North Carolina", "Caroline du Nord")));
		repos.saveOption(new Option(repos.generateProvinceId(), us.getOptionId(), Type.PROVINCE, Status.ACTIVE, true, new Localized("ND", "North Dakota", "Dakota du Nord")));
		repos.saveOption(new Option(repos.generateProvinceId(), us.getOptionId(), Type.PROVINCE, Status.ACTIVE, true, new Localized("NE", "Nebraska", "Nebraska")));
		repos.saveOption(new Option(repos.generateProvinceId(), us.getOptionId(), Type.PROVINCE, Status.ACTIVE, true, new Localized("NH", "New Hampshire", "New Hampshire")));
		repos.saveOption(new Option(repos.generateProvinceId(), us.getOptionId(), Type.PROVINCE, Status.ACTIVE, true, new Localized("NJ", "New Jersey", "New Jersey")));
		repos.saveOption(new Option(repos.generateProvinceId(), us.getOptionId(), Type.PROVINCE, Status.ACTIVE, true, new Localized("NM", "New Mexico", "Nouveau-Mexique")));
		repos.saveOption(new Option(repos.generateProvinceId(), us.getOptionId(), Type.PROVINCE, Status.ACTIVE, true, new Localized("NV", "Nevada", "Nevada")));
		repos.saveOption(new Option(repos.generateProvinceId(), us.getOptionId(), Type.PROVINCE, Status.ACTIVE, true, new Localized("NY", "New York", "New York")));
		repos.saveOption(new Option(repos.generateProvinceId(), us.getOptionId(), Type.PROVINCE, Status.ACTIVE, true, new Localized("OH", "Ohio", "Ohio")));
		repos.saveOption(new Option(repos.generateProvinceId(), us.getOptionId(), Type.PROVINCE, Status.ACTIVE, true, new Localized("OK", "Oklahoma", "Oklahoma")));
		repos.saveOption(new Option(repos.generateProvinceId(), us.getOptionId(), Type.PROVINCE, Status.ACTIVE, true, new Localized("OR", "Oregon", "Orégon")));
		repos.saveOption(new Option(repos.generateProvinceId(), us.getOptionId(), Type.PROVINCE, Status.ACTIVE, true, new Localized("PA", "Pennsylvania", "Pennsylvanie")));
		repos.saveOption(new Option(repos.generateProvinceId(), us.getOptionId(), Type.PROVINCE, Status.ACTIVE, true, new Localized("RI", "Rhode Island", "Rhode Island")));
		repos.saveOption(new Option(repos.generateProvinceId(), us.getOptionId(), Type.PROVINCE, Status.ACTIVE, true, new Localized("SC", "South Carolina", "Carolina du Sud")));
		repos.saveOption(new Option(repos.generateProvinceId(), us.getOptionId(), Type.PROVINCE, Status.ACTIVE, true, new Localized("SD", "South Dakota", "Dakota du Sud")));
		repos.saveOption(new Option(repos.generateProvinceId(), us.getOptionId(), Type.PROVINCE, Status.ACTIVE, true, new Localized("TN", "Tennessee", "Tennessee")));
		repos.saveOption(new Option(repos.generateProvinceId(), us.getOptionId(), Type.PROVINCE, Status.ACTIVE, true, new Localized("TX", "Texas", "Texas")));
		repos.saveOption(new Option(repos.generateProvinceId(), us.getOptionId(), Type.PROVINCE, Status.ACTIVE, true, new Localized("UT", "Utah", "Utah")));
		repos.saveOption(new Option(repos.generateProvinceId(), us.getOptionId(), Type.PROVINCE, Status.ACTIVE, true, new Localized("VA", "Virginia", "Virginie")));
		repos.saveOption(new Option(repos.generateProvinceId(), us.getOptionId(), Type.PROVINCE, Status.ACTIVE, true, new Localized("VT", "Vermont", "Vermont")));
		repos.saveOption(new Option(repos.generateProvinceId(), us.getOptionId(), Type.PROVINCE, Status.ACTIVE, true, new Localized("WA", "Washington", "Washington")));
		repos.saveOption(new Option(repos.generateProvinceId(), us.getOptionId(), Type.PROVINCE, Status.ACTIVE, true, new Localized("WI", "Wisconsin", "Wisconsin")));
		repos.saveOption(new Option(repos.generateProvinceId(), us.getOptionId(), Type.PROVINCE, Status.ACTIVE, true, new Localized("WV", "West Virginia", "Virginie-Occidentale")));
		repos.saveOption(new Option(repos.generateProvinceId(), us.getOptionId(), Type.PROVINCE, Status.ACTIVE, true, new Localized("WY", "Wyoming", "Wyoming")));
	}

	/**
	 * Create a set of Mexican Provinces
	 * @param repos
	 */
	default void createMexicanProvinces(CrmRepositories repos) {
		Option mx = repos.findOptions(new OptionsFilter().withType(Type.COUNTRY).withOptionCode("MX"), OptionsFilter.getDefaultPaging()).getSingleItem();
		repos.saveOption(new Option(repos.generateProvinceId(), mx.getOptionId(), Type.PROVINCE, Status.ACTIVE, true, new Localized("AG", "Aguascalientas", "Aguascalientas")));
		repos.saveOption(new Option(repos.generateProvinceId(), mx.getOptionId(), Type.PROVINCE, Status.ACTIVE, true, new Localized("BA", "Baja California (North)", "Baja California (Nord)")));
		repos.saveOption(new Option(repos.generateProvinceId(), mx.getOptionId(), Type.PROVINCE, Status.ACTIVE, true, new Localized("BJ", "Baja California (South)", "Baja California (Sud)")));
		repos.saveOption(new Option(repos.generateProvinceId(), mx.getOptionId(), Type.PROVINCE, Status.ACTIVE, true, new Localized("CE", "Campeche", "Campeche")));
		repos.saveOption(new Option(repos.generateProvinceId(), mx.getOptionId(), Type.PROVINCE, Status.ACTIVE, true, new Localized("CH", "Chihuahua", "Chihuahua")));
		repos.saveOption(new Option(repos.generateProvinceId(), mx.getOptionId(), Type.PROVINCE, Status.ACTIVE, true, new Localized("CI", "Chiapas", "Chiapas")));
		repos.saveOption(new Option(repos.generateProvinceId(), mx.getOptionId(), Type.PROVINCE, Status.ACTIVE, true, new Localized("CL", "Colima", "Colima")));
		repos.saveOption(new Option(repos.generateProvinceId(), mx.getOptionId(), Type.PROVINCE, Status.ACTIVE, true, new Localized("CU", "Coahuila de Zaragoza", "Coahuila de Zaragoza")));
		repos.saveOption(new Option(repos.generateProvinceId(), mx.getOptionId(), Type.PROVINCE, Status.ACTIVE, true, new Localized("DF", "Distrito", "Distrito (Federal)")));
		repos.saveOption(new Option(repos.generateProvinceId(), mx.getOptionId(), Type.PROVINCE, Status.ACTIVE, true, new Localized("DO", "Durango", "Durango")));
		repos.saveOption(new Option(repos.generateProvinceId(), mx.getOptionId(), Type.PROVINCE, Status.ACTIVE, true, new Localized("GR", "Guerreo", "Guerreo")));
		repos.saveOption(new Option(repos.generateProvinceId(), mx.getOptionId(), Type.PROVINCE, Status.ACTIVE, true, new Localized("GU", "Guanajuato", "Guanajuato")));
		repos.saveOption(new Option(repos.generateProvinceId(), mx.getOptionId(), Type.PROVINCE, Status.ACTIVE, true, new Localized("HL", "Hidalgo", "Hidalgo")));
		repos.saveOption(new Option(repos.generateProvinceId(), mx.getOptionId(), Type.PROVINCE, Status.ACTIVE, true, new Localized("JL", "Jalisco", "Jalisco")));
		repos.saveOption(new Option(repos.generateProvinceId(), mx.getOptionId(), Type.PROVINCE, Status.ACTIVE, true, new Localized("MC", "Michoacan de Ocampo", "Michoacan de Ocampo")));
		repos.saveOption(new Option(repos.generateProvinceId(), mx.getOptionId(), Type.PROVINCE, Status.ACTIVE, true, new Localized("MR", "Morelos", "Morelos")));
		repos.saveOption(new Option(repos.generateProvinceId(), mx.getOptionId(), Type.PROVINCE, Status.ACTIVE, true, new Localized("MX", "Mexico (State)", "Mexico (Ètat)")));
		repos.saveOption(new Option(repos.generateProvinceId(), mx.getOptionId(), Type.PROVINCE, Status.ACTIVE, true, new Localized("NA", "Nayarit", "Nayarit")));
		repos.saveOption(new Option(repos.generateProvinceId(), mx.getOptionId(), Type.PROVINCE, Status.ACTIVE, true, new Localized("NL", "Nuevo Leon", "Nuevo Leon")));
		repos.saveOption(new Option(repos.generateProvinceId(), mx.getOptionId(), Type.PROVINCE, Status.ACTIVE, true, new Localized("OA", "Oaxaca", "Oaxaca")));
		repos.saveOption(new Option(repos.generateProvinceId(), mx.getOptionId(), Type.PROVINCE, Status.ACTIVE, true, new Localized("PB", "Puebla", "Puebla")));
		repos.saveOption(new Option(repos.generateProvinceId(), mx.getOptionId(), Type.PROVINCE, Status.ACTIVE, true, new Localized("QR", "Quintana Roo", "Quintana Roo")));
		repos.saveOption(new Option(repos.generateProvinceId(), mx.getOptionId(), Type.PROVINCE, Status.ACTIVE, true, new Localized("QU", "Queretaro de Arteaga", "Queretaro de Arteaga")));
		repos.saveOption(new Option(repos.generateProvinceId(), mx.getOptionId(), Type.PROVINCE, Status.ACTIVE, true, new Localized("SI", "Sinaloa", "Sinaloa")));
		repos.saveOption(new Option(repos.generateProvinceId(), mx.getOptionId(), Type.PROVINCE, Status.ACTIVE, true, new Localized("SL", "San Luis Potosi", "San Luis Potosi")));
		repos.saveOption(new Option(repos.generateProvinceId(), mx.getOptionId(), Type.PROVINCE, Status.ACTIVE, true, new Localized("SO", "Sonora", "Sonora")));
		repos.saveOption(new Option(repos.generateProvinceId(), mx.getOptionId(), Type.PROVINCE, Status.ACTIVE, true, new Localized("TA", "Tamaulipas", "Tamaulipas")));
		repos.saveOption(new Option(repos.generateProvinceId(), mx.getOptionId(), Type.PROVINCE, Status.ACTIVE, true, new Localized("TB", "Tabasco", "Tabasco")));
		repos.saveOption(new Option(repos.generateProvinceId(), mx.getOptionId(), Type.PROVINCE, Status.ACTIVE, true, new Localized("TL", "Tlaxcala", "Tlaxcala")));
		repos.saveOption(new Option(repos.generateProvinceId(), mx.getOptionId(), Type.PROVINCE, Status.ACTIVE, true, new Localized("VC", "Veracruz-Llave", "Veracruz-Llave")));
		repos.saveOption(new Option(repos.generateProvinceId(), mx.getOptionId(), Type.PROVINCE, Status.ACTIVE, true, new Localized("YU", "Yucatan", "Yucatan")));
		repos.saveOption(new Option(repos.generateProvinceId(), mx.getOptionId(), Type.PROVINCE, Status.ACTIVE, true, new Localized("ZA", "Zacatecas", "Zacatecas")));
	}
	
}
