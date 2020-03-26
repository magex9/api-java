package ca.magex.crm.graphql.error;

import ca.magex.crm.api.exceptions.ApiException;
import graphql.execution.DataFetcherExceptionHandler;
import graphql.execution.DataFetcherExceptionHandlerParameters;
import graphql.execution.SimpleDataFetcherExceptionHandler;

/**
 * DataFetcher Exception Handler which translates the ApiExceptions into GraphQLError
 * 
 * @author Jonny
 *
 */
public class ApiDataFetcherExceptionHandler implements DataFetcherExceptionHandler {
	
	private DataFetcherExceptionHandler defaultFetcherExceptionHandler = new SimpleDataFetcherExceptionHandler();
	
	@Override
	public void accept(DataFetcherExceptionHandlerParameters handlerParameters) {
		/* we want to treat our ApiExceptions specially */
		if (handlerParameters.getException() instanceof ApiException) {
			handlerParameters.getExecutionContext().addError(new ApiGraphQLError((ApiException) handlerParameters.getException()));
		} else {
			defaultFetcherExceptionHandler.accept(handlerParameters);
		}
	}	
}
