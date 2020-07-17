package ca.magex.crm.api.authentication;

import java.io.Serializable;
import java.util.Date;
import java.util.Stack;

import org.apache.commons.lang3.SerializationUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import ca.magex.crm.api.Crm;

public class CrmPasswordDetails implements Serializable {

	private static final long serialVersionUID = Crm.SERIAL_UID_VERSION;

	private String username;
	private Stack<String> cipherText;
	private boolean temporary;
	private Date expiration;
	
	public CrmPasswordDetails(String username, String cipherText, boolean temporary, Date expiration) {
		this.username = username;
		this.cipherText = new Stack<>();
		this.cipherText.push(cipherText);		
		this.temporary = temporary;
		this.expiration = expiration;
	}
	
	public String getUsername() {
		return username;
	}

	/**
	 * returns true if the current password is a temporary password
	 * @return
	 */
	public boolean isTemporary() {
		return temporary;
	}
	
	/**
	 * returns the date the current password expires
	 * @return
	 */
	public Date getExpiration() {
		return expiration;
	}
	
	/**
	 * returns the cipher text encoded password
	 * @return
	 */
	public String getCipherText() {		
		return cipherText.peek();
	}
	
	/**
	 * returns a new password details with a given password
	 * @param cipherText
	 * @return
	 */
	public CrmPasswordDetails withPassword(String cipherText) {
		CrmPasswordDetails details = SerializationUtils.clone(this);
		details.cipherText.push(cipherText);
		details.temporary = false;
		details.expiration = null;
		return details;
	}
	
	/**
	 * returns a new password details with a generated temporary password
	 * @param cipherText
	 * @param expiration
	 * @return
	 */
	public CrmPasswordDetails withTemporaryPassword(String cipherText, Date expiration) {
		CrmPasswordDetails details = SerializationUtils.clone(this);
		details.cipherText.push(cipherText);
		details.temporary = true;
		details.expiration = expiration;
		return details;
	}
	
	@Override
	public int hashCode() {
		return HashCodeBuilder.reflectionHashCode(this);
	}
	
	@Override
	public boolean equals(Object obj) {
		return EqualsBuilder.reflectionEquals(this, obj);
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this, ToStringStyle.JSON_STYLE);
	}
	
	public static void main(String[] args) {
		;
		System.out.println(new CrmPasswordDetails("admin", new BCryptPasswordEncoder().encode("admin"), true, new Date()));
	}
}