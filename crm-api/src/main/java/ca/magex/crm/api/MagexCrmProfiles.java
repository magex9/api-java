package ca.magex.crm.api;

public class MagexCrmProfiles {
	
	// Spring Security Setup Server Type
	public static final String AUTH_EMBEDDED_JWT		= "AuthEmbeddedJwt";
	public static final String AUTH_REMOTE_JWT			= "AuthRemoteJwt";
	
	// CRM Authentication Profile
	public static final String CRM_AUTH                 = "CrmAuth";
	public static final String CRM_NO_AUTH              = "CrmNoAuth";
	
	// CRM Datastore Profile
	public static final String CRM_DATASTORE_DECENTRALIZED		= "CrmDecentralizedDatastore";
	public static final String CRM_DATASTORE_CENTRALIZED		= "CrmCentralizedDatastore";
}
