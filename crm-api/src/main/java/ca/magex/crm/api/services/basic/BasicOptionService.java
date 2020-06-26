package ca.magex.crm.api.services.basic;

import ca.magex.crm.api.filters.OptionsFilter;
import ca.magex.crm.api.filters.Paging;
import ca.magex.crm.api.repositories.CrmRepositories;
import ca.magex.crm.api.services.CrmOptionService;
import ca.magex.crm.api.system.FilteredPage;
import ca.magex.crm.api.system.Identifier;
import ca.magex.crm.api.system.Localized;
import ca.magex.crm.api.system.Option;
import ca.magex.crm.api.system.Status;
import ca.magex.crm.api.system.Type;

public class BasicOptionService implements CrmOptionService {

	private CrmRepositories repos;

	public BasicOptionService(CrmRepositories repos) {
		this.repos = repos;
	}

	@Override
	public Option createOption(Identifier parentId, String typeCode, Localized name) {
		return repos.saveOption(new Option(repos.generateOptionId(), parentId, Type.of(typeCode), Status.ACTIVE, true, name));
	}

	@Override
	public Option findOption(Identifier optionId) {
		return repos.findOption(optionId);
	}

	@Override
	public Option findOptionByCode(String typeCode, String optionCode) {
		return repos.findOptions(new OptionsFilter().withType(Type.of(typeCode)).withOptionCode(optionCode), OptionsFilter.getDefaultPaging()).getSingleItem();
	}

	@Override
	public Option updateOptionName(Identifier optionId, Localized name) {
		Option option = repos.findOption(optionId);
		if (option == null) {
			return null;
		}
		return repos.saveOption(option.withName(name));
	}

	@Override
	public Option enableOption(Identifier optionId) {
		Option option = repos.findOption(optionId);
		if (option == null) {
			return null;
		}
		return repos.saveOption(option.withStatus(Status.ACTIVE));
	}

	@Override
	public Option disableOption(Identifier optionId) {
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