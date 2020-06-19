package ca.magex.crm.api.services;

import java.io.OutputStream;

import ca.magex.crm.api.Crm;
import ca.magex.crm.api.common.PersonName;
import ca.magex.crm.api.filters.LookupsFilter;
import ca.magex.crm.api.filters.OptionsFilter;
import ca.magex.crm.api.repositories.CrmRepositories;
import ca.magex.crm.api.roles.Group;
import ca.magex.crm.api.roles.Role;
import ca.magex.crm.api.roles.User;
import ca.magex.crm.api.system.Identifier;
import ca.magex.crm.api.system.Lang;
import ca.magex.crm.api.system.Localized;
import ca.magex.crm.api.system.Lookup;
import ca.magex.crm.api.system.Option;
import ca.magex.crm.api.system.Status;

public interface CrmConfigurationService {

	boolean isInitialized();

	User initializeSystem(String organization, PersonName name, String email, String username, String password);
	
	public boolean reset();
	
	public void dump(OutputStream os);
	
	/**
	 * Default initialization of the system into the given repositories
	 * @param repos
	 */
	default void initialize(CrmRepositories repos) {
		createSysGroup(repos);
		createAppGroup(repos);
		createCrmGroup(repos);
		createOrgGroup(repos);
		createCountriesLookup(repos);
		createCanadianProvinces(repos);
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
		Identifier groupId = repos.saveGroup(new Group(repos.generateId(), Status.ACTIVE, new Localized("SYS", "System", "Système"))).getGroupId();
		repos.saveRole(new Role(repos.generateId(), groupId, Status.ACTIVE, new Localized("SYS_ADMIN", "System Administrator", "Adminstrator du système")));
		repos.saveRole(new Role(repos.generateId(), groupId, Status.ACTIVE, new Localized("SYS_ACTUATOR", "System Actuator", "Actuator du système")));
		repos.saveRole(new Role(repos.generateId(), groupId, Status.ACTIVE, new Localized("SYS_ACCESS", "System Access", "Access du système")));
	}

	/**
	 * Create the default application group for applications to create background requests
	 * <ul>
	 * 	<li>APP_AUTH_REQUEST - Token verification (application background user)</li>
	 * </ul>
	 * @param repos
	 */
	default void createAppGroup(CrmRepositories repos) {
		Identifier groupId = repos.saveGroup(new Group(repos.generateId(), Status.ACTIVE, new Localized("APP", "Application", "Application"))).getGroupId();
		repos.saveRole(new Role(repos.generateId(), groupId, Status.ACTIVE, new Localized("APP_AUTH_REQUEST", "Authorization Requestor", "Demandeur d'Autorisation")));
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
		Identifier groupId = repos.saveGroup(new Group(repos.generateId(), Status.ACTIVE, new Localized("CRM", "Customer Relationship Management", "Gestion de la relation client"))).getGroupId();
		repos.saveRole(new Role(repos.generateId(), groupId, Status.ACTIVE, new Localized("CRM_ADMIN", "CRM Admin", "Administrateur GRC")));
		repos.saveRole(new Role(repos.generateId(), groupId, Status.ACTIVE, new Localized("CRM_USER", "CRM Viewer", "Visionneuse GRC")));
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
		Identifier groupId = repos.saveGroup(new Group(repos.generateId(), Status.ACTIVE, new Localized("ORG", "Organization", "Organisation"))).getGroupId();
		repos.saveRole(new Role(repos.generateId(), groupId, Status.ACTIVE, new Localized("ORG_ADMIN", "Organization Admin", "Administrateur GRC")));
		repos.saveRole(new Role(repos.generateId(), groupId, Status.ACTIVE, new Localized("ORG_USER", "Organization Viewer", "Visionneuse GRC")));
	}

