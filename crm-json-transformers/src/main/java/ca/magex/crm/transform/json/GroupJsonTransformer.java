package ca.magex.crm.transform.json;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import ca.magex.crm.api.roles.Group;
import ca.magex.crm.api.services.CrmServices;
import ca.magex.crm.api.system.Identifier;
import ca.magex.crm.api.system.Localized;
import ca.magex.crm.api.system.Status;
import ca.magex.json.model.JsonObject;
import ca.magex.json.model.JsonPair;
import ca.magex.json.model.JsonText;

public class GroupJsonTransformer extends AbstractJsonTransformer<Group> {

	public GroupJsonTransformer(CrmServices crm) {
		super(crm);
	}

	@Override
	public Class<Group> getType() {
		return Group.class;
	}

	@Override
	public JsonObject formatRoot(Group group) {
		return formatLocalized(group, null);
	}
	
	@Override
	public JsonObject formatLocalized(Group group, Locale locale) {
		List<JsonPair> pairs = new ArrayList<JsonPair>();
		formatType(pairs);
		if (group.getGroupId() != null) {
			pairs.add(new JsonPair("groupId", new IdentifierJsonTransformer(crm)
				.format(group.getGroupId(), locale)));
		}
		if (group.getStatus() != null) {
			pairs.add(new JsonPair("status", new StatusJsonTransformer(crm)
				.format(group.getStatus(), locale)));
		}
		if (group.getCode() != null) {
			pairs.add(new JsonPair("code", new JsonText(group.getCode())));
		}
		if (group.getName() != null) {
			pairs.add(new JsonPair("name", new LocalizedJsonTransformer(crm)
				.format(group.getName(), locale)));
		}
		return new JsonObject(pairs);
	}

	@Override
	public Group parseJsonObject(JsonObject json, Locale locale) {
		Identifier groupId = parseObject("groupId", json, Identifier.class, IdentifierJsonTransformer.class, locale);
		Status status = parseObject("status", json, Status.class, StatusJsonTransformer.class, locale);
		Localized name = parseObject("name", json, Localized.class, LocalizedJsonTransformer.class, locale);
		return new Group(groupId, status, name);
	}

}
