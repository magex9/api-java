package ca.magex.crm.ld.system;

import ca.magex.crm.api.system.Role;
import ca.magex.crm.ld.AbstractLinkedDataTransformer;
import ca.magex.crm.ld.data.DataObject;

public class RoleTransformer extends AbstractLinkedDataTransformer<Role> {

	@Override
	public Class<?> getType() {
		return Role.class;
	}
	
	@Override
	public DataObject format(Role role) {
		return base()
			.with("@value", role.getCode())
			.with("name", role.getName());
	}

	@Override
	public Role parse(DataObject data) {
		validateContext(data);
		validateType(data);
		Integer code = data.getInt("@value");
		String name = data.getString("name");
		return new Role(code, name);
	}
			
}
