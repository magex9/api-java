package ca.magex.crm.ld.crm;

import ca.magex.crm.api.crm.Organization;
import ca.magex.crm.api.system.Identifier;
import ca.magex.crm.api.system.Status;
import ca.magex.crm.ld.AbstractLinkedDataTransformer;
import ca.magex.crm.ld.data.DataObject;
import ca.magex.crm.ld.system.StatusTransformer;

public class OrganizationTransformer extends AbstractLinkedDataTransformer<Organization> {

	private StatusTransformer statusTransformer;
	
	private LocationTransformer locationTransform;
	
	public OrganizationTransformer() {
		this.statusTransformer = new StatusTransformer();
		this.locationTransform = new LocationTransformer();
	}
	
	public String getType() {
		return "Organization";
	}
	
	@Override
	public DataObject format(Organization organization) {
		return format(organization.getOrganizationId())
			.with("displayName", organization.getDisplayName())
			.with("status", statusTransformer.format(organization.getStatus()))
			.with("mainLocation", locationTransform.format(organization.getMainLocationId()));
	}

	@Override
	public Organization parse(DataObject data) {
		validateContext(data);
		validateType(data);
		Identifier organizationId = getTopicId(data);
		Status status = statusTransformer.parse(data.get("status"));
		String displayName = data.getString("displayName");
		Identifier mainLocationId = getTopicId(data.getObject("mainLocation"));
		return new Organization(organizationId, status, displayName, mainLocationId);
	}

}
