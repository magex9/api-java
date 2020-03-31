package ca.magex.crm.ld.crm;

import ca.magex.crm.api.crm.LocationSummary;
import ca.magex.crm.api.system.Identifier;
import ca.magex.crm.api.system.Status;
import ca.magex.crm.ld.AbstractLinkedDataTransformer;
import ca.magex.crm.ld.data.DataObject;
import ca.magex.crm.ld.system.StatusTransformer;

public class LocationSummaryTransformer extends AbstractLinkedDataTransformer<LocationSummary> {

	private StatusTransformer statusTransformer;
	
	public LocationSummaryTransformer() {
		this.statusTransformer = new StatusTransformer();
	}
	
	public Class<?> getType() {
		return LocationSummary.class;
	}
	
	@Override
	public DataObject format(LocationSummary location) {
		return format(location.getLocationId())
			.with("organization", format(location.getOrganizationId()))
			.with("status", statusTransformer.format(location.getStatus()))
			.with("reference", location.getReference())
			.with("displayName", location.getDisplayName());
	}

	@Override
	public LocationSummary parse(DataObject data, String parentContext) {
		validateContext(data, parentContext);
		validateType(data);
		Identifier locationId = getTopicId(data);
		Identifier organizationId = getTopicId(data.getObject("organization"));
		Status status = statusTransformer.parse(data.get("status"));
		String reference = data.getString("reference");
		String displayName = data.getString("displayName");
		return new LocationSummary(locationId, organizationId, status, reference, displayName);
	}

		
}
