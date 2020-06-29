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
		if (parentId == null) {
			return repos.saveOption(new Option(repos.generateForType(type, name.getCode()), parentId, type, Status.ACTIVE, true, name));
		}
		Option parent = repos.findOption(parentId);				
		/* update the code to prepend the parent code */
		Localized updatedName = name.withCode(parent.getCode() + "/" + name.getCode());
		/* construct the identifier based off of the fully qualified code */
		OptionIdentifier optionId = repos.generateForType(type, updatedName.getCode());
		return repos.saveOption(new Option(optionId, parentId, type, Status.ACTIVE, true, updatedName));
	}

	@Override
	public Option findOption(OptionIdentifier optionId) {
		return repos.findOption(optionId);
	}

	@Override
	public Option findOptionByCode(Type type, String optionCode) {
		return repos.findOption(repos.generateForType(type, optionCode));
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