package ca.magex.crm.api.services.basic;

import ca.magex.crm.api.filters.OptionsFilter;
import ca.magex.crm.api.filters.Paging;
import ca.magex.crm.api.repositories.CrmRepositories;
import ca.magex.crm.api.services.CrmOptionService;
import ca.magex.crm.api.system.FilteredPage;
import ca.magex.crm.api.system.Localized;
import ca.magex.crm.api.system.Option;
import ca.magex.crm.api.system.Status;
import ca.magex.crm.api.system.Type;
import ca.magex.crm.api.system.id.OptionIdentifier;

public class BasicOptionService implements CrmOptionService {

	private CrmRepositories repos;

	public BasicOptionService(CrmRepositories repos) {
		this.repos = repos;
	}

	@Override
	public Option createOption(OptionIdentifier parentId, Type type, Localized name) {
		return repos.saveOption(new Option(repos.generateForType(type), parentId, type, Status.ACTIVE, true, name));
	}

	@Override
	public Option findOption(OptionIdentifier optionId) {
		return repos.findOption(optionId);
	}

	@Override
	public Option findOptionByCode(Type type, String optionCode) {
		return repos.findOptions(new OptionsFilter().withType(type).withOptionCode(optionCode), OptionsFilter.getDefaultPaging()).getSingleItem();
	}

	@Override
	public Option updateOptionName(OptionIdentifier optionId, Localized name) {
		Option option = repos.findOption(optionId);
		if (option == null) {
			return null;
		}
		return repos.saveOption(option.withName(name));
	}

	@Override
	public Option enableOption(OptionIdentifier optionId) {
		Option option = repos.findOption(optionId);
		if (option == null) {
			return null;
		}
		return repos.saveOption(option.withStatus(Status.ACTIVE));
	}

	@Override
	public Option disableOption(OptionIdentifier optionId) {
		Option option = repos.findOption(optionId);
		if (option == null) {
			return null;
		}
		return repos.saveOption(option.withStatus(Status.INACTIVE));
	}

	@Override
	public FilteredPage<Option> findOptions(OptionsFilter filter, Paging paging) {
		return repos.findOptions(filter, paging);
	}
	
}