package ca.magex.json;

public class ParserException extends RuntimeException {

	private static final long serialVersionUID = -3624059725745853552L;

	public ParserException(String message, Throwable cause) {
		super(message, cause);
	}
	
	public ParserException(String message) {
		super(message);
	}

}
