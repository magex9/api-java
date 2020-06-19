package ca.magex.crm.api.filters;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.domain.Sort.Order;

import ca.magex.crm.api.Crm;
import ca.magex.crm.api.exceptions.ApiException;
import ca.magex.crm.api.system.Identifier;
import ca.magex.crm.api.system.Lang;
import ca.magex.crm.api.system.Option;
import ca.magex.crm.api.system.Status;

public class OptionsFilter implements CrmFilter<Option> {

	private static final long serialVersionUID = Crm.SERIAL_UID_VERSION;

	public static final List<Sort> SORT_OPTIONS = List.of(
		Sort.by(Order.asc("englishName")),
		Sort.by(Order.desc("englishName")),
		Sort.by(Order.asc("frenchName")),
		Sort.by(Order.desc("frenchName")),
		Sort.by(Order.asc("code")),
		Sort.by(Order.desc("code")),
		Sort.by(Order.asc("status")),
		Sort.by(Order.desc("status"))
	);

	private Identifier lookupId;

	private String englishName;

	private String frenchName;

	private String optionCode;

	private Status status;

	public OptionsFilter() {
		this(null, null, null, null, null);
	}

	public OptionsFilter(Identifier lookupId, String englishName, String frenchName, String optionCode, Status status) {
		this.lookupId = lookupId;
		this.englishName = englishName;
		this.frenchName = frenchName;
		this.optionCode = optionCode;
		this.status = status;
	}

	public OptionsFilter(Map<String, Object> filterCriteria) {
		try {
			this.lookupId = filterCriteria.containsKey("lookupId") ? new Identifier((String) filterCriteria.get("lookupId")) : null;
			this.englishName = (String) filterCriteria.get("englishName");
			this.frenchName = (String) filterCriteria.get("frenchName");
			this.optionCode = (String) filterCriteria.get("optionCode");
			this.status = null;
			if (filterCriteria.containsKey("status") && StringUtils.isNotBlank((String) filterCriteria.get("status"))) {
				try {
					this.status = Status.valueOf(StringUtils.upperCase((String) filterCriteria.get("status")));
				} catch (IllegalArgumentException e) {
					throw new ApiException("Invalid status value '" + filterCriteria.get("status") + "' expected one of {" + StringUtils.join(Status.values(), ",") + "}");
				}
			}
		}
		catch(ClassCastException cce) {
			throw new ApiException("Unable to instantiate roles filter", cce);
		}
	}
	
	public Identifier getLookupId() {
		return lookupId;
	}
	
	public String getEnglishName() {
		return englishName;
	}

	public String getFrenchName() {
		return frenchName;
	}

	public String getOptionCode() {
		return optionCode;
	}

	public Status getStatus() {
		return status;
	}

	public OptionsFilter withLookupId(Identifier lookupId) {
		return new OptionsFilter(lookupId, englishName, frenchName, optionCode, status);
	}

	public OptionsFilter withEnglishName(String englishName) {
		return new OptionsFilter(lookupId, englishName, frenchName, optionCode, status);
	}

	public OptionsFilter withFrenchName(String frenchName) {
		return new OptionsFilter(lookupId, englishName, frenchName, optionCode, status);
	}

	public OptionsFilter withOptionCode(String optionCode) {
		return new OptionsFilter(lookupId, englishName, frenchName, optionCode, status);
	}

	public OptionsFilter withStatus(Status status) {
		return new OptionsFilter(lookupId, englishName, frenchName, optionCode, status);
	}

	public static List<Sort> getSortOptions() {
		return SORT_OPTIONS;
	}

	public static Sort getDefaultSort() {
		return Sort.by(Direction.ASC, "optionCode");
	}

	public static Paging getDefaultPaging() {
		return new Paging(getDefaultSort());
	}

	@Override
	public boolean apply(Option instance) {
		return List.of(instance)
			.stream()
			.filter(o -> this.getLookupId() == null || this.getLookupId().equals(o.getLookupId()))
			.filter(o -> this.getEnglishName() == null || StringUtils.containsIgnoreCase(o.getName(Lang.ENGLISH), this.getEnglishName()))
			.filter(o -> this.getFrenchName() == null || StringUtils.containsIgnoreCase(o.getName(Lang.FRENCH), this.getFrenchName()))
			.filter(o -> this.getOptionCode() == null || StringUtils.equalsIgnoreCase(this.getOptionCode(), o.getCode()))
			.filter(o -> this.getStatus() == null || this.getStatus().equals(o.getStatus()))
			.findAny()
			.isPresent();
	}

	@Override
	public int hashCode() {
		return HashCodeBuilder.reflectionHashCode(this);
	}

	@Override
	public boolean equals(Object obj) {
		return EqualsBuilder.reflectionEquals(this, obj);
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this, ToStringStyle.JSON_STYLE);
	}
}