package ca.magex.crm.api.exceptions;

import ca.magex.crm.api.Crm;

public class ItemNotFoundException extends ApiException {

	private static final long serialVersionUID = Crm.SERIAL_UID_VERSION;
	
	private String reason;

	public ItemNotFoundException(String reason) {
		super("Item not found: " + reason);
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
