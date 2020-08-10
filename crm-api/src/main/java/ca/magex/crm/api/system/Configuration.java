package ca.magex.crm.api.system;

import java.io.Serializable;
import java.time.LocalDateTime;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import ca.magex.crm.api.Crm;
import ca.magex.crm.api.system.id.ConfigurationIdentifier;

public class Configuration implements Serializable {

	private static final long serialVersionUID = Crm.SERIAL_UID_VERSION;
	
	/** a unique identifier for the user within the system */
	private ConfigurationIdentifier configurationId;
	
	private LocalDateTime modified;
	
	private Status status;
	
	public Configuration(ConfigurationIdentifier configurationId, Status status, LocalDateTime modified) {
		this.configurationId = configurationId;
		this.status = status;
		this.modified = modified == null ? LocalDateTime.now() : modified;
	}
	
	public ConfigurationIdentifier getConfigurationId() {
		return configurationId;
	}
	
	public Configuration withConfigurationId(ConfigurationIdentifier configurationId) {
		return new Configuration(configurationId, status, LocalDateTime.now());
	}

	public Status getStatus() {
		return status;
	}
	
	public Configuration withStatus(Status status) {
		return new Configuration(configurationId, status, LocalDateTime.now());
	}
	
	public LocalDateTime getModified() {
		return modified;
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
