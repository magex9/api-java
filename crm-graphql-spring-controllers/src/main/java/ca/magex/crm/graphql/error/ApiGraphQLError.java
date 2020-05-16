package ca.magex.crm.graphql.error;

import java.util.Collections;
import java.util.List;

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

	private ApiException cause = null;

	public ApiGraphQLError(ApiException cause) {
		this.cause = cause;
	}

	private static final long serialVersionUID = 1L;

	@Override
	public String getMessage() {
		return cause.getErrorCode() + ": " + cause.getMessage();
	}

	@Override
	public List<SourceLocation> getLocations() {
		return Collections.emptyList();
	}

	@Override
	public ErrorType getErrorType() {
		return ErrorType.ExecutionAborted;
	}
}