	/**
	 * Create a default set of statuses that are immutable.
	 * @param repos
	 */
	default void createStatusLookup(CrmRepositories repos) {
		Identifier lookupId = repos.saveLookup(new Lookup(repos.generateId(), Status.ACTIVE, false, new Localized(Crm.STATUSES_LOOKUP, "Statuses", "Statuts"), null)).getLookupId();
		repos.saveOption(new Option(repos.generateId(), lookupId, Status.ACTIVE, Status.ACTIVE.getName()));
		repos.saveOption(new Option(repos.generateId(), lookupId, Status.ACTIVE, Status.INACTIVE.getName()));
		repos.saveOption(new Option(repos.generateId(), lookupId, Status.ACTIVE, Status.PENDING.getName()));
	}
	
	/**
	 * Create a default set of statuses that are immutable.
	 * @param repos
	 */
	default void createLocaleLookup(CrmRepositories repos) {
		Identifier lookupId = repos.saveLookup(new Lookup(repos.generateId(), Status.ACTIVE, false, new Localized(Crm.LOCALES_LOOKUP, "Locales", "Locaux"), null)).getLookupId();
		repos.saveOption(new Option(repos.generateId(), lookupId, Status.ACTIVE, Lang.NAMES.get(Lang.ROOT)));
		repos.saveOption(new Option(repos.generateId(), lookupId, Status.ACTIVE, Lang.NAMES.get(Lang.ENGLISH)));
		repos.saveOption(new Option(repos.generateId(), lookupId, Status.ACTIVE, Lang.NAMES.get(Lang.FRENCH)));
	}
	
	/**
	 * Create a default set of statuses that are immutable.
	 * @param repos
	 */
	default void createSalutationsLookup(CrmRepositories repos) {
		Identifier lookupId = repos.saveLookup(new Lookup(repos.generateId(), Status.ACTIVE, false, new Localized(Crm.SALUTATION_LOOKUP, "Salutations", "Salutations"), null)).getLookupId();
		repos.saveOption(new Option(repos.generateId(), lookupId, Status.ACTIVE, new Localized("1", "Miss", "Mlle.")));
		repos.saveOption(new Option(repos.generateId(), lookupId, Status.ACTIVE, new Localized("2", "Mrs.", "Mme.")));
		repos.saveOption(new Option(repos.generateId(), lookupId, Status.ACTIVE, new Localized("3", "Mr.", "M.")));
	}
	
	/**
	 * Create a default set of countries for the system that can be extended to a larger set later
	 * @param repos
	 */
	default void createCountriesLookup(CrmRepositories repos) {
		Identifier lookupId = repos.saveLookup(new Lookup(repos.generateId(), Status.ACTIVE, true, new Localized(Crm.COUNTRY_LOOKUP, "Countries", "Des pays"), null)).getLookupId();
		repos.saveOption(new Option(repos.generateId(), lookupId, Status.ACTIVE, new Localized("CA", "Canada", "Canada")));
		repos.saveOption(new Option(repos.generateId(), lookupId, Status.ACTIVE, new Localized("GB", "United Kingdom", "Royaume-Uni")));
		repos.saveOption(new Option(repos.generateId(), lookupId, Status.ACTIVE, new Localized("DE", "Germany", "Allemagne")));
		repos.saveOption(new Option(repos.generateId(), lookupId, Status.ACTIVE, new Localized("FR", "France", "France")));
		repos.saveOption(new Option(repos.generateId(), lookupId, Status.ACTIVE, new Localized("IT", "Italy", "Italie")));
		repos.saveOption(new Option(repos.generateId(), lookupId, Status.ACTIVE, new Localized("MX", "Mexico", "Mexique")));
		repos.saveOption(new Option(repos.generateId(), lookupId, Status.ACTIVE, new Localized("US", "United States", "États-Unis d'Amérique")));
	}
	
