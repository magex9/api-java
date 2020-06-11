package ca.magex.crm.transform.json;

import java.util.Locale;

import org.springframework.stereotype.Component;

import ca.magex.crm.api.transform.Transformer;
import ca.magex.json.model.JsonText;

@Component
public class StringJsonTransformer implements Transformer<String, JsonText> {

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
