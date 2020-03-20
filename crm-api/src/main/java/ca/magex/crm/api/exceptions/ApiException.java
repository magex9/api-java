package ca.magex.crm.api.exceptions;

public class ApiException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public ApiException(String msg, Exception e) {
		super(msg, e);
	}

	public ApiException(String msg) {
		super(msg);
	}
	
	public int getErrorCode() {
		return 500;
	}
	
}
