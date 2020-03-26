package ca.magex.crm.ld.system;

import ca.magex.crm.api.system.Status;
import ca.magex.crm.ld.AbstractLinkedDataTransformer;
import ca.magex.crm.ld.data.DataObject;

public class StatusTransformer extends AbstractLinkedDataTransformer<Status> {

	@Override
	public String getType() {
		return "status";
	}
	
	@Override
	public DataObject format(Status status) {
		return value(status);
	}
	
	@Override
	public Status parse(DataObject data) {
		validateContext(data);
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
