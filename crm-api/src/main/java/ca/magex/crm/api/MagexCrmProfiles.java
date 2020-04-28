package ca.magex.crm.api;

public class MagexCrmProfiles {
	
	// Spring Security Setup Server Type
	public static final String AUTH_EMBEDDED_JWT		= "AuthEmbeddedJwt";
	public static final String AUTH_REMOTE_JWT			= "AuthRemoteJwt";
	
	// CRM Authentication Profile
	public static final String CRM_NO_AUTH			   	= "CrmNoAuth";
	public static final String CRM_AUTH_EMBEDDED 		= "CrmAuthEmbedded";
	public static final String CRM_AUTH_REMOTE	 		= "CrmAuthRemote";
}
