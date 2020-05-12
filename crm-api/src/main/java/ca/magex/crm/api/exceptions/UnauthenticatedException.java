package ca.magex.crm.api.exceptions;

public class UnauthenticatedException extends ApiException {

	private static final long serialVersionUID = 1L;

	public UnauthenticatedException() {
		super("Unauthenticated");
	}

	@Override
	public int getErrorCode() {
		return 401;
	}
	
}
