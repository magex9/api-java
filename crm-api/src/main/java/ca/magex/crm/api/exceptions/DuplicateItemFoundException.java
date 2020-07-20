package ca.magex.crm.api.exceptions;

import ca.magex.crm.api.Crm;

public class DuplicateItemFoundException extends ApiException {

	private static final long serialVersionUID = Crm.SERIAL_UID_VERSION;

	public DuplicateItemFoundException(String uri) {
		super("Duplicate item found: " + uri);
	}

	@Override
	public Integer getErrorCode() {
		return 404;
	}
	
}
