package ca.magex.crm.transform.json;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.springframework.stereotype.Component;

import ca.magex.crm.api.roles.Role;
import ca.magex.crm.api.services.CrmServices;
import ca.magex.crm.api.system.Identifier;
import ca.magex.crm.api.system.Localized;
import ca.magex.crm.api.system.Status;
import ca.magex.json.model.JsonObject;
import ca.magex.json.model.JsonPair;
import ca.magex.json.model.JsonText;

@Component
public class RoleJsonTransformer extends AbstractJsonTransformer<Role> {
	
	private IdentifierJsonTransformer identifierJsonTransformer;
	
	private StatusJsonTransformer statusJsonTransformer;
	
	private LocalizedJsonTransformer localizedJsonTransformer;

	public RoleJsonTransformer(CrmServices crm, IdentifierJsonTransformer identifierJsonTransformer,
			StatusJsonTransformer statusJsonTransformer, LocalizedJsonTransformer localizedJsonTransformer) {
		super(crm);
		this.identifierJsonTransformer = identifierJsonTransformer;
		this.statusJsonTransformer = statusJsonTransformer;
		this.localizedJsonTransformer = localizedJsonTransformer;
	}

	@Override
	public Class<Role> getSourceType() {
		return Role.class;
	}

	@Override
	public JsonObject formatRoot(Role role) {
		return formatLocalized(role, null);
	}
	
	@Override
	public JsonObject formatLocalized(Role role, Locale locale) {
		List<JsonPair> pairs = new ArrayList<JsonPair>();
		formatType(pairs);
		if (role.getRoleId() != null) {
			pairs.add(new JsonPair("roleId", identifierJsonTransformer
				.format(role.getRoleId(), locale)));
		}
		if (role.getGroupId() != null) {
			pairs.add(new JsonPair("groupId", identifierJsonTransformer
				.format(role.getGroupId(), locale)));
		}
		if (role.getStatus() != null) {
			pairs.add(new JsonPair("status", statusJsonTransformer
				.format(role.getStatus(), locale)));
		}
		if (role.getCode() != null) {
			pairs.add(new JsonPair("code", new JsonText(role.getCode())));
		}
		if (role.getName() != null) {
			pairs.add(new JsonPair("name", localizedJsonTransformer
				.format(role.getName(), locale)));
		}
		return new JsonObject(pairs);
	}

	@Override
	public Role parseJsonObject(JsonObject json, Locale locale) {
		Identifier roleId = parseObject("roleId", json, identifierJsonTransformer, locale);
		Identifier groupId = parseObject("groupId", json, identifierJsonTransformer, locale);
		Status status = parseObject("status", json, statusJsonTransformer, locale);
		Localized name = parseObject("name", json, localizedJsonTransformer, locale);
		return new Role(roleId, groupId, status, name);
	}

}
