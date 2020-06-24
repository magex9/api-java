package ca.magex.crm.transform.json;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.springframework.stereotype.Component;

import ca.magex.crm.api.services.CrmServices;
import ca.magex.crm.api.system.Identifier;
import ca.magex.crm.api.system.Localized;
import ca.magex.crm.api.system.Lookup;
import ca.magex.crm.api.system.Option;
import ca.magex.crm.api.system.Status;
import ca.magex.json.model.JsonElement;
import ca.magex.json.model.JsonObject;
import ca.magex.json.model.JsonPair;
import ca.magex.json.model.JsonText;

@Component
public class LookupJsonTransformer extends AbstractJsonTransformer<Lookup> {

	private IdentifierJsonTransformer identifierJsonTransformer;
	
	private StatusJsonTransformer statusJsonTransformer;
	
	private LocalizedJsonTransformer localizedJsonTransformer;
	
	private OptionJsonTransformer optionJsonTransformer;
	
	public LookupJsonTransformer(CrmServices crm) {
		super(crm);
		this.identifierJsonTransformer = new IdentifierJsonTransformer(crm);
		this.statusJsonTransformer = new StatusJsonTransformer(crm);
		this.localizedJsonTransformer = new LocalizedJsonTransformer(crm);
		this.optionJsonTransformer = new OptionJsonTransformer(crm);
	}

	@Override
	public Class<Lookup> getSourceType() {
		return Lookup.class;
	}

	@Override
	public JsonElement formatRoot(Lookup lookup) {
		return formatLocalized(lookup, null);
	}
	
	@Override
	public JsonElement formatLocalized(Lookup lookup, Locale locale) {
		List<JsonPair> pairs = new ArrayList<JsonPair>();
		formatType(pairs);
		if (lookup.getLookupId() != null) {
			pairs.add(new JsonPair("lookupId", identifierJsonTransformer
				.format(lookup.getLookupId(), locale)));
		}
		if (lookup.getStatus() != null) {
			pairs.add(new JsonPair("status", statusJsonTransformer
				.format(lookup.getStatus(), locale)));
		}
		formatBoolean(pairs, "mutable", lookup);
		if (lookup.getCode() != null) {
			pairs.add(new JsonPair("code", new JsonText(lookup.getCode())));
		}
		if (lookup.getName() != null) {
			pairs.add(new JsonPair("name", localizedJsonTransformer
				.format(lookup.getName(), locale)));
		}
		if (lookup.getParent() != null) {
			pairs.add(new JsonPair("parent", optionJsonTransformer
				.format(lookup.getParent(), locale)));
		}
		return new JsonObject(pairs);
	}

	@Override
	public Lookup parseJsonObject(JsonObject json, Locale locale) {
		Identifier lookupId = parseObject("lookupId", json, identifierJsonTransformer, locale);
		Status status = parseObject("status", json, statusJsonTransformer, locale);
		Boolean mutable = parseBoolean("mutable", json);
		Localized name = parseObject("name", json, localizedJsonTransformer, locale);
		Option parent = parseObject("parent", json, optionJsonTransformer, locale);
		return new Lookup(lookupId, status, mutable, name, parent);
	}

}
