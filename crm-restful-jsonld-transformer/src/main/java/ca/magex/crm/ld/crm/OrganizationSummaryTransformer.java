package ca.magex.crm.ld.crm;

import ca.magex.crm.api.crm.OrganizationSummary;
import ca.magex.crm.api.services.SecuredOrganizationService;
import ca.magex.crm.api.system.Identifier;
import ca.magex.crm.api.system.Status;
import ca.magex.crm.ld.AbstractLinkedDataTransformer;
import ca.magex.crm.ld.data.DataObject;
import ca.magex.crm.ld.system.StatusTransformer;

public class OrganizationSummaryTransformer extends AbstractLinkedDataTransformer<OrganizationSummary> {

	private StatusTransformer statusTransformer;
	
	public OrganizationSummaryTransformer(SecuredOrganizationService service) {
		this.statusTransformer = new StatusTransformer(service);
	}
	
	public Class<?> getType() {
		return OrganizationSummary.class;
	}
	
	@Override
	public DataObject format(OrganizationSummary organization) {
		return format(organization.getOrganizationId())
			.with("displayName", organization.getDisplayName())
			.with("status", statusTransformer.format(organization.getStatus()));
	}

	@Override
	public OrganizationSummary parse(DataObject data, String parentContext) {
		validateContext(data, parentContext);
		validateType(data);
		Identifier organizationId = getTopicId(data);
		Status status = statusTransformer.parse(data.get("status"));
		String displayName = data.getString("displayName");
		return new OrganizationSummary(organizationId, status, displayName);
	}

}