	/**
	 * Create a set of Canadian Provinces
	 * @param repos
	 */
	default void createCanadianProvinces(CrmRepositories repos) {
		Lookup countries = repos.findLookups(new LookupsFilter().withLookupCode(Crm.COUNTRY_LOOKUP), LookupsFilter.getDefaultPaging()).getSingleItem();
		Option ca = repos.findOptions(new OptionsFilter().withLookupId(countries.getLookupId()).withOptionCode("CA"), OptionsFilter.getDefaultPaging()).getSingleItem();
		Identifier lookupId = repos.saveLookup(new Lookup(repos.generateId(), Status.ACTIVE, true, new Localized(Crm.CA_PROVINCE_LOOKUP, "Canadian Provinces", "Provinces canadiennes"), ca)).getLookupId();
		repos.saveOption(new Option(repos.generateId(), lookupId, Status.ACTIVE, new Localized("AB", "Alberta", "Alberta")));
		repos.saveOption(new Option(repos.generateId(), lookupId, Status.ACTIVE, new Localized("BC", "British Columbia", "Colombie-Britannique")));
		repos.saveOption(new Option(repos.generateId(), lookupId, Status.ACTIVE, new Localized("MB", "Manitoba", "Manitoba")));
		repos.saveOption(new Option(repos.generateId(), lookupId, Status.ACTIVE, new Localized("NB", "New Brunswick", "Nouveau-Brunswick")));
		repos.saveOption(new Option(repos.generateId(), lookupId, Status.ACTIVE, new Localized("NL", "Newfoundland and Labrador", "Terre-Neuve et Labrador")));
		repos.saveOption(new Option(repos.generateId(), lookupId, Status.ACTIVE, new Localized("NS", "Nova Scotia", "Nouvelle-Ècosse")));
		repos.saveOption(new Option(repos.generateId(), lookupId, Status.ACTIVE, new Localized("NT", "Northwest Territories", "Territoires du Nord-Ouest")));
		repos.saveOption(new Option(repos.generateId(), lookupId, Status.ACTIVE, new Localized("NU", "Nunavut", "Nunavut")));
		repos.saveOption(new Option(repos.generateId(), lookupId, Status.ACTIVE, new Localized("ON", "Ontario", "Ontario")));
		repos.saveOption(new Option(repos.generateId(), lookupId, Status.ACTIVE, new Localized("PE", "Prince Edward Island", "Île-du-Prince-Èdouard")));
		repos.saveOption(new Option(repos.generateId(), lookupId, Status.ACTIVE, new Localized("QC", "Quebec", "Québec")));
		repos.saveOption(new Option(repos.generateId(), lookupId, Status.ACTIVE, new Localized("SK", "Saskatchewan", "Saskatchewan")));
		repos.saveOption(new Option(repos.generateId(), lookupId, Status.ACTIVE, new Localized("YT", "Yukon", "Yukon")));
	}
	
