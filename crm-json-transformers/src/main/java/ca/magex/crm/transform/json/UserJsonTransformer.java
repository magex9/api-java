package ca.magex.crm.transform.json;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import ca.magex.crm.api.crm.PersonSummary;
import ca.magex.crm.api.crm.User;
import ca.magex.crm.api.services.CrmServices;
import ca.magex.crm.api.system.Identifier;
import ca.magex.crm.api.system.Status;
import ca.magex.json.model.JsonObject;
import ca.magex.json.model.JsonPair;
import ca.magex.json.model.JsonText;

@Component
public class UserJsonTransformer extends AbstractJsonTransformer<User> {

	private IdentifierJsonTransformer identifierJsonTransformer;
	
	private StatusJsonTransformer statusJsonTransformer;
	
	private PersonSummaryJsonTransformer personSummaryJsonTransformer;
	
	public UserJsonTransformer(CrmServices crm) {
		super(crm);
		this.identifierJsonTransformer = new IdentifierJsonTransformer(crm);
		this.statusJsonTransformer = new StatusJsonTransformer(crm);
		this.personSummaryJsonTransformer = new PersonSummaryJsonTransformer(crm);
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
		if (user.getUserId() != null) {
			pairs.add(new JsonPair("userId", identifierJsonTransformer
				.format(user.getUserId(), locale)));
		}
		formatText(pairs, "username", user);
		if (user.getPerson() != null) {
			pairs.add(new JsonPair("person", personSummaryJsonTransformer
				.format(user.getPerson(), locale)));
		}
		if (user.getStatus() != null) {
			pairs.add(new JsonPair("status", statusJsonTransformer
				.format(user.getStatus(), locale)));
		}
		formatTexts(pairs, "roles", user, String.class);
		return new JsonObject(pairs);
	}

	@Override
	public User parseJsonObject(JsonObject json, Locale locale) {
		Identifier userId = parseObject("userId", json, identifierJsonTransformer, locale);
		String username = parseText("username", json);
		Status status = parseObject("status", json, statusJsonTransformer, locale);
		PersonSummary person = parseObject("person", json, personSummaryJsonTransformer, locale);
		List<String> roles = json.getArray("roles").stream().map(e -> ((JsonText)e).value()).collect(Collectors.toList());
		return new User(userId, username, person, status, roles);
	}

}
