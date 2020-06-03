package ca.magex.crm.transform.json;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.springframework.stereotype.Component;

import ca.magex.crm.api.roles.Group;
import ca.magex.crm.api.services.CrmServices;
import ca.magex.crm.api.system.Identifier;
import ca.magex.crm.api.system.Localized;
import ca.magex.crm.api.system.Status;
import ca.magex.json.model.JsonObject;
import ca.magex.json.model.JsonPair;
import ca.magex.json.model.JsonText;

@Component
public class GroupJsonTransformer extends AbstractJsonTransformer<Group> {
	
	private IdentifierJsonTransformer identifierJsonTransformer;
	
	private StatusJsonTransformer statusJsonTransformer;
	
	private LocalizedJsonTransformer localizedJsonTransformer;

	public GroupJsonTransformer(CrmServices crm) {
		super(crm);
		this.identifierJsonTransformer = new IdentifierJsonTransformer(crm);
		this.statusJsonTransformer = new StatusJsonTransformer(crm);
		this.localizedJsonTransformer = new LocalizedJsonTransformer(crm);
	}

	@Override
	public Class<Group> getSourceType() {
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
			pairs.add(new JsonPair("groupId", identifierJsonTransformer
				.format(group.getGroupId(), locale)));
		}
		if (group.getStatus() != null) {
			pairs.add(new JsonPair("status", statusJsonTransformer
				.format(group.getStatus(), locale)));
		}
		if (group.getCode() != null) {
			pairs.add(new JsonPair("code", new JsonText(group.getCode())));
		}
		if (group.getName() != null) {
			pairs.add(new JsonPair("name", localizedJsonTransformer
				.format(group.getName(), locale)));
		}
		return new JsonObject(pairs);
	}

	@Override
	public Group parseJsonObject(JsonObject json, Locale locale) {
		Identifier groupId = parseObject("groupId", json, identifierJsonTransformer, locale);
		Status status = parseObject("status", json, statusJsonTransformer, locale);
		Localized name = parseObject("name", json, localizedJsonTransformer, locale);
		return new Group(groupId, status, name);
	}

}
