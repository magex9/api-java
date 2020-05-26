package ca.magex.crm.graphql.datafetcher;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import ca.magex.crm.api.exceptions.ApiException;
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
			final String qualifier = environment.getArgument("qualifier");
			switch (StringUtils.upperCase(category)) {
			case "COUNTRY":
				if (qualifier != null) {
					throw new ApiException("qualifier not required for country lookup");
				}
				if (StringUtils.isBlank(code)) {
					return crm.findCountries().stream().map((c) -> (CrmLookupItem) c).collect(Collectors.toList());
				} else {
					return Arrays.asList(crm.findCountryByCode(code));
				}
			case "SALUTATION":
				if (qualifier != null) {
					throw new ApiException("qualifier not required for salutation lookup");
				}
				if (StringUtils.isBlank(code)) {
					return crm.findSalutations().stream().map((c) -> (CrmLookupItem) c).collect(Collectors.toList());
				} else {
					return Arrays.asList(crm.findSalutationByCode(code));
				}
			case "LANGUAGE":
				if (qualifier != null) {
					throw new ApiException("qualifier not required for language lookup");
				}
				if (StringUtils.isBlank(code)) {
					return crm.findLanguages().stream().map((c) -> (CrmLookupItem) c).collect(Collectors.toList());
				} else {
					return Arrays.asList(crm.findLanguageByCode(code));
				}
			case "SECTOR":
				if (qualifier != null) {
					throw new ApiException("qualifier not required for sector lookup");
				}
				if (StringUtils.isBlank(code)) {
					return crm.findBusinessSectors().stream().map((c) -> (CrmLookupItem) c).collect(Collectors.toList());
				} else {
					return Arrays.asList(crm.findBusinessSectorByCode(code));
				}
			case "UNIT":
				if (qualifier != null) {
					throw new ApiException("qualifier not required for unit lookup");
				}
				if (StringUtils.isBlank(code)) {
					return crm.findBusinessUnits().stream().map((c) -> (CrmLookupItem) c).collect(Collectors.toList());
				} else {
					return Arrays.asList(crm.findBusinessUnitByCode(code));
				}
			case "CLASSIFICATION":
				if (qualifier != null) {
					throw new ApiException("qualifier not required for classification lookup");
				}
				if (StringUtils.isBlank(code)) {
					return crm.findBusinessClassifications().stream().map((c) -> (CrmLookupItem) c).collect(Collectors.toList());
				} else {
					return Arrays.asList(crm.findBusinessClassificationByCode(code));
				}
			case "PROVINCE":				
				if (qualifier == null) {
					throw new ApiException("qualifier required for province lookup");
				}
				if (StringUtils.isBlank(code)) {
					return crm.findProvinces(qualifier).stream().map((c) -> (CrmLookupItem) c).collect(Collectors.toList());
				} else {
					return Arrays.asList(crm.findProvinceByCode(code, qualifier));
				}
			case "STATUS":
				if (qualifier != null) {
					throw new ApiException("qualifier not required for status lookup");
				}
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
