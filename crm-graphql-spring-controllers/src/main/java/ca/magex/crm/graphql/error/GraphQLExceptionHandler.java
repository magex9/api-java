package ca.magex.crm.graphql.error;

import java.util.stream.Collectors;

import ca.magex.crm.api.exceptions.ApiException;
import ca.magex.crm.api.exceptions.BadRequestException;
import graphql.GraphQLError;
import graphql.execution.DataFetcherExceptionHandler;
import graphql.execution.DataFetcherExceptionHandlerParameters;
import graphql.execution.DataFetcherExceptionHandlerResult;
import graphql.execution.SimpleDataFetcherExceptionHandler;

/**
 * DataFetcher Exception Handler which translates the ApiExceptions into GraphQLError
 * 
 * @author Jonny
 *
 */
public class GraphQLExceptionHandler implements DataFetcherExceptionHandler {

	private DataFetcherExceptionHandler delegate = new SimpleDataFetcherExceptionHandler();
	
	public GraphQLExceptionHandler(DataFetcherExceptionHandler delegate) {
		this.delegate = delegate;
	}
	
	@Override
	public DataFetcherExceptionHandlerResult onException(DataFetcherExceptionHandlerParameters handlerParameters) {
		/* we want to treat our ApiExceptions specially */
		if (handlerParameters.getException() instanceof BadRequestException) {
			BadRequestException bre = (BadRequestException) handlerParameters.getException();
			return DataFetcherExceptionHandlerResult.newResult().errors(bre.getMessages().stream().map(BadRequestGraphQLError::new).collect(Collectors.toList())).build();
		}
		if (handlerParameters.getException() instanceof ApiException) {
			GraphQLError error = new ApiGraphQLError((ApiException) handlerParameters.getException());
			return DataFetcherExceptionHandlerResult.newResult(error).build();
		} else {
			return delegate.onException(handlerParameters);
		}
	}	
}
