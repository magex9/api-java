package ca.magex.crm.graphql.error;

import ca.magex.crm.api.exceptions.ApiException;
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
public class ApiDataFetcherExceptionHandler implements DataFetcherExceptionHandler {

	private SimpleDataFetcherExceptionHandler delegate = new SimpleDataFetcherExceptionHandler();
	
	@Override
	public DataFetcherExceptionHandlerResult onException(DataFetcherExceptionHandlerParameters handlerParameters) {
		/* we want to treat our ApiExceptions specially */
		if (handlerParameters.getException() instanceof ApiException) {
			GraphQLError error = new ApiGraphQLError((ApiException) handlerParameters.getException());
			return DataFetcherExceptionHandlerResult.newResult(error).build();
		} else {
			return delegate.onException(handlerParameters);
		}
	}	
}
