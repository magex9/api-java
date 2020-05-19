package ca.magex.crm.graphql.exceptions;

public class GraphQLClientException extends RuntimeException {

	private static final long serialVersionUID = -3993514705186158522L;

	public GraphQLClientException(String message, Throwable cause) {
		super(message, cause);
	}
	
	public GraphQLClientException(String message) {
		super(message);
	}
}
