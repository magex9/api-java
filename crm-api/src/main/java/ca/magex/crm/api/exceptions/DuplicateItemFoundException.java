package ca.magex.crm.api.exceptions;

import ca.magex.crm.api.services.Crm;

public class DuplicateItemFoundException extends ApiException {

	private static final long serialVersionUID = Crm.SERIAL_UID_VERSION;

	public DuplicateItemFoundException(String uri) {
		super("Duplicate item found found: " + uri);
	}

	@Override
	public int getErrorCode() {
		return 404;
	}
	
}
