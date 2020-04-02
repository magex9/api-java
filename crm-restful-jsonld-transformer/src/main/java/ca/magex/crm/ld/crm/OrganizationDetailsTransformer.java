package ca.magex.crm.ld.crm;

import ca.magex.crm.api.crm.OrganizationDetails;
import ca.magex.crm.api.services.SecuredCrmServices;
import ca.magex.crm.api.system.Identifier;
import ca.magex.crm.api.system.Status;
import ca.magex.crm.ld.AbstractLinkedDataTransformer;
import ca.magex.crm.ld.data.DataObject;
import ca.magex.crm.ld.system.StatusTransformer;

public class OrganizationDetailsTransformer extends AbstractLinkedDataTransformer<OrganizationDetails> {

	private StatusTransformer statusTransformer;
	
	private LocationDetailsTransformer locationTransform;
	
	public OrganizationDetailsTransformer(SecuredCrmServices crm) {
		this.statusTransformer = new StatusTransformer(crm);
		this.locationTransform = new LocationDetailsTransformer(crm);
	}
	
	public Class<?> getType() {
		return OrganizationDetails.class;
	}
	
	@Override
	public DataObject format(OrganizationDetails organization) {
		return format(organization.getOrganizationId())
			.with("displayName", organization.getDisplayName())
			.with("status", statusTransformer.format(organization.getStatus()))
			.with("mainLocation", locationTransform.format(organization.getMainLocationId()));
	}

	@Override
	public OrganizationDetails parse(DataObject data, String parentContext) {
		validateContext(data, parentContext);
		validateType(data);
		Identifier organizationId = getTopicId(data);
		Status status = statusTransformer.parse(data.get("status"));
		String displayName = data.getString("displayName");
		Identifier mainLocationId = getTopicId(data.getObject("mainLocation"));
		return new OrganizationDetails(organizationId, status, displayName, mainLocationId);
	}

}
