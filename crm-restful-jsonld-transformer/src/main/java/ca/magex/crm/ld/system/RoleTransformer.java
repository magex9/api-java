package ca.magex.crm.ld.system;

import ca.magex.crm.api.services.SecuredOrganizationService;
import ca.magex.crm.api.system.Lang;
import ca.magex.crm.api.system.Role;
import ca.magex.crm.ld.AbstractLinkedDataTransformer;
import ca.magex.crm.ld.data.DataObject;

public class RoleTransformer extends AbstractLinkedDataTransformer<Role> {

	public SecuredOrganizationService service;
	
	public RoleTransformer(SecuredOrganizationService service) {
		this.service = service;
	}

	@Override
	public Class<?> getType() {
		return Role.class;
	}
	
	@Override
	public DataObject format(Role role) {
		return base()
			.with("@value", role.getCode())
			.with("@en", role.getName(Lang.ENGLISH))
			.with("@fr", role.getName(Lang.FRENCH));
	}

	@Override
	public Role parse(DataObject data, String parentContext) {
		validateContext(data, parentContext);
		validateType(data);
		String code = data.getString("@value");
		return service.findRoleByCode(code);
	}
			
}
