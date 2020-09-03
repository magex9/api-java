package ca.magex.crm.restful.models;

import java.io.Serializable;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import ca.magex.crm.api.Crm;
import ca.magex.crm.api.system.Localized;

public class RestfulAction implements Serializable {

	private static final long serialVersionUID = Crm.SERIAL_UID_VERSION;

	private String action;
	
	private Localized label;
	
	private String method;
	
	private String link;

	public RestfulAction(String action, Localized label, String method, String link) {
		super();
		this.action = action;
		this.label = label;
		this.method = method;
		this.link = link;
	}
	
	public String getAction() {
		return action;
	}
	
	public Localized getLabel() {
		return label;
	}
	
	public String getMethod() {
		return method;
	}
	
	public String getLink() {
		return link;
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
