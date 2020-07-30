package ca.magex.crm.restful.client.services;

import java.util.List;
import java.util.stream.Collectors;

import ca.magex.crm.api.filters.OptionsFilter;
import ca.magex.crm.api.filters.Paging;
import ca.magex.crm.api.services.CrmOptionService;
import ca.magex.crm.api.system.FilteredPage;
import ca.magex.crm.api.system.Localized;
import ca.magex.crm.api.system.Option;
import ca.magex.crm.api.system.Type;
import ca.magex.crm.api.system.id.OptionIdentifier;
import ca.magex.crm.api.system.id.PhraseIdentifier;
import ca.magex.crm.api.transform.Transformer;
import ca.magex.crm.restful.client.RestTemplateClient;
import ca.magex.crm.transform.json.OptionJsonTransformer;
import ca.magex.json.model.JsonElement;
import ca.magex.json.model.JsonObject;

public class RestfulOptionService implements CrmOptionService {
	
	public static void main(String[] args) {
//		String code = "TMP" + Calendar.getInstance().getTimeInMillis();
//		
		RestTemplateClient rest = new RestTemplateClient("http://localhost:9002/crm", null, "admin", "admin");
		
		RestfulOptionService client = new RestfulOptionService(rest);
//		
//		System.out.println(client.findOptions(new OptionsFilter().withType(Type.STATUS)));
//		
		System.out.println(client.findOptionByCode(Type.PHRASE, PhraseIdentifier.VALIDATION_FIELD_FORMAT.getCode()));
		
		
	}
	
	private RestTemplateClient client;
	
	private Transformer<Option, JsonElement> transformer;
	
	public RestfulOptionService(RestTemplateClient client) {
		this.client = client;
		this.transformer = new OptionJsonTransformer(this);
	}
	
	@Override
	public Option createOption(OptionIdentifier parentId, Type type, Localized name) {
		Option option = prototypeOption(parentId == null ? null : findOption(parentId), type, name);
		JsonObject json = client.post("/options", (JsonObject)transformer.format(option, client.getLocale()));
		return transformer.parse(json, client.getLocale());
	}

	@Override
	public Option findOption(OptionIdentifier optionId) {
		JsonObject json = client.get(optionId);
		return transformer.parse(json, client.getLocale());
	}

	@Override
	public Option findOptionByCode(Type type, String optionCode) {
		JsonObject json = client.get("/types/" + type.getCode().toLowerCase().replaceAll("_", "-") + "/" + optionCode.toLowerCase());
		return transformer.parse(json, client.getLocale());
	}

	@Override
	public Option updateOptionName(OptionIdentifier optionId, Localized name) {
		JsonObject json = client.patch(optionId, new JsonObject()
			.with("code", name.getCode())
			.with("english", name.getEnglishName())
			.with("french", name.getFrenchName())
		);
		return transformer.parse(json, client.getLocale());
	}

	@Override
	public Option enableOption(OptionIdentifier optionId) {
		JsonObject json = client.put(optionId + "/enable", new JsonObject().with("confirm", true));
		return transformer.parse(json, client.getLocale());	
	}

	@Override
	public Option disableOption(OptionIdentifier optionId) {
		JsonObject json = client.put(optionId + "/disable", new JsonObject().with("confirm", true));
		return transformer.parse(json, client.getLocale());
	}

	@Override
	public long countOptions(OptionsFilter filter) {
		JsonObject json = client.get("/options/count", new JsonObject()
			.with("parentId", filter.getParentId())
			.with("status", filter.getStatus())
			.with("type", filter.getTypeCode())
			.with("name", filter.getName() == null ? null : filter.getName().get(client.getLocale()))
			.prune());
		return json.getLong("total");
	}

	@Override
	public FilteredPage<Option> findOptions(OptionsFilter filter, Paging paging) {
		JsonObject json = client.get("/options", new JsonObject()
			.with("parentId", filter.getParentId())
			.with("status", filter.getStatus())
			.with("type", filter.getTypeCode())
			.with("name", filter.getName() == null ? null : filter.getName().get(client.getLocale()))
			.prune());
		List<Option> content = json.getArray("content", JsonObject.class).stream()
			.map(e -> transformer.parse(e, client.getLocale())).collect(Collectors.toList());
		return new FilteredPage<>(filter, paging, content, json.getLong("total"));
	}

}
