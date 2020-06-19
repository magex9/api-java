package ca.magex.crm.api.filters;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;

import ca.magex.crm.api.Crm;
import ca.magex.crm.api.exceptions.ApiException;
import ca.magex.crm.api.system.Lang;
import ca.magex.crm.api.system.Lookup;
import ca.magex.crm.api.system.Status;

public class LookupsFilter implements CrmFilter<Lookup> {

	private static final long serialVersionUID = Crm.SERIAL_UID_VERSION;
	
	public static final List<Sort> SORT_OPTIONS = List.of(
		Sort.by(Order.asc("englishName")),
		Sort.by(Order.desc("englishName")),
		Sort.by(Order.asc("frenchName")),
		Sort.by(Order.desc("frenchName")),
		Sort.by(Order.asc("lookupCode")),
		Sort.by(Order.desc("lookupCode")),
		Sort.by(Order.asc("parentCode")),
		Sort.by(Order.desc("parentCode")),
		Sort.by(Order.asc("status")),
		Sort.by(Order.desc("status"))
	);
	
	private String englishName;
	
	private String frenchName;
	
	private String lookupCode;
	
	private String parentCode;
	
	private Status status;

	public LookupsFilter() {
		this(null, null, null, null, null);
	}
	
	public LookupsFilter(String englishName, String frenchName, String lookupCode, String parentCode, Status status) {
		this.englishName = englishName;
		this.frenchName = frenchName;
		this.lookupCode = lookupCode;
		this.parentCode = parentCode;
		this.status = status;
	}
	
	public LookupsFilter(Map<String, Object> filterCriteria) {
		try {
			this.englishName = (String) filterCriteria.get("englishName");
			this.frenchName = (String) filterCriteria.get("frenchName");
			this.lookupCode = (String) filterCriteria.get("lookupCode");
			this.parentCode = (String) filterCriteria.get("parentCode");
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
			throw new ApiException("Unable to instantiate groups filter", cce);
		}
	}
	
	public String getEnglishName() {
		return englishName;
	}
	
	public String getFrenchName() {
		return frenchName;
	}
	
	public String getLookupCode() {
		return lookupCode;
	}
	
	public String getParentCode() {
		return parentCode;
	}
	
	public Status getStatus() {
		return status;
	}
	
	public LookupsFilter withEnglishName(String englishName) {
		return new LookupsFilter(englishName, frenchName, lookupCode, parentCode, status);
	}
	
	public LookupsFilter withFrenchName(String frenchName) {
		return new LookupsFilter(englishName, frenchName, lookupCode, parentCode, status);
	}
	
	public LookupsFilter withLookupCode(String lookupCode) {
		return new LookupsFilter(englishName, frenchName, lookupCode, parentCode, status);
	}
	
	public LookupsFilter withParentCode(String parentCode) {
		return new LookupsFilter(englishName, frenchName, lookupCode, parentCode, status);
	}
	
	public LookupsFilter withStatus(Status status) {
		return new LookupsFilter(englishName, frenchName, lookupCode, parentCode, status);
	}
	
	public static List<Sort> getSortOptions() {
		return SORT_OPTIONS;
	}
	
	public static Sort getDefaultSort() {
		return Sort.by(Order.asc("lookupCode"));
	}

	public static Paging getDefaultPaging() {
		return new Paging(getDefaultSort());
	}

	@Override
	public boolean apply(Lookup instance) {
		return List.of(instance)
			.stream()
			.filter(g -> this.getEnglishName() == null || StringUtils.containsIgnoreCase(g.getName(Lang.ENGLISH), this.getEnglishName()))
			.filter(g -> this.getFrenchName() == null || StringUtils.containsIgnoreCase(g.getName(Lang.FRENCH), this.getFrenchName()))
			.filter(g -> this.getLookupCode() == null || StringUtils.equalsIgnoreCase(this.getLookupCode(), g.getCode()))
			.filter(g -> this.getParentCode() == null || StringUtils.equalsIgnoreCase(this.getParentCode(), g.getParent().getCode()))
			.filter(g -> this.getStatus() == null || this.getStatus().equals(g.getStatus()))
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