	/**
	 * Create a set of American States
	 * @param repos
	 */
	default void createAmericanStates(CrmRepositories repos) {
		Lookup countries = repos.findLookups(new LookupsFilter().withLookupCode(Crm.COUNTRY_LOOKUP), LookupsFilter.getDefaultPaging()).getSingleItem();
		Option us = repos.findOptions(new OptionsFilter().withLookupId(countries.getLookupId()).withOptionCode("US"), OptionsFilter.getDefaultPaging()).getSingleItem();
		Identifier lookupId = repos.saveLookup(new Lookup(repos.generateId(), Status.ACTIVE, true, new Localized(Crm.US_PROVINCE_LOOKUP, "America States", "États d'Amérique"), us)).getLookupId();
		repos.saveOption(new Option(repos.generateId(), lookupId, Status.ACTIVE, new Localized("AK", "Alaska", "Alaska")));
		repos.saveOption(new Option(repos.generateId(), lookupId, Status.ACTIVE, new Localized("AL", "Alabama", "Alabama")));
		repos.saveOption(new Option(repos.generateId(), lookupId, Status.ACTIVE, new Localized("AR", "Arkansas", "Arkansas")));
		repos.saveOption(new Option(repos.generateId(), lookupId, Status.ACTIVE, new Localized("AZ", "Arizona", "Arizona")));
		repos.saveOption(new Option(repos.generateId(), lookupId, Status.ACTIVE, new Localized("CA", "California", "Californie")));
		repos.saveOption(new Option(repos.generateId(), lookupId, Status.ACTIVE, new Localized("CO", "Colorado", "Colorado")));
		repos.saveOption(new Option(repos.generateId(), lookupId, Status.ACTIVE, new Localized("CT", "Connecticut", "Connecticut")));
		repos.saveOption(new Option(repos.generateId(), lookupId, Status.ACTIVE, new Localized("DC", "District of Columbia", "District fédéral de Columbia")));
		repos.saveOption(new Option(repos.generateId(), lookupId, Status.ACTIVE, new Localized("DE", "Delaware", "Delaware")));
		repos.saveOption(new Option(repos.generateId(), lookupId, Status.ACTIVE, new Localized("FL", "Florida", "Floride")));
		repos.saveOption(new Option(repos.generateId(), lookupId, Status.ACTIVE, new Localized("GA", "Georgia", "Géorgie")));
		repos.saveOption(new Option(repos.generateId(), lookupId, Status.ACTIVE, new Localized("HI", "Hawaii", "Hawai")));
		repos.saveOption(new Option(repos.generateId(), lookupId, Status.ACTIVE, new Localized("IA", "Iowa", "Iowa")));
		repos.saveOption(new Option(repos.generateId(), lookupId, Status.ACTIVE, new Localized("ID", "Idaho", "Idaho")));
		repos.saveOption(new Option(repos.generateId(), lookupId, Status.ACTIVE, new Localized("IL", "Illinois", "Illinois")));
		repos.saveOption(new Option(repos.generateId(), lookupId, Status.ACTIVE, new Localized("IN", "Indiana", "Indiana")));
		repos.saveOption(new Option(repos.generateId(), lookupId, Status.ACTIVE, new Localized("KS", "Kansas", "Kansas")));
		repos.saveOption(new Option(repos.generateId(), lookupId, Status.ACTIVE, new Localized("KY", "Kentucky", "Kentucky")));
		repos.saveOption(new Option(repos.generateId(), lookupId, Status.ACTIVE, new Localized("LA", "Louisiana", "Louisiane")));
		repos.saveOption(new Option(repos.generateId(), lookupId, Status.ACTIVE, new Localized("MA", "Massachusetts", "Massachusetts")));
		repos.saveOption(new Option(repos.generateId(), lookupId, Status.ACTIVE, new Localized("MD", "Maryland", "Maryland")));
		repos.saveOption(new Option(repos.generateId(), lookupId, Status.ACTIVE, new Localized("ME", "Maine", "Maine")));
		repos.saveOption(new Option(repos.generateId(), lookupId, Status.ACTIVE, new Localized("MI", "Michigan", "Michigan")));
		repos.saveOption(new Option(repos.generateId(), lookupId, Status.ACTIVE, new Localized("MN", "Minnesota", "Minnesota")));
		repos.saveOption(new Option(repos.generateId(), lookupId, Status.ACTIVE, new Localized("MO", "Missouri", "Missouri")));
		repos.saveOption(new Option(repos.generateId(), lookupId, Status.ACTIVE, new Localized("MS", "Mississippi", "Mississippi")));
		repos.saveOption(new Option(repos.generateId(), lookupId, Status.ACTIVE, new Localized("MT", "Montana", "Montana")));
		repos.saveOption(new Option(repos.generateId(), lookupId, Status.ACTIVE, new Localized("NC", "North Carolina", "Caroline du Nord")));
		repos.saveOption(new Option(repos.generateId(), lookupId, Status.ACTIVE, new Localized("ND", "North Dakota", "Dakota du Nord")));
		repos.saveOption(new Option(repos.generateId(), lookupId, Status.ACTIVE, new Localized("NE", "Nebraska", "Nebraska")));
		repos.saveOption(new Option(repos.generateId(), lookupId, Status.ACTIVE, new Localized("NH", "New Hampshire", "New Hampshire")));
		repos.saveOption(new Option(repos.generateId(), lookupId, Status.ACTIVE, new Localized("NJ", "New Jersey", "New Jersey")));
		repos.saveOption(new Option(repos.generateId(), lookupId, Status.ACTIVE, new Localized("NM", "New Mexico", "Nouveau-Mexique")));
		repos.saveOption(new Option(repos.generateId(), lookupId, Status.ACTIVE, new Localized("NV", "Nevada", "Nevada")));
		repos.saveOption(new Option(repos.generateId(), lookupId, Status.ACTIVE, new Localized("NY", "New York", "New York")));
		repos.saveOption(new Option(repos.generateId(), lookupId, Status.ACTIVE, new Localized("OH", "Ohio", "Ohio")));
		repos.saveOption(new Option(repos.generateId(), lookupId, Status.ACTIVE, new Localized("OK", "Oklahoma", "Oklahoma")));
		repos.saveOption(new Option(repos.generateId(), lookupId, Status.ACTIVE, new Localized("OR", "Oregon", "Orégon")));
		repos.saveOption(new Option(repos.generateId(), lookupId, Status.ACTIVE, new Localized("PA", "Pennsylvania", "Pennsylvanie")));
		repos.saveOption(new Option(repos.generateId(), lookupId, Status.ACTIVE, new Localized("RI", "Rhode Island", "Rhode Island")));
		repos.saveOption(new Option(repos.generateId(), lookupId, Status.ACTIVE, new Localized("SC", "South Carolina", "Carolina du Sud")));
		repos.saveOption(new Option(repos.generateId(), lookupId, Status.ACTIVE, new Localized("SD", "South Dakota", "Dakota du Sud")));
		repos.saveOption(new Option(repos.generateId(), lookupId, Status.ACTIVE, new Localized("TN", "Tennessee", "Tennessee")));
		repos.saveOption(new Option(repos.generateId(), lookupId, Status.ACTIVE, new Localized("TX", "Texas", "Texas")));
		repos.saveOption(new Option(repos.generateId(), lookupId, Status.ACTIVE, new Localized("UT", "Utah", "Utah")));
		repos.saveOption(new Option(repos.generateId(), lookupId, Status.ACTIVE, new Localized("VA", "Virginia", "Virginie")));
		repos.saveOption(new Option(repos.generateId(), lookupId, Status.ACTIVE, new Localized("VT", "Vermont", "Vermont")));
		repos.saveOption(new Option(repos.generateId(), lookupId, Status.ACTIVE, new Localized("WA", "Washington", "Washington")));
		repos.saveOption(new Option(repos.generateId(), lookupId, Status.ACTIVE, new Localized("WI", "Wisconsin", "Wisconsin")));
		repos.saveOption(new Option(repos.generateId(), lookupId, Status.ACTIVE, new Localized("WV", "West Virginia", "Virginie-Occidentale")));
		repos.saveOption(new Option(repos.generateId(), lookupId, Status.ACTIVE, new Localized("WY", "Wyoming", "Wyoming")));
	}

