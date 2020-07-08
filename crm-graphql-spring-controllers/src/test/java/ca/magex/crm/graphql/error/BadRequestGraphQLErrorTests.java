package ca.magex.crm.graphql.error;

import org.junit.Assert;
import org.junit.Test;

import ca.magex.crm.api.system.Choice;
import ca.magex.crm.api.system.Message;
import ca.magex.crm.api.system.id.MessageTypeIdentifier;
import ca.magex.crm.api.system.id.OrganizationIdentifier;
import graphql.ErrorType;

public class BadRequestGraphQLErrorTests {

	@Test
	public void testError() {
		Message message = new Message(
				new OrganizationIdentifier("A"), 
				new MessageTypeIdentifier("error"), "path", "ABC", new Choice<>("custom message"));
		
		BadRequestGraphQLError error = new BadRequestGraphQLError(message);
		Assert.assertEquals("{\"identifier\":\"\\/organizations\\/A\",\"type\":\"\\/options\\/message-types\\/error\",\"path\":\"path\",\"value\":\"ABC\",\"reason\":{\"identifier\":null,\"other\":\"custom message\"}}", error.getMessage());
		Assert.assertEquals(0, error.getLocations().size());
		Assert.assertEquals(ErrorType.ValidationError, error.getErrorType());
		
		BadRequestGraphQLError error2 = new BadRequestGraphQLError(message);
		Assert.assertEquals(error, error2);
		Assert.assertEquals(error.hashCode(), error2.hashCode());
		Assert.assertEquals(error.toString(), error2.toString());
	}
}