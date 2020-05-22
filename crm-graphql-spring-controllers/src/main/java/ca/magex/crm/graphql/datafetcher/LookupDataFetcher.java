package ca.magex.crm.graphql.datafetcher;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import ca.magex.crm.api.exceptions.ItemNotFoundException;
import ca.magex.crm.api.lookup.CrmLookupItem;
import ca.magex.crm.api.system.Status;
import graphql.schema.DataFetcher;

@Component
public class LookupDataFetcher extends AbstractDataFetcher {

	private static Logger logger = LoggerFactory.getLogger(LookupDataFetcher.class);
	
	public DataFetcher<List<CrmLookupItem>> findCodeLookups() {
		return (environment) -> {
			logger.info("Entering findCodeLookups@" + LookupDataFetcher.class.getSimpleName());
			final String category = environment.getArgument("category");
			final String code = environment.getArgument("code");
			switch (StringUtils.upperCase(category)) {
			case "COUNTRY":
				if (StringUtils.isBlank(code)) {
					return crm.findCountries().stream().map((c) -> (CrmLookupItem) c).collect(Collectors.toList());
				} else {
					return Arrays.asList(crm.findCountryByCode(code));
				}
			case "SALUTATION":
				if (StringUtils.isBlank(code)) {
					return crm.findSalutations().stream().map((c) -> (CrmLookupItem) c).collect(Collectors.toList());
				} else {
					return Arrays.asList(crm.findSalutationByCode(code));
				}
			case "LANGUAGE":
				if (StringUtils.isBlank(code)) {
					return crm.findLanguages().stream().map((c) -> (CrmLookupItem) c).collect(Collectors.toList());
				} else {
					return Arrays.asList(crm.findLanguageByCode(code));
				}
			case "SECTOR":
				if (StringUtils.isBlank(code)) {
					return crm.findBusinessSectors().stream().map((c) -> (CrmLookupItem) c).collect(Collectors.toList());
				} else {
					return Arrays.asList(crm.findBusinessSectorByCode(code));
				}
			case "UNIT":
				if (StringUtils.isBlank(code)) {
					return crm.findBusinessUnits().stream().map((c) -> (CrmLookupItem) c).collect(Collectors.toList());
				} else {
					return Arrays.asList(crm.findBusinessUnitByCode(code));
				}
			case "CLASSIFICATION":
				if (StringUtils.isBlank(code)) {
					return crm.findBusinessClassifications().stream().map((c) -> (CrmLookupItem) c).collect(Collectors.toList());
				} else {
					return Arrays.asList(crm.findBusinessClassificationByCode(code));
				}			
			case "STATUS":
				if (StringUtils.isBlank(code)) {
					return Arrays.asList(Status.values());
				} else {
					return Arrays.asList(Status.valueOf(StringUtils.upperCase(code)));
				}
			default:
				throw new ItemNotFoundException("invalid category '" + category + "'");
			}
		};
	}
}
