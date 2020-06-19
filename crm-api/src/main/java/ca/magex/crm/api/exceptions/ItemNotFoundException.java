package ca.magex.crm.api.exceptions;

import ca.magex.crm.api.Crm;

public class ItemNotFoundException extends ApiException {

	private static final long serialVersionUID = Crm.SERIAL_UID_VERSION;

	public ItemNotFoundException(String uri) {
		super("Item not found: " + uri);
	}

	@Override
	public Integer getErrorCode() {
		return 404;
	}
	
}
