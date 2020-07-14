package ca.magex.crm.transform.json;

import java.util.Locale;

import org.springframework.stereotype.Component;

import ca.magex.crm.api.Crm;
import ca.magex.crm.api.services.CrmServices;
import ca.magex.crm.api.system.Identifier;
import ca.magex.crm.api.system.id.OptionIdentifier;
import ca.magex.json.model.JsonElement;
import ca.magex.json.model.JsonText;

@Component
public class IdentifierJsonTransformer extends AbstractJsonTransformer<Identifier> {

	public IdentifierJsonTransformer(CrmServices crm) {
		super(crm);
	}

	@Override
	public Class<Identifier> getSourceType() {
		return Identifier.class;
	}

	@Override
	public JsonElement formatRoot(Identifier identifier) {
		return formatLocalized(identifier, null);
	}
	
	@Override
	public JsonElement formatLocalized(Identifier identifier, Locale locale) {
		if (locale == null) {
			return new JsonText(Crm.REST_BASE + identifier.toString().toLowerCase());
		} else {
			if (identifier instanceof OptionIdentifier) {
				return new JsonText(crm.findOption((OptionIdentifier)identifier).getName(locale));
			} else {
				return new JsonText(identifier.getCode());
			}
		}
	}

//	@Override
//	public Identifier parseJsonText(JsonText json, Locale locale) {
//		return new Identifier(json.value());
//	}
//
//	@Override
//	public Identifier parseJsonObject(JsonObject json, Locale locale) {
//		return new Identifier(json.getString("@id"));
//	}

}
