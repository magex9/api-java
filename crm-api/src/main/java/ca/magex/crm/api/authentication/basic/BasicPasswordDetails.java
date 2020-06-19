package ca.magex.crm.api.authentication.basic;

import java.io.Serializable;
import java.util.Date;
import java.util.Stack;

import org.apache.commons.lang3.SerializationUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import ca.magex.crm.api.Crm;
import ca.magex.crm.api.authentication.CrmPasswordDetails;

public class BasicPasswordDetails implements Serializable, CrmPasswordDetails {

	private static final long serialVersionUID = Crm.SERIAL_UID_VERSION;

	private Stack<String> cipherText;
	private boolean temporary;
	private Date expiration;
	
	public BasicPasswordDetails(String cipherText, boolean temporary, Date expiration) {
		this.cipherText = new Stack<>();
		this.cipherText.push(cipherText);
		this.temporary = temporary;
		this.expiration = expiration;
	}

	@Override
	public boolean isTemporary() {
		return temporary;
	}
	
	@Override
	public Date getExpiration() {
		return expiration;
	}
	
	@Override
	public String getCipherText() {		
		return cipherText.peek();
	}
	
	public BasicPasswordDetails withPassword(String cipherText) {
		BasicPasswordDetails details = SerializationUtils.clone(this);
		details.cipherText.push(cipherText);
		details.temporary = false;
		details.expiration = null;
		return details;
	}
	
	public BasicPasswordDetails withTemporaryPassword(String cipherText, Date expiration) {
		BasicPasswordDetails details = SerializationUtils.clone(this);
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
