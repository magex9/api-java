package ca.magex.crm.transform.json;

import java.util.Locale;

import ca.magex.crm.api.services.CrmOptionService;
import ca.magex.crm.api.transform.Transformer;
import ca.magex.json.model.JsonText;

public class StringJsonTransformer implements Transformer<String, JsonText> {

	public StringJsonTransformer(CrmOptionService crm) {
		
	}

	@Override
	public Class<String> getSourceType() {
		return String.class;
	}

	@Override
	public Class<JsonText> getTargetType() {
		return JsonText.class;
	}

	@Override
	public JsonText format(String value, Locale locale) {
		return new JsonText(value);
	}

	@Override
	public String parse(JsonText target, Locale locale) {
		return target.value();
	}

}
