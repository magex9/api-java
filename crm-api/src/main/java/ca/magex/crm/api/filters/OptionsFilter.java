package ca.magex.crm.api.filters;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;

import ca.magex.crm.api.Crm;
import ca.magex.crm.api.exceptions.ApiException;
import ca.magex.crm.api.exceptions.ItemNotFoundException;
import ca.magex.crm.api.system.Identifier;
import ca.magex.crm.api.system.Lang;
import ca.magex.crm.api.system.Localized;
import ca.magex.crm.api.system.Option;
import ca.magex.crm.api.system.Status;
import ca.magex.crm.api.system.Type;
import ca.magex.crm.api.system.id.IdentifierFactory;
import ca.magex.crm.api.system.id.OptionIdentifier;

public class OptionsFilter implements CrmFilter<Option> {

	private static final long serialVersionUID = Crm.SERIAL_UID_VERSION;

	public static final List<Sort> SORT_OPTIONS = List.of(
			Sort.by(Order.asc("code")),
			Sort.by(Order.desc("code")),
			Sort.by(Order.asc("englishName")),
			Sort.by(Order.desc("englishName")),
			Sort.by(Order.asc("frenchName")),
			Sort.by(Order.desc("frenchName")),
			Sort.by(Order.asc("status")),
			Sort.by(Order.desc("status")));

	private Localized name;

	private OptionIdentifier parentId;

	private Type type;

	private Status status;

	public OptionsFilter() {
		this(null, null, null, null);
	}

	public OptionsFilter(Localized name, OptionIdentifier parentId, Type type, Status status) {
		this.name = name;
		this.parentId = parentId;
		this.type = type;
		this.status = status;
	}

	public OptionsFilter(Map<String, Object> filterCriteria) {
		try {
			String code = "";
			String english = "";
			String french = "";
			if (filterCriteria.containsKey("code")) {
				code = (String) filterCriteria.get("code");
			} 
			if (filterCriteria.containsKey("english")) {
				english = (String) filterCriteria.get("english");
			} 
			if (filterCriteria.containsKey("french")) {
				french = (String) filterCriteria.get("french");
			}
			if (StringUtils.isNotBlank(code) || StringUtils.isNotBlank(english) || StringUtils.isNotBlank(french)) {
				this.name = new Localized(code, english, french);
			}
			this.parentId = filterCriteria.containsKey("parentId") ? IdentifierFactory.forId((CharSequence) filterCriteria.get("parentId")) : null;
			this.type = null;
			if (filterCriteria.containsKey("type") && StringUtils.isNotBlank((String) filterCriteria.get("type"))) {
				try {
					this.type = Type.of(StringUtils.upperCase((String) filterCriteria.get("type")));
				} catch (ItemNotFoundException e) {					
					throw new ApiException("Invalid type value '" + filterCriteria.get("type") + "' expected one of {" + Stream.of(Type.values()).map(Type::name).collect(Collectors.joining(",")) + "}");
				}
			}
			this.status = null;
			if (filterCriteria.containsKey("status") && StringUtils.isNotBlank((String) filterCriteria.get("status"))) {
				try {
					this.status = Status.of(StringUtils.upperCase((String) filterCriteria.get("status")));
				} catch (ItemNotFoundException e) {
					throw new ApiException("Invalid status value '" + filterCriteria.get("status") + "' expected one of {" + Stream.of(Status.values()).map(Status::name).collect(Collectors.joining(",")) + "}");
				}
			}
		} catch (ClassCastException cce) {
			throw new ApiException("Unable to instantiate roles filter", cce);
		}
	}

	public Localized getName() {
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
	
	public String getEnglishName() {
		return name == null ? null : name.getEnglishName();
	}
	
	public String getFrenchName() {
		return name == null ? null : name.getFrenchName();
	}
	
	public String getCode() {
		return name == null ? null : name.getCode();
	}

	public OptionsFilter withName(Localized name) {
		return new OptionsFilter(name, parentId, type, status);
	}

	public OptionsFilter withParentId(OptionIdentifier parentId) {
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
		return Sort.by(Order.asc("type"), Order.asc("code"));
	}

	public static Paging getDefaultPaging() {
		return new Paging(getDefaultSort());
	}

	@Override
	public boolean apply(Option instance) {
		return List.of(instance)
				.stream()
				.filter(o -> 
						(this.getName() == null) || 
						(StringUtils.isBlank(this.getName().get(Lang.ROOT))) || 
						(StringUtils.endsWith(o.getCode(), "/" + this.getName().get(Lang.ROOT))) ||
						(StringUtils.equals(o.getCode(), this.getName().get(Lang.ROOT))))
				.filter(o ->
						(this.getName() == null) ||
						(StringUtils.isBlank(this.getName().get(Lang.ENGLISH))) ||
						(StringUtils.equalsIgnoreCase(o.getName(Lang.ENGLISH), this.getName().get(Lang.ENGLISH))))
				.filter(o ->
					(this.getName() == null) ||
					(StringUtils.isBlank(this.getName().get(Lang.FRENCH))) ||
					(StringUtils.equalsIgnoreCase(o.getName(Lang.FRENCH), this.getName().get(Lang.FRENCH))))
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
		return new ToStringBuilder(this, ToStringStyle.JSON_STYLE)
				.append("name", name == null ? (Object) null : ToStringBuilder.reflectionToString(getName(), ToStringStyle.JSON_STYLE))
				.append("parentId", parentId == null ? (Object) null : parentId)
				.append("type", type == null ? (Object) null : type.name())
				.append("status", status == null ? (Object) null : status)
				.build();
	}
}