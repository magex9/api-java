package ca.magex.crm.api.exceptions;

import ca.magex.crm.api.Crm;

public class DuplicateItemFoundException extends ApiException {

	private static final long serialVersionUID = Crm.SERIAL_UID_VERSION;
	
	private String reason;

	public DuplicateItemFoundException(String reason) {
		super("Duplicate item found: " + reason);
		this.reason = reason;
	}
	
	public String getReason() {
		return reason;
	}

	@Override
	public Integer getErrorCode() {
		return 404;
	}
	
}