	/**
	 * Create a set of Mexican Provinces
	 * @param repos
	 */
	default void createMexicanProvinces(CrmRepositories repos) {
		Lookup countries = repos.findLookups(new LookupsFilter().withLookupCode(Crm.COUNTRY_LOOKUP), LookupsFilter.getDefaultPaging()).getSingleItem();
		Option mx = repos.findOptions(new OptionsFilter().withLookupId(countries.getLookupId()).withOptionCode("MX"), OptionsFilter.getDefaultPaging()).getSingleItem();
		Identifier lookupId = repos.saveLookup(new Lookup(repos.generateId(), Status.ACTIVE, true, new Localized(Crm.MX_PROVINCE_LOOKUP, "Mexican Provinces", "Provinces mexicaines"), mx)).getLookupId();
		repos.saveOption(new Option(repos.generateId(), lookupId, Status.ACTIVE, new Localized("AG", "Aguascalientas", "Aguascalientas")));
		repos.saveOption(new Option(repos.generateId(), lookupId, Status.ACTIVE, new Localized("BA", "Baja California (North)", "Baja California (Nord)")));
		repos.saveOption(new Option(repos.generateId(), lookupId, Status.ACTIVE, new Localized("BJ", "Baja California (South)", "Baja California (Sud)")));
		repos.saveOption(new Option(repos.generateId(), lookupId, Status.ACTIVE, new Localized("CE", "Campeche", "Campeche")));
		repos.saveOption(new Option(repos.generateId(), lookupId, Status.ACTIVE, new Localized("CH", "Chihuahua", "Chihuahua")));
		repos.saveOption(new Option(repos.generateId(), lookupId, Status.ACTIVE, new Localized("CI", "Chiapas", "Chiapas")));
		repos.saveOption(new Option(repos.generateId(), lookupId, Status.ACTIVE, new Localized("CL", "Colima", "Colima")));
		repos.saveOption(new Option(repos.generateId(), lookupId, Status.ACTIVE, new Localized("CU", "Coahuila de Zaragoza", "Coahuila de Zaragoza")));
		repos.saveOption(new Option(repos.generateId(), lookupId, Status.ACTIVE, new Localized("DF", "Distrito", "Distrito (Federal)")));
		repos.saveOption(new Option(repos.generateId(), lookupId, Status.ACTIVE, new Localized("DO", "Durango", "Durango")));
		repos.saveOption(new Option(repos.generateId(), lookupId, Status.ACTIVE, new Localized("GR", "Guerreo", "Guerreo")));
		repos.saveOption(new Option(repos.generateId(), lookupId, Status.ACTIVE, new Localized("GU", "Guanajuato", "Guanajuato")));
		repos.saveOption(new Option(repos.generateId(), lookupId, Status.ACTIVE, new Localized("HL", "Hidalgo", "Hidalgo")));
		repos.saveOption(new Option(repos.generateId(), lookupId, Status.ACTIVE, new Localized("JL", "Jalisco", "Jalisco")));
		repos.saveOption(new Option(repos.generateId(), lookupId, Status.ACTIVE, new Localized("MC", "Michoacan de Ocampo", "Michoacan de Ocampo")));
		repos.saveOption(new Option(repos.generateId(), lookupId, Status.ACTIVE, new Localized("MR", "Morelos", "Morelos")));
		repos.saveOption(new Option(repos.generateId(), lookupId, Status.ACTIVE, new Localized("MX", "Mexico (State)", "Mexico (Ètat)")));
		repos.saveOption(new Option(repos.generateId(), lookupId, Status.ACTIVE, new Localized("NA", "Nayarit", "Nayarit")));
		repos.saveOption(new Option(repos.generateId(), lookupId, Status.ACTIVE, new Localized("NL", "Nuevo Leon", "Nuevo Leon")));
		repos.saveOption(new Option(repos.generateId(), lookupId, Status.ACTIVE, new Localized("OA", "Oaxaca", "Oaxaca")));
		repos.saveOption(new Option(repos.generateId(), lookupId, Status.ACTIVE, new Localized("PB", "Puebla", "Puebla")));
		repos.saveOption(new Option(repos.generateId(), lookupId, Status.ACTIVE, new Localized("QR", "Quintana Roo", "Quintana Roo")));
		repos.saveOption(new Option(repos.generateId(), lookupId, Status.ACTIVE, new Localized("QU", "Queretaro de Arteaga", "Queretaro de Arteaga")));
		repos.saveOption(new Option(repos.generateId(), lookupId, Status.ACTIVE, new Localized("SI", "Sinaloa", "Sinaloa")));
		repos.saveOption(new Option(repos.generateId(), lookupId, Status.ACTIVE, new Localized("SL", "San Luis Potosi", "San Luis Potosi")));
		repos.saveOption(new Option(repos.generateId(), lookupId, Status.ACTIVE, new Localized("SO", "Sonora", "Sonora")));
		repos.saveOption(new Option(repos.generateId(), lookupId, Status.ACTIVE, new Localized("TA", "Tamaulipas", "Tamaulipas")));
		repos.saveOption(new Option(repos.generateId(), lookupId, Status.ACTIVE, new Localized("TB", "Tabasco", "Tabasco")));
		repos.saveOption(new Option(repos.generateId(), lookupId, Status.ACTIVE, new Localized("TL", "Tlaxcala", "Tlaxcala")));
		repos.saveOption(new Option(repos.generateId(), lookupId, Status.ACTIVE, new Localized("VC", "Veracruz-Llave", "Veracruz-Llave")));
		repos.saveOption(new Option(repos.generateId(), lookupId, Status.ACTIVE, new Localized("YU", "Yucatan", "Yucatan")));
		repos.saveOption(new Option(repos.generateId(), lookupId, Status.ACTIVE, new Localized("ZA", "Zacatecas", "Zacatecas")));
	}

}
