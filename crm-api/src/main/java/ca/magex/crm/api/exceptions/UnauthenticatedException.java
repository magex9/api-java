package ca.magex.crm.api.exceptions;

import ca.magex.crm.api.services.Crm;

public class UnauthenticatedException extends ApiException {

	private static final long serialVersionUID = Crm.SERIAL_UID_VERSION;

	public UnauthenticatedException() {
		super("Unauthenticated");
	}

	@Override
	public Integer getErrorCode() {
		return 401;
	}
	
}
