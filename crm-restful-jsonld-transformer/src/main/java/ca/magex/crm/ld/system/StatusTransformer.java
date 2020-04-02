package ca.magex.crm.ld.system;

import ca.magex.crm.api.services.SecuredOrganizationService;
import ca.magex.crm.api.system.Lang;
import ca.magex.crm.api.system.Status;
import ca.magex.crm.ld.AbstractLinkedDataTransformer;
import ca.magex.crm.ld.data.DataObject;

public class StatusTransformer extends AbstractLinkedDataTransformer<Status> {

	public StatusTransformer(SecuredOrganizationService service) {

	}

	@Override
	public Class<?> getType() {
		return Status.class;
	}
	
	@Override
	public DataObject format(Status status) {
		return value(status)
			.with("@en", status.getName(Lang.ENGLISH))
			.with("@fr", status.getName(Lang.FRENCH));
	}
	
	@Override
	public Status parse(DataObject data, String parentContext) {
		validateContext(data, parentContext);
		validateType(data);
		return Status.valueOf(data.getString("@value").toUpperCase());
	}
	
	public Status parse(String data) {
		try {
			return Status.valueOf(data.toUpperCase());
		} catch (Exception e) {
			return super.parse(data);
		}
	}
	
}
