package ca.magex.crm.ld.system;

import ca.magex.crm.api.services.CrmServices;
import ca.magex.crm.api.system.Lang;
import ca.magex.crm.api.system.Role;
import ca.magex.crm.ld.AbstractLinkedDataTransformer;
import ca.magex.crm.ld.data.DataObject;

public class RoleTransformer extends AbstractLinkedDataTransformer<Role> {

	public CrmServices crm;
	
	public RoleTransformer(CrmServices crm) {
		this.crm = crm;
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
		return crm.findRoleByCode(code);
	}
			
}
