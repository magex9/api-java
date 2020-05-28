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

import ca.magex.crm.api.exceptions.ApiException;
import ca.magex.crm.api.roles.Group;
import ca.magex.crm.api.services.Crm;
import ca.magex.crm.api.system.Lang;
import ca.magex.crm.api.system.Status;

public class GroupsFilter implements CrmFilter<Group> {

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

	private String englishName;
	
	private String frenchName;
	
	private String code;
	
	private Status status;

	public GroupsFilter() {
		this(null, null, null, null);
	}
	
	public GroupsFilter(String englishName, String frenchName, String code, Status status) {
		this.englishName = englishName;
		this.frenchName = frenchName;
		this.code = code;
		this.status = status;
	}
	
	public GroupsFilter(Map<String, Object> filterCriteria) {
		try {
			this.englishName = (String) filterCriteria.get("englishName");
			this.frenchName = (String) filterCriteria.get("frenchName");
			this.code = (String) filterCriteria.get("code");
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
	
	public String getCode() {
		return code;
	}
	
	public Status getStatus() {
		return status;
	}
	
	public GroupsFilter withEnglishName(String englishName) {
		return new GroupsFilter(englishName, frenchName, code, status);
	}
	
	public GroupsFilter withFrenchName(String frenchName) {
		return new GroupsFilter(englishName, frenchName, code, status);
	}
	
	public GroupsFilter withCode(String code) {
		return new GroupsFilter(englishName, frenchName, code, status);
	}
	
	public GroupsFilter withStatus(Status status) {
		return new GroupsFilter(englishName, frenchName, code, status);
	}
	
	public static List<Sort> getSortOptions() {
		return SORT_OPTIONS;
	}
	
	public static Sort getDefaultSort() {
		return Sort.by(Order.asc("code"));
	}

	public static Paging getDefaultPaging() {
		return new Paging(getDefaultSort());
	}

	@Override
	public boolean apply(Group instance) {
		return List.of(instance)
				.stream()
				.filter(g -> this.getCode() == null || StringUtils.equalsIgnoreCase(this.getCode(), g.getCode()))
				.filter(g -> this.getEnglishName() == null || StringUtils.containsIgnoreCase(g.getName(Lang.ENGLISH),this.getEnglishName()))
				.filter(g -> this.getFrenchName() == null || StringUtils.containsIgnoreCase(g.getName(Lang.FRENCH),this.getFrenchName()))
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