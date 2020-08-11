package ca.magex.crm.api.exceptions;

import ca.magex.crm.api.Crm;

public class PermissionDeniedException extends ApiException {

	private static final long serialVersionUID = Crm.SERIAL_UID_VERSION;
	
	private String reason;

	public PermissionDeniedException(String reason) {
		super("Permission denied: " + reason);
		this.reason = reason;
	}
	
	public String getReason() {
		return reason;
	}

	@Override
	public Integer getErrorCode() {
		return 403;
	}
	
}
