package ca.magex.crm.graphql.error;

import java.util.Collections;
import java.util.List;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import ca.magex.crm.api.Crm;
import ca.magex.crm.api.exceptions.ApiException;
import graphql.ErrorType;
import graphql.GraphQLError;
import graphql.language.SourceLocation;

/**
 * Custom GraphQL Error to wrap an ApiException
 * 
 * @author Jonny
 */
public class ApiGraphQLError implements GraphQLError {

	private static final long serialVersionUID = Crm.SERIAL_UID_VERSION;
	
	private ApiException cause = null;

	public ApiGraphQLError(ApiException cause) {
		this.cause = cause;
	}

	@Override
	public String getMessage() {
		return cause.getMessage(); 
				
	}

	@Override
	public List<SourceLocation> getLocations() {
		return Collections.emptyList();
	}

	@Override
	public ErrorType getErrorType() {
		return ErrorType.ExecutionAborted;
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