package ca.magex.crm.graphql.error;

import java.util.List;
import java.util.stream.Collectors;

import ca.magex.crm.api.exceptions.ApiException;
import ca.magex.crm.api.exceptions.BadRequestException;
import ca.magex.crm.api.system.Message;
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
			List<Message> validationMessages = bre.getMessages();
			
			return DataFetcherExceptionHandlerResult
					.newResult()
					.errors(validationMessages
							.stream()
							.map((vm) -> new BadRequestGraphQLError(bre.getMessage(), vm))
							.collect(Collectors.toList())).build();
		}
		if (handlerParameters.getException() instanceof ApiException) {
			GraphQLError error = new ApiGraphQLError((ApiException) handlerParameters.getException());
			return DataFetcherExceptionHandlerResult.newResult(error).build();
		} else {
			return delegate.onException(handlerParameters);
		}
	}	
}
