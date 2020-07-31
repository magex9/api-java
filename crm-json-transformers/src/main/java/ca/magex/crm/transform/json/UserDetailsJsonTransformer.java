package ca.magex.crm.transform.json;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import ca.magex.crm.api.crm.UserDetails;
import ca.magex.crm.api.services.CrmOptionService;
import ca.magex.crm.api.system.Status;
import ca.magex.crm.api.system.Type;
import ca.magex.crm.api.system.id.AuthenticationRoleIdentifier;
import ca.magex.crm.api.system.id.OrganizationIdentifier;
import ca.magex.crm.api.system.id.PersonIdentifier;
import ca.magex.crm.api.system.id.UserIdentifier;
import ca.magex.json.model.JsonObject;
import ca.magex.json.model.JsonPair;

public class UserDetailsJsonTransformer extends AbstractJsonTransformer<UserDetails> {

	public UserDetailsJsonTransformer(CrmOptionService crm) {
		super(crm);
	}

	@Override
	public Class<UserDetails> getSourceType() {
		return UserDetails.class;
	}
	
	@Override
	public JsonObject formatRoot(UserDetails user) {
		return formatLocalized(user, null);
	}
	
	@Override
	public JsonObject formatLocalized(UserDetails user, Locale locale) {
		List<JsonPair> pairs = new ArrayList<JsonPair>();
		formatType(pairs, locale);
		formatIdentifier(pairs, "userId", user, UserIdentifier.class, locale);
		formatIdentifier(pairs, "organizationId", user, OrganizationIdentifier.class, locale);
		formatIdentifier(pairs, "personId", user, PersonIdentifier.class, locale);
		formatText(pairs, "username", user);
		formatStatus(pairs, "status", user, locale);
		formatOptions(pairs, "authenticationRoleIds", user, Type.AUTHENTICATION_ROLE, locale);
		return new JsonObject(pairs);
	}

	@Override
	public UserDetails parseJsonObject(JsonObject json, Locale locale) {
		UserIdentifier userId = parseIdentifier("userId", json, UserIdentifier.class, locale);
		OrganizationIdentifier organizationId = parseIdentifier("organizationId", json, OrganizationIdentifier.class, locale);
		PersonIdentifier personId = parseIdentifier("personId", json, PersonIdentifier.class, locale);
		String username = parseText("username", json);
		Status status = parseObject("status", json, new StatusJsonTransformer(crm), locale);
		List<AuthenticationRoleIdentifier> roleIds = parseOptions("authenticationRoleIds", json, AuthenticationRoleIdentifier.class, locale);
		return new UserDetails(userId, organizationId, personId, username, status, roleIds);
	}

}
