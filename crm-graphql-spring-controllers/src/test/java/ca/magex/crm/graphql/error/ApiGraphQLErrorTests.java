package ca.magex.crm.graphql.error;

import org.junit.Assert;
import org.junit.Test;

import ca.magex.crm.api.exceptions.ApiException;
import graphql.ErrorType;

public class ApiGraphQLErrorTests {

	@Test
	public void testError() {
		ApiException cause = new ApiException("it smells bad");
		ApiGraphQLError error = new ApiGraphQLError(cause);
		Assert.assertEquals("it smells bad", error.getMessage());
		Assert.assertEquals(0, error.getLocations().size());
		Assert.assertEquals(ErrorType.ExecutionAborted, error.getErrorType());
		
		ApiGraphQLError error2 = new ApiGraphQLError(cause);
		Assert.assertEquals(error, error2);
		Assert.assertEquals(error.hashCode(), error2.hashCode());
		Assert.assertEquals(error.toString(), error2.toString());
	}
}
