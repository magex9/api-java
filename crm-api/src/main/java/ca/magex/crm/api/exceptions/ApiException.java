package ca.magex.crm.api.exceptions;

import ca.magex.crm.api.services.Crm;

public class ApiException extends RuntimeException {

	private static final long serialVersionUID = Crm.SERIAL_UID_VERSION;

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
