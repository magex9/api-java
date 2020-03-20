package ca.magex.crm.api.exceptions;

public class ItemNotFoundException extends ApiException {

	private static final long serialVersionUID = 1L;

	public ItemNotFoundException(String uri) {
		super("Item not found: " + uri);
	}

	@Override
	public int getErrorCode() {
		return 404;
	}
	
}
