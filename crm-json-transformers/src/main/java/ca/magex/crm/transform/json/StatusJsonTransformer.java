package ca.magex.crm.transform.json;

import java.util.Locale;

import ca.magex.crm.api.services.CrmOptionService;
import ca.magex.crm.api.system.Status;
import ca.magex.crm.api.system.Type;
import ca.magex.json.model.JsonElement;
import ca.magex.json.model.JsonObject;
import ca.magex.json.model.JsonText;

public class StatusJsonTransformer extends AbstractJsonTransformer<Status> {

	public StatusJsonTransformer(CrmOptionService crm) {
		super(crm);
	}

	@Override
	public Class<Status> getSourceType() {
		return Status.class;
	}

	@Override
	public JsonElement formatRoot(Status status) {
		return formatLocalized(status, null);
	}
	
	@Override
	public JsonElement formatLocalized(Status status, Locale locale) {
		return formatOption(crm.findOptionByCode(Type.STATUS, status.getCode()), locale);
	}

	@Override
	public Status parseJsonText(JsonText json, Locale locale) {
		return Status.of(json.value(), locale);
	}

	@Override
	public Status parseJsonObject(JsonObject json, Locale locale) {
		return Status.of(json.getString("@value").toUpperCase());
	}

}
