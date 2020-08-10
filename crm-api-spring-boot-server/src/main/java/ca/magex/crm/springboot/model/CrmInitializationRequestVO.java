package ca.magex.crm.springboot.model;

import java.io.Serializable;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

/**
 * Value Object used for the one time initialization of the CRM System
 * 
 * @author Jonny
 */
public class CrmInitializationRequestVO implements Serializable {
	
	private static final long serialVersionUID = -2444662392416781082L;
	
	private String organizationName;
	private String ownerGivenName;
	private String ownerMiddleName;
	private String ownerSurname;
	private String ownerEmail;
	private String ownerTelephone;
	private String ownerFax;
	private String username;
	private String password;

	@NotBlank
	@Size(max=100)
	public String getOrganizationName() {
		return organizationName;
	}

	public void setOrganizationName(String organizationName) {
		this.organizationName = organizationName;
	}

	@NotBlank
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

	@NotBlank
	public String getOwnerSurname() {
		return ownerSurname;
	}

	public void setOwnerSurname(String ownerSurname) {
		this.ownerSurname = ownerSurname;
	}

	@NotBlank
	@Email
	public String getOwnerEmail() {
		return ownerEmail;
	}

	public void setOwnerEmail(String ownerEmail) {
		this.ownerEmail = ownerEmail;
	}
	
	@NotBlank
	@Pattern(regexp = "(^$|[0-9]{10})")
	public String getOwnerTelephone() {
		return ownerTelephone;
	}
	
	public void setOwnerTelephone(String ownerTelephone) {
		this.ownerTelephone = ownerTelephone;
	}
	
	@Pattern(regexp = "(^$|[0-9]{10})")
	public String getOwnerFax() {
		return ownerFax;
	}
	
	public void setOwnerFax(String ownerFax) {
		this.ownerFax = ownerFax;
	}

	@Size(min = 5, max = 20)
	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	@Size(min = 5, max = 20)
	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
}
