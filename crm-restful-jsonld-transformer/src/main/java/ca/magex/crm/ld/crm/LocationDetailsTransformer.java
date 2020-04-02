package ca.magex.crm.ld.crm;

import ca.magex.crm.api.common.MailingAddress;
import ca.magex.crm.api.crm.LocationDetails;
import ca.magex.crm.api.services.SecuredOrganizationService;
import ca.magex.crm.api.system.Identifier;
import ca.magex.crm.api.system.Status;
import ca.magex.crm.ld.AbstractLinkedDataTransformer;
import ca.magex.crm.ld.common.MailingAddressTransformer;
import ca.magex.crm.ld.data.DataObject;
import ca.magex.crm.ld.system.StatusTransformer;

public class LocationDetailsTransformer extends AbstractLinkedDataTransformer<LocationDetails> {

	private StatusTransformer statusTransformer;
	
	private MailingAddressTransformer mailingAddressTransformer;
	
	public LocationDetailsTransformer(SecuredOrganizationService service) {
		this.statusTransformer = new StatusTransformer(service);
		this.mailingAddressTransformer = new MailingAddressTransformer(service);
	}
	
	public Class<?> getType() {
		return LocationDetails.class;
	}
	
	@Override
	public DataObject format(LocationDetails location) {
		return format(location.getLocationId())
			.with("organization", format(location.getOrganizationId()))
			.with("status", statusTransformer.format(location.getStatus()))
			.with("reference", location.getReference())
			.with("displayName", location.getDisplayName())
			.with("address", mailingAddressTransformer.format(location.getAddress()));
	}

	@Override
	public LocationDetails parse(DataObject data, String parentContext) {
		validateContext(data, parentContext);
		validateType(data);
		Identifier locationId = getTopicId(data);
		Identifier organizationId = getTopicId(data.getObject("organization"));
		Status status = statusTransformer.parse(data.get("status"));
		String reference = data.getString("reference");
		String displayName = data.getString("displayName");
		MailingAddress address = mailingAddressTransformer.parse(data.getObject("address"));
		return new LocationDetails(locationId, organizationId, status, reference, displayName, address);
	}

		
}
