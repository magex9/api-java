package ca.magex.crm.ld.common;

import java.util.List;

import ca.magex.crm.api.common.User;
import ca.magex.crm.api.system.Role;
import ca.magex.crm.ld.AbstractLinkedDataTransformer;
import ca.magex.crm.ld.data.DataObject;
import ca.magex.crm.ld.system.RoleTransformer;

public class UserTransformer extends AbstractLinkedDataTransformer<User> {

	private RoleTransformer roleTransformer;
	
	public UserTransformer() {
		this.roleTransformer = new RoleTransformer();
	}
	
	@Override
	public Class<?> getType() {
		return User.class;
	}
	
	@Override
	public DataObject format(User user) {
		return base()
			.with("userName", user.getUserName())
			.with("roles", roleTransformer.format(user.getRoles()));
	}

	@Override
	public User parse(DataObject data, String parentContext) {
		validateContext(data, parentContext);
		validateType(data);
		String userName = data.getString("userName");
		List<Role> roles = roleTransformer.parse(data.getArray("roles"));
		return new User(userName, roles);
	}
			
}
