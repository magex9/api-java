package ca.magex.crm.springboot.model;

/**
 * Value Object used for the one time initialization of the CRM System
 * 
 * @author Jonny
 */
public class CrmInitializationRequestVO {
	
	private String organizationName;	
	private String ownerGivenName;	
	private String ownerMiddleName;	
	private String ownerSurname;	
	private String ownerEmail;	
	private String username;	
	private String password;
	
	public String getOrganizationName() {
		return organizationName;
	}
	
	public void setOrganizationName(String organizationName) {
		this.organizationName = organizationName;
	}
	
	public String getOwnerGivenName() {
		return ownerGivenName;
	}
	
	public void setOwnerGivenName(String ownerGivenName) {
		this.ownerGivenName = ownerGivenName;
	}
	
	public String getOwnerMiddleName() {
		return ownerMiddleName;
	}
	
	public void setOwnerMiddleName(String ownerMiddleName) {
		this.ownerMiddleName = ownerMiddleName;
	}
	
	public String getOwnerSurname() {
		return ownerSurname;
	}
	
	public void setOwnerSurname(String ownerSurname) {
		this.ownerSurname = ownerSurname;
	}
	
	public String getOwnerEmail() {
		return ownerEmail;
	}
	
	public void setOwnerEmail(String ownerEmail) {
		this.ownerEmail = ownerEmail;
	}
	
	public String getUsername() {
		return username;
	}
	
	public void setUsername(String username) {
		this.username = username;
	}
	
	public String getPassword() {
		return password;
	}
	
	public void setPassword(String password) {
		this.password = password;
	}
}
