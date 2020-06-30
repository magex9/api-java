package ca.magex.crm.transform.json;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import ca.magex.crm.api.crm.User;
import ca.magex.crm.api.services.CrmServices;
import ca.magex.crm.api.system.Identifier;
import ca.magex.crm.api.system.Status;
import ca.magex.crm.api.system.id.AuthenticationRoleIdentifier;
import ca.magex.crm.api.system.id.PersonIdentifier;
import ca.magex.crm.api.system.id.UserIdentifier;
import ca.magex.json.model.JsonObject;
import ca.magex.json.model.JsonPair;
import ca.magex.json.model.JsonText;

@Component
public class UserJsonTransformer extends AbstractJsonTransformer<User> {

	public UserJsonTransformer(CrmServices crm) {
		super(crm);
	}

	@Override
	public Class<User> getSourceType() {
		return User.class;
	}
	
	@Override
	public JsonObject formatRoot(User user) {
		return formatLocalized(user, null);
	}
	
	@Override
	public JsonObject formatLocalized(User user, Locale locale) {
		List<JsonPair> pairs = new ArrayList<JsonPair>();
		formatType(pairs);
		formatIdentifier(pairs, "userId", user, UserIdentifier.class, locale);
		formatIdentifier(pairs, "personId", user, PersonIdentifier.class, locale);
		formatText(pairs, "username", user);
		formatStatus(pairs, "status", user, locale);
		formatObjects(pairs, "roleIds", user, Identifier.class);
		return new JsonObject(pairs);
	}

	@Override
	public User parseJsonObject(JsonObject json, Locale locale) {
		UserIdentifier userId = parseIdentifier("userId", json, UserIdentifier.class, locale);
		PersonIdentifier personId = parseIdentifier("person", json, PersonIdentifier.class, locale);
		String username = parseText("username", json);
		Status status = parseObject("status", json, new StatusJsonTransformer(crm), locale);
		List<AuthenticationRoleIdentifier> roleIds = json.getArray("roleIds").stream().map(e -> new AuthenticationRoleIdentifier(((JsonText)e).value())).collect(Collectors.toList());
		return new User(userId, personId, username, status, roleIds);
	}

}
