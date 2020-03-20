package ca.magex.crm.api.exceptions;

public class PermissionDeniedException extends ApiException {

	private static final long serialVersionUID = 1L;

	public PermissionDeniedException(String uri) {
		super("Permission denied: " + uri);
	}

	@Override
	public int getErrorCode() {
		return 403;
	}
	
}
