package ca.magex.crm.transform.json;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.springframework.stereotype.Component;

import ca.magex.crm.api.filters.OptionsFilter;
import ca.magex.crm.api.services.CrmServices;
import ca.magex.crm.api.system.Lang;
import ca.magex.crm.api.system.Option;
import ca.magex.crm.api.system.Status;
import ca.magex.crm.api.system.Type;
import ca.magex.json.model.JsonElement;
import ca.magex.json.model.JsonObject;
import ca.magex.json.model.JsonPair;
import ca.magex.json.model.JsonText;

@Component
public class StatusJsonTransformer extends AbstractJsonTransformer<Status> {

	public StatusJsonTransformer(CrmServices crm) {
		super(crm);
	}

	@Override
	public Class<Status> getSourceType() {
		return Status.class;
	}

	@Override
	public JsonElement formatRoot(Status status) {
		List<JsonPair> pairs = new ArrayList<JsonPair>();
		pairs.add(new JsonPair("@context", "http://magex.ca/crm/lookups/Statuses"));
		pairs.add(new JsonPair("@value", status.getCode()));
		pairs.add(new JsonPair("@en", status.getName(Lang.ENGLISH)));
		pairs.add(new JsonPair("@fr", status.getName(Lang.FRENCH)));
		return new JsonObject(pairs);
	}
	
	@Override
	public JsonElement formatLocalized(Status status, Locale locale) {
		return new JsonText(status.getName(locale));
	}

	@Override
	public Status parseJsonText(JsonText json, Locale locale) {
		Option option = crm.findOptions(new OptionsFilter().withType(Type.STATUS).withName(locale, json.value())).getSingleItem();
		return Status.valueOf(option.getCode().toUpperCase());
	}

	@Override
	public Status parseJsonObject(JsonObject json, Locale locale) {
		return Status.valueOf(json.getString("@value").toUpperCase());
	}

}
