package ca.magex.crm.graphql.error;

import org.junit.Assert;
import org.junit.Test;

import ca.magex.crm.api.system.Identifier;
import ca.magex.crm.api.system.Localized;
import ca.magex.crm.api.system.Message;
import graphql.ErrorType;

public class BadRequestGraphQLErrorTests {

	@Test
	public void testError() {
		Message message = new Message(new Identifier("A"), "error", "path", new Localized("A", "en", "fr"));
		BadRequestGraphQLError error = new BadRequestGraphQLError(message);
		Assert.assertEquals("{\"identifier\":\"A\",\"type\":\"error\",\"path\":\"path\",\"reason\":{\"code\":\"A\",\"en\":\"en\",\"fr\":\"fr\"}}", error.getMessage());
		Assert.assertEquals(0, error.getLocations().size());
		Assert.assertEquals(ErrorType.ValidationError, error.getErrorType());
		
		BadRequestGraphQLError error2 = new BadRequestGraphQLError(message);
		Assert.assertEquals(error, error2);
		Assert.assertEquals(error.hashCode(), error2.hashCode());
		Assert.assertEquals(error.toString(), error2.toString());
	}
}