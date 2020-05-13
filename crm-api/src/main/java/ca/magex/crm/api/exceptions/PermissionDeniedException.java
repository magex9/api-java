package ca.magex.crm.api.exceptions;

import ca.magex.crm.api.services.Crm;

public class PermissionDeniedException extends ApiException {

	private static final long serialVersionUID = Crm.SERIAL_UID_VERSION;

	public PermissionDeniedException(String uri) {
		super("Permission denied: " + uri);
	}

	@Override
	public int getErrorCode() {
		return 403;
	}
	
}
