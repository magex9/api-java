package ca.magex.crm.api.authentication;

import java.io.Serializable;
import java.util.Date;
import java.util.Stack;

import org.apache.commons.lang3.SerializationUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import ca.magex.crm.api.services.Crm;

public class PasswordDetails implements Serializable {

	private static final long serialVersionUID = Crm.SERIAL_UID_VERSION;

	private Stack<String> cipherText;
	private boolean temporary;
	private Date expiration;
	
	public PasswordDetails(String cipherText, boolean temporary, Date expiration) {
		this.cipherText = new Stack<>();
		this.cipherText.push(cipherText);
		this.temporary = temporary;
		this.expiration = expiration;
	}

	public boolean isTemporary() {
		return temporary;
	}
	
	public Date getExpiration() {
		return expiration;
	}
	
	public String getCipherText() {		
		return cipherText.peek();
	}
	
	public PasswordDetails withPassword(String cipherText) {
		PasswordDetails details = SerializationUtils.clone(this);
		details.cipherText.push(cipherText);
		details.temporary = false;
		details.expiration = null;
		return details;
	}
	
	public PasswordDetails withTemporaryPassword(String cipherText, Date expiration) {
		PasswordDetails details = SerializationUtils.clone(this);
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
}
