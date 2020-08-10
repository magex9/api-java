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
import ca.magex.crm.api.system.Lang;
import ca.magex.crm.api.system.Localized;

public class LocalizedFilter implements CrmFilter<Localized> {

	private static final long serialVersionUID = Crm.SERIAL_UID_VERSION;
	
	public static final Sort SORT_ENGLISH_ASC = Sort.by(Order.asc(":" + Lang.ENGLISH));
	public static final Sort SORT_ENGLISH_DESC = Sort.by(Order.desc(":" + Lang.ENGLISH));
	public static final Sort SORT_FRENCH_ASC = Sort.by(Order.asc(":" + Lang.FRENCH));
	public static final Sort SORT_FRENCH_DESC = Sort.by(Order.desc(":" + Lang.FRENCH));
	public static final Sort SORT_CODE_ASC = Sort.by(Order.asc(":" + Lang.ROOT));
	public static final Sort SORT_CODE_DESC = Sort.by(Order.desc(":" + Lang.ROOT));

	public static final List<Sort> SORT_OPTIONS = List.of(
		SORT_ENGLISH_ASC,
		SORT_ENGLISH_DESC,
		SORT_FRENCH_ASC,
		SORT_FRENCH_DESC,
		SORT_CODE_ASC,
		SORT_CODE_DESC
	);

	private String englishName;
	
	private String frenchName;
	
	private String code;

	public LocalizedFilter() {
		this(null, null, null);
	}
	
	public LocalizedFilter(String englishName, String frenchName, String code) {
		this.englishName = englishName;
		this.frenchName = frenchName;
		this.code = code;
	}
	
	public LocalizedFilter(Map<String, Object> filterCriteria) {
		this.englishName = (String) filterCriteria.get("englishName");
		this.frenchName = (String) filterCriteria.get("frenchName");
		this.code = (String) filterCriteria.get("code");
	}
	
	public String getEnglishName() {
		return englishName;
	}
	
	public String getFrenchName() {
		return frenchName;
	}
	
	public String getCode() {
		return code;
	}
	
	public LocalizedFilter withEnglishName(String englishName) {
		return new LocalizedFilter(englishName, frenchName, code);
	}
	
	public LocalizedFilter withFrenchName(String frenchName) {
		return new LocalizedFilter(englishName, frenchName, code);
	}
	
	public LocalizedFilter withCode(String code) {
		return new LocalizedFilter(englishName, frenchName, code);
	}
	
	public static List<Sort> getSortOptions() {
		return SORT_OPTIONS;
	}
	
	public static Sort getDefaultSort() {
		return SORT_CODE_ASC;
	}

	public static Paging getDefaultPaging() {
		return new Paging(getDefaultSort());
	}

	@Override
	public boolean apply(Localized instance) {
		return List.of(instance)
			.stream()
			.filter(g -> this.getCode() == null || StringUtils.equalsIgnoreCase(this.getCode(), g.getCode()))
			.filter(g -> this.getEnglishName() == null || StringUtils.containsIgnoreCase(g.getEnglishName(), this.getEnglishName()))
			.filter(g -> this.getFrenchName() == null || StringUtils.containsIgnoreCase(g.getFrenchName(), this.getFrenchName()))
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