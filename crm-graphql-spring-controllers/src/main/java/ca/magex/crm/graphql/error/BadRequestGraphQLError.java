package ca.magex.crm.graphql.error;

import java.util.Collections;
import java.util.List;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import ca.magex.crm.api.Crm;
import ca.magex.crm.api.system.Message;
import graphql.ErrorClassification;
import graphql.ErrorType;
import graphql.GraphQLError;
import graphql.language.SourceLocation;

public class BadRequestGraphQLError implements GraphQLError {

	private static final long serialVersionUID = Crm.SERIAL_UID_VERSION;
	
	private Message message = null;
	
	public BadRequestGraphQLError(Message message) {
		this.message = message;
	}
	
	@Override
	public String getMessage() {
		return message.toString();
	}
	
	@Override
	public List<SourceLocation> getLocations() {
		return Collections.emptyList();
	}
	
	@Override
	public ErrorClassification getErrorType() {
		return ErrorType.ValidationError;
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
