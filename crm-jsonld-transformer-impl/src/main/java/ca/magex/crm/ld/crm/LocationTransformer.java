package ca.magex.crm.ld.crm;

import ca.magex.crm.api.common.MailingAddress;
import ca.magex.crm.api.crm.Location;
import ca.magex.crm.api.system.Identifier;
import ca.magex.crm.api.system.Status;
import ca.magex.crm.ld.AbstractLinkedDataTransformer;
import ca.magex.crm.ld.common.MailingAddressTransformer;
import ca.magex.crm.ld.data.DataObject;
import ca.magex.crm.ld.system.StatusTransformer;

public class LocationTransformer extends AbstractLinkedDataTransformer<Location> {

	private StatusTransformer statusTransformer;
	
	private MailingAddressTransformer mailingAddressTransformer;
	
	public LocationTransformer() {
		this.statusTransformer = new StatusTransformer();
		this.mailingAddressTransformer = new MailingAddressTransformer();
	}
	
	public String getType() {
		return "organization";
	}
	
	@Override
	public DataObject format(Location location) {
		return format(location.getLocationId())
			.with("organization", format(location.getOrganizationId()))
			.with("status", statusTransformer.format(location.getStatus()))
			.with("reference", location.getReference())
			.with("displayName", location.getDisplayName())
			.with("address", mailingAddressTransformer.format(location.getAddress()));
	}

	@Override
	public Location parse(DataObject data) {
		validateContext(data);
		validateType(data);
		Identifier locationId = getTopicId(data);
		Identifier organizationId = getTopicId(data.getObject("organization"));
		Status status = statusTransformer.parse(data.get("status"));
		String reference = data.getString("reference");
		String displayName = data.getString("displayName");
		MailingAddress address = mailingAddressTransformer.parse(data.getObject("address"));
		return new Location(locationId, organizationId, status, reference, displayName, address);
	}

		
}
