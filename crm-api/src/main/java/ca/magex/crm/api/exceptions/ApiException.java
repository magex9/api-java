package ca.magex.crm.api.exceptions;

import ca.magex.crm.api.services.Crm;

public class ApiException extends RuntimeException {

	private static final long serialVersionUID = Crm.SERIAL_UID_VERSION;

	private Integer associatedErrorCode;
	
	public ApiException(String msg, Exception e, Integer associatedErrorCode) {
		super(msg, e);
		this.associatedErrorCode = associatedErrorCode;
	}
	
	public ApiException(String msg, Exception e) {
		this(msg, e, 500);
	}

	public ApiException(String msg) {
		this(msg, null);
	}
	
	public Integer getErrorCode() {
		return associatedErrorCode;
	}
	
}
