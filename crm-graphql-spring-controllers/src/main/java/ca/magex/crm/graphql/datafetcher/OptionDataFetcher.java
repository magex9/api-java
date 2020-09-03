package ca.magex.crm.graphql.datafetcher;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import ca.magex.crm.api.crm.OrganizationDetails;
import ca.magex.crm.api.crm.PersonDetails;
import ca.magex.crm.api.crm.UserDetails;
import ca.magex.crm.api.filters.OptionsFilter;
import ca.magex.crm.api.system.Localized;
import ca.magex.crm.api.system.Option;
import ca.magex.crm.api.system.id.IdentifierFactory;
import ca.magex.crm.api.system.id.OptionIdentifier;
import ca.magex.crm.graphql.controller.GraphQLController;
import graphql.schema.DataFetcher;

@Component
public class OptionDataFetcher extends AbstractDataFetcher {

	private static Logger logger = LoggerFactory.getLogger(GraphQLController.class);	

	public DataFetcher<List<Option>> findAuthenticationGroupsForOrg() {
		return (environment) -> {
			logger.info("Entering findAuthenticationGroupsForOrg@" + OptionDataFetcher.class.getSimpleName());
			OrganizationDetails source = environment.getSource();
			return source.getAuthenticationGroupIds()
				.stream()
				.map((id) -> crm.findOption(id))
				.collect(Collectors.toList());			
		};
	}
	
	public DataFetcher<List<Option>> findAuthenticationRolesForUser() {
		return (environment) -> {
			logger.info("Entering findAuthenticationGroupsForOrg@" + OptionDataFetcher.class.getSimpleName());
			UserDetails source = environment.getSource();
			return source.getAuthenticationRoleIds()
				.stream()
				.map((id) -> crm.findOption(id))
				.collect(Collectors.toList());			
		};
	}
	
	public DataFetcher<List<Option>> findBusinessGroupsForOrg() {
		return (environment) -> {
			logger.info("Entering findBusinessGroupsForOrg@" + OptionDataFetcher.class.getSimpleName());
			OrganizationDetails source = environment.getSource();
			return source.getBusinessGroupIds()
				.stream()
				.map((id) -> crm.findOption(id))
				.collect(Collectors.toList());			
		};
	}
	
	public DataFetcher<List<Option>> findBusinessRolesForPerson() {
		return (environment) -> {
			logger.info("Entering findBusinessGroupsForOrg@" + OptionDataFetcher.class.getSimpleName());
			PersonDetails source = environment.getSource();
			return source.getBusinessRoleIds()
				.stream()
				.map((id) -> crm.findOption(id))
				.collect(Collectors.toList());			
		};
	}
	
	public DataFetcher<Option> findOption() {
		return (environment) -> {
			logger.info("Entering findOption@" + OptionDataFetcher.class.getSimpleName());
			OptionIdentifier optionId = extractOptionIdentifier(environment, "optionId");
			return crm.findOption(optionId);
		};
	}
	
	public DataFetcher<Integer> countOptions() {
		return (environment) -> {
			logger.info("Entering countOptions@" + OptionDataFetcher.class.getSimpleName());
			return (int) crm.countOptions(new OptionsFilter(extractFilter(environment)));
		};
	}

	public DataFetcher<Page<Option>> findOptions() {
		return (environment) -> {
			logger.info("Entering findOptions@" + OptionDataFetcher.class.getSimpleName());
			return crm.findOptions(new OptionsFilter(extractFilter(environment)), extractPaging(environment));
		};
	}
	
	public DataFetcher<Option> findParentOption() {
		return (environment) -> {
			logger.info("Entering findParentOption@" + OptionDataFetcher.class.getSimpleName());
			Option source = environment.getSource();
			if (source.getParentId() == null) {
				return null;
			}
			return crm.findOption(source.getParentId());
		};
	}
	
	public DataFetcher<Map<String,Object>> findOptionActions() {
		return (environment) -> {
			logger.info("Entering findOptionActions@" + OptionDataFetcher.class.getSimpleName());
			Option source = environment.getSource();
			return Map.of(
					"update", crm.canUpdateOption(source.getOptionId()),
					"enable", crm.canEnableOption(source.getOptionId()),
					"disable", crm.canDisableOption(source.getOptionId()));
		};
	}
	
	public DataFetcher<Option> createOption() {
		return (environment) -> {
			logger.info("Entering createOption@" + OptionDataFetcher.class.getSimpleName());			
			/* create the new option */
			return crm.createOption(
					extractOptionIdentifier(environment, "parentId"), 
					extractType(environment, "type"), 
					extractLocalized(environment, "name"));			
		};
	}
	
	public DataFetcher<Option> updateOption() {
		return (environment) -> {
			logger.info("Entering updateOption@" + OptionDataFetcher.class.getSimpleName());
			OptionIdentifier optionId = extractOptionIdentifier(environment, "optionId");
			Option option = crm.findOption(optionId);
			Localized newName = option.getName();
			if (environment.getArgument("english") != null) {
				newName = newName.withEnglishName(environment.getArgument("english"));
			}
			if (environment.getArgument("french") != null) {
				newName = newName.withFrenchName(environment.getArgument("french"));
			}
			if (!option.getName().equals(newName)) {
				option = crm.updateOptionName(optionId, newName);
			}
			return option;
		};
	}
	
	public DataFetcher<Option> enableOption() {
		return (environment) -> {
			logger.info("Entering enableOption@" + OptionDataFetcher.class.getSimpleName());
			OptionIdentifier optionId = IdentifierFactory.forOptionId((String) environment.getArgument("optionId"));
			return crm.findOption(crm.enableOption(optionId).getOptionId());
		};
	}
	
	public DataFetcher<Option> disableOption() {
		return (environment) -> {
			logger.info("Entering disableOption@" + OptionDataFetcher.class.getSimpleName());
			OptionIdentifier optionId = IdentifierFactory.forOptionId((String) environment.getArgument("optionId"));
			return crm.findOption(crm.disableOption(optionId).getOptionId());
		};
	}
}
