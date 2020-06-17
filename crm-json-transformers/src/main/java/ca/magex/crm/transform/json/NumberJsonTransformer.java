package ca.magex.crm.transform.json;

import java.util.Locale;

import org.springframework.stereotype.Component;

import ca.magex.crm.api.transform.Transformer;
import ca.magex.json.model.JsonNumber;

@Component
public class NumberJsonTransformer implements Transformer<Number, JsonNumber> {

	@Override
	public Class<Number> getSourceType() {
		return Number.class;
	}

	@Override
	public Class<JsonNumber> getTargetType() {
		return JsonNumber.class;
	}

	@Override
	public JsonNumber format(Number value, Locale locale) {
		return new JsonNumber(value);
	}

	@Override
	public Number parse(JsonNumber target, Locale locale) {
		return target.value();
	}

}
