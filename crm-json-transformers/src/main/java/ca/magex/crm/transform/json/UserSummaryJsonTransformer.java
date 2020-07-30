package ca.magex.crm.transform.json;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.springframework.stereotype.Component;

import ca.magex.crm.api.crm.UserSummary;
import ca.magex.crm.api.services.CrmOptionService;
import ca.magex.crm.api.system.Status;
import ca.magex.crm.api.system.id.OrganizationIdentifier;
import ca.magex.crm.api.system.id.UserIdentifier;
import ca.magex.json.model.JsonObject;
import ca.magex.json.model.JsonPair;

@Component
public class UserSummaryJsonTransformer extends AbstractJsonTransformer<UserSummary> {

	public UserSummaryJsonTransformer(CrmOptionService crm) {
		super(crm);
	}

	@Override
	public Class<UserSummary> getSourceType() {
		return UserSummary.class;
	}
	
	@Override
	public JsonObject formatRoot(UserSummary user) {
		return formatLocalized(user, null);
	}
	
	@Override
	public JsonObject formatLocalized(UserSummary user, Locale locale) {
		List<JsonPair> pairs = new ArrayList<JsonPair>();
		formatType(pairs, locale);
		formatIdentifier(pairs, "userId", user, UserIdentifier.class, locale);
		formatIdentifier(pairs, "organizationId", user, OrganizationIdentifier.class, locale);
		formatText(pairs, "username", user);
		formatStatus(pairs, "status", user, locale);
		return new JsonObject(pairs);
	}

	@Override
	public UserSummary parseJsonObject(JsonObject json, Locale locale) {
		UserIdentifier userId = parseIdentifier("userId", json, UserIdentifier.class, locale);
		OrganizationIdentifier organizationId = parseIdentifier("organizationId", json, OrganizationIdentifier.class, locale);
		String username = parseText("username", json);
		Status status = parseObject("status", json, new StatusJsonTransformer(crm), locale);
		return new UserSummary(userId, organizationId, username, status);
	}

}
