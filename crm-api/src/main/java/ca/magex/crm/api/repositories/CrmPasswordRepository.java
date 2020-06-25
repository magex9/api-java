package ca.magex.crm.api.repositories;

import ca.magex.crm.api.authentication.CrmPasswordDetails;

/**
 * Repository interface used for saving/retrieving password details
 * 
 * @author Jonny
 */
public interface CrmPasswordRepository {

	/**
	 * returns a randomly generated password
	 * 
	 * @return
	 */
	public String generateTemporaryPassword();
	
	/**
	 * Save the given password details to the repository
	 * 
	 * @param passwordDetails
	 * @return
	 */
	public CrmPasswordDetails savePasswordDetails(CrmPasswordDetails passwordDetails);
	
	/**
	 * returns the password details for the given username
	 * 
	 * @param username
	 * @return
	 */
	public CrmPasswordDetails findPasswordDetails(String username);
	
	
}
