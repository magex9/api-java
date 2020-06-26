package ca.magex.crm.api.filters;

import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;

import ca.magex.crm.api.Crm;
import ca.magex.crm.api.exceptions.ApiException;
import ca.magex.crm.api.repositories.CrmOptionRepository;
import ca.magex.crm.api.system.Identifier;
import ca.magex.crm.api.system.Lang;
import ca.magex.crm.api.system.Option;
import ca.magex.crm.api.system.Status;
import ca.magex.crm.api.system.Type;

public class OptionsFilter implements CrmFilter<Option> {

	private static final long serialVersionUID = Crm.SERIAL_UID_VERSION;

	public static final List<Sort> SORT_OPTIONS = List.of(
		Sort.by(Order.asc("optionCode")),
		Sort.by(Order.desc("optionCode")),
		Sort.by(Order.asc("englishName")),
		Sort.by(Order.desc("englishName")),
		Sort.by(Order.asc("frenchName")),
		Sort.by(Order.desc("frenchName")),
		Sort.by(Order.asc("status")),
		Sort.by(Order.desc("status"))
	);
	
	private ImmutablePair<Locale, String> name;

	private Identifier parentId;

	private Type type;

	private Status status;

	public OptionsFilter() {
		this(null, null, null, null);
	}

	public OptionsFilter(ImmutablePair<Locale, String> name, Identifier parentId, Type type, Status status) {
		this.name = name;
		this.parentId = parentId;
		this.type = type;
		this.status = status;
	}

	public OptionsFilter(Map<String, Object> filterCriteria) {
		try {
			this.name = filterCriteria.containsKey("name") ? new ImmutablePair<Locale, String>(Lang.ROOT, (String) filterCriteria.get("name")) : null;
			this.parentId = filterCriteria.containsKey("parentId") ? new Identifier(CrmOptionRepository.CONTEXT, (String) filterCriteria.get("parentId")) : null;
			this.type = null;
			if (filterCriteria.containsKey("type") && StringUtils.isNotBlank((String) filterCriteria.get("type"))) {
				try {
					this.type = Type.valueOf(StringUtils.upperCase((String) filterCriteria.get("type")));
				} catch (IllegalArgumentException e) {
					throw new ApiException("Invalid type value '" + filterCriteria.get("type") + "' expected one of {" + StringUtils.join(Type.values(), ",") + "}");
				}
			}
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
	
	public Pair<Locale, String> getName() {
		return name;
	}
	
	public Identifier getParentId() {
		return parentId;
	}
	
	public Type getType() {
		return type;
	}

	public Status getStatus() {
		return status;
	}
	
	public OptionsFilter withName(Locale locale, String name) {
		return new OptionsFilter(new ImmutablePair<Locale, String>(locale, name), parentId, type, status);
	}
	
	public OptionsFilter withOptionCode(String optionCode) {
		return withName(Lang.ROOT, optionCode);
	}

	public OptionsFilter withParentId(Identifier parentId) {
		return new OptionsFilter(name, parentId, type, status);
	}

	public OptionsFilter withType(Type type) {
		return new OptionsFilter(name, parentId, type, status);
	}

	public OptionsFilter withStatus(Status status) {
		return new OptionsFilter(name, parentId, type, status);
	}

	public static List<Sort> getSortOptions() {
		return SORT_OPTIONS;
	}

	public static Sort getDefaultSort() {
		return Sort.by(Order.asc("optionCode"));
	}

	public static Paging getDefaultPaging() {
		return new Paging(getDefaultSort());
	}

	@Override
	public boolean apply(Option instance) {
		return List.of(instance)
			.stream()
			.filter(g -> this.getName() == null || StringUtils.equalsIgnoreCase(g.getName(this.getName().getLeft()), this.getName().getRight()))
			.filter(o -> this.getParentId() == null || this.getParentId().equals(o.getParentId()))
			.filter(o -> this.getType() == null || this.getType().equals(o.getType()))
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