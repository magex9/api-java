package ca.magex.crm.api;

public class MagexCrmProfiles {
	
	// Spring Security Setup Server Type
	public static final String AUTH_EMBEDDED_JWT		= "AuthEmbeddedJwt";
	public static final String AUTH_REMOTE_JWT			= "AuthRemoteJwt";
	
	// CRM Authentication Profile
	public static final String CRM_NO_AUTH_EMBEDDED	   	= "CrmNoAuthEmbedded";
	public static final String CRM_NO_AUTH_REMOTE	   	= "CrmNoAuthRemote";
	public static final String CRM_AUTH_EMBEDDED 		= "CrmAuthEmbedded";
	public static final String CRM_AUTH_REMOTE	 		= "CrmAuthRemote";
	
	// CRM Datastore Profile
	public static final String CRM_DISTRIBUTED			= "CrmDistributedDatastore";
	public static final String CRM_CENTRALIZED			= "CrmCentralizedDatastore";
}
