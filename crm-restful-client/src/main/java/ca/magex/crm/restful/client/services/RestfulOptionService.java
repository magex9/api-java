package ca.magex.crm.restful.client.services;

import java.util.List;

import ca.magex.crm.api.filters.OptionsFilter;
import ca.magex.crm.api.filters.Paging;
import ca.magex.crm.api.services.CrmOptionService;
import ca.magex.crm.api.system.FilteredPage;
import ca.magex.crm.api.system.Localized;
import ca.magex.crm.api.system.Option;
import ca.magex.crm.api.system.Status;
import ca.magex.crm.api.system.Type;
import ca.magex.crm.api.system.id.OptionIdentifier;
import ca.magex.crm.restful.client.RestTemplateClient;
import ca.magex.json.model.JsonObject;

public class RestfulOptionService implements CrmOptionService {
	
	private RestTemplateClient client;
	
	public RestfulOptionService(RestTemplateClient client) {
		this.client = client;
	}
	
	@Override
	public Option createOption(OptionIdentifier parentId, Type type, Localized name) {
		Option option = prototypeOption(parentId == null ? null : findOption(parentId), type, name);
		JsonObject json = client.post("/options", client.format(option, Option.class));
		return client.parse(json, Option.class);
	}

	@Override
	public Option findOption(OptionIdentifier optionId) {
		JsonObject json = client.get(optionId);
		return client.parse(json, Option.class);
	}

	@Override
	public Option findOptionByCode(Type type, String optionCode) {
		JsonObject json = client.get("/options/" + type.getCode().toLowerCase().replaceAll("_", "-") + "/" + optionCode.toLowerCase());
		return client.parse(json, Option.class);
	}

	@Override
	public Option updateOptionName(OptionIdentifier optionId, Localized name) {
		JsonObject json = client.patch(optionId, new JsonObject()
			.with("code", name.getCode())
			.with("english", name.getEnglishName())
			.with("french", name.getFrenchName())
		);
		return client.parse(json, Option.class);
	}

	@Override
	public Option enableOption(OptionIdentifier optionId) {
		JsonObject json = client.put(optionId + "/enable", new JsonObject().with("confirm", true));
		return client.parse(json, Option.class);	
	}

	@Override
	public Option disableOption(OptionIdentifier optionId) {
		JsonObject json = client.put(optionId + "/disable", new JsonObject().with("confirm", true));
		return client.parse(json, Option.class);
	}
	
	public JsonObject formatFilter(OptionsFilter filter) {
		return new JsonObject()
			.with("parentId", filter.getParentId() == null ? null : filter.getParentId().toString())
			.with("status", filter.getStatus() == null ? null : (client.format(filter.getStatus(), Status.class)).getString("@value"))
			.with("type", filter.getTypeCode())
			.with("name", filter.getName() == null ? null : filter.getName().getCode())
			.with("name.en", filter.getName() == null ? null : filter.getName().getEnglishName())
			.with("name.fr", filter.getName() == null ? null : filter.getName().getFrenchName())
			.prune();
	}

	@Override
	public long countOptions(OptionsFilter filter) {
		JsonObject json = client.get("/options/count", formatFilter(filter));
		return json.getLong("total");
	}

	@Override
	public FilteredPage<Option> findOptions(OptionsFilter filter, Paging paging) {
		JsonObject json = client.get("/options", formatFilter(filter));
		List<Option> content = client.parseList(json.getArray("content"), Option.class);
		return new FilteredPage<>(filter, paging, content, json.getLong("total"));
	}

}
