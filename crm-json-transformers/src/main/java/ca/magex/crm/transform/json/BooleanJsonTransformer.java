package ca.magex.crm.transform.json;

import java.util.Locale;

import ca.magex.crm.api.services.CrmOptionService;
import ca.magex.crm.api.transform.Transformer;
import ca.magex.json.model.JsonBoolean;

public class BooleanJsonTransformer implements Transformer<Boolean, JsonBoolean> {

	public BooleanJsonTransformer(CrmOptionService crm) {
		
	}

	@Override
	public Class<Boolean> getSourceType() {
		return Boolean.class;
	}

	@Override
	public Class<JsonBoolean> getTargetType() {
		return JsonBoolean.class;
	}

	@Override
	public JsonBoolean format(Boolean value, Locale locale) {
		return new JsonBoolean(value);
	}

	@Override
	public Boolean parse(JsonBoolean target, Locale locale) {
		return target.value();
	}

}
