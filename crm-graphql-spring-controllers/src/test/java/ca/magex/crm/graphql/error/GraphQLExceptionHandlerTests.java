package ca.magex.crm.graphql.error;

import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import ca.magex.crm.api.exceptions.ApiException;
import ca.magex.crm.api.exceptions.BadRequestException;
import ca.magex.crm.api.system.Choice;
import ca.magex.crm.api.system.Message;
import ca.magex.crm.api.system.id.MessageTypeIdentifier;
import ca.magex.crm.api.system.id.OrganizationIdentifier;
import ca.magex.crm.api.system.id.PhraseIdentifier;
import graphql.execution.DataFetcherExceptionHandler;
import graphql.execution.DataFetcherExceptionHandlerParameters;
import graphql.execution.DataFetcherExceptionHandlerResult;

@RunWith(SpringJUnit4ClassRunner.class)
public class GraphQLExceptionHandlerTests {

	@MockBean DataFetcherExceptionHandler delegate;
	
	@Before
	public void reset() {
		Mockito.reset(delegate);
	}

	@Test
	public void testHandleBadRequestException() {
		BadRequestException bre = new BadRequestException("Error During Load", List.of(
				new Message(new OrganizationIdentifier("A"), new MessageTypeIdentifier("error"), "p1", "blank", new Choice<>("Custom 1")),
				new Message(new OrganizationIdentifier("B"), new MessageTypeIdentifier("error"), "p2", "empty", new Choice<>(new PhraseIdentifier("error")))));

		DataFetcherExceptionHandlerParameters handlerParameters = Mockito.mock(DataFetcherExceptionHandlerParameters.class);
		Mockito.when(handlerParameters.getException()).thenReturn(bre);

		GraphQLExceptionHandler exceptionHandler = new GraphQLExceptionHandler(delegate);
		DataFetcherExceptionHandlerResult result = exceptionHandler.onException(handlerParameters);
		Assert.assertEquals(2, result.getErrors().size());

		Mockito.verify(delegate, Mockito.times(0)).onException(Mockito.any());
	}

	@Test
	public void testHandleApiException() {
		ApiException ape = new ApiException("Hello World");

		DataFetcherExceptionHandlerParameters handlerParameters = Mockito.mock(DataFetcherExceptionHandlerParameters.class);
		Mockito.when(handlerParameters.getException()).thenReturn(ape);

		GraphQLExceptionHandler exceptionHandler = new GraphQLExceptionHandler(delegate);
		DataFetcherExceptionHandlerResult result = exceptionHandler.onException(handlerParameters);
		Assert.assertEquals(1, result.getErrors().size());

		Mockito.verify(delegate, Mockito.times(0)).onException(Mockito.any());
	}
	
	@Test
	public void testHandleOtherException() {
		RuntimeException rte = new RuntimeException("Hello World");

		DataFetcherExceptionHandlerParameters handlerParameters = Mockito.mock(DataFetcherExceptionHandlerParameters.class);
		Mockito.when(handlerParameters.getException()).thenReturn(rte);

		GraphQLExceptionHandler exceptionHandler = new GraphQLExceptionHandler(delegate);
		exceptionHandler.onException(handlerParameters);
		Mockito.verify(delegate, Mockito.times(1)).onException(Mockito.any());
	}
}
