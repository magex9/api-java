package ca.magex.crm.api.exceptions;

import ca.magex.crm.api.Crm;

public class PermissionDeniedException extends ApiException {

	private static final long serialVersionUID = Crm.SERIAL_UID_VERSION;

	public PermissionDeniedException(String uri) {
		super("Permission denied: " + uri);
	}

	@Override
	public Integer getErrorCode() {
		return 403;
	}
	
}
