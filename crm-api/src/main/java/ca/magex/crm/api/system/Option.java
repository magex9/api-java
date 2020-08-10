package ca.magex.crm.api.system;

import java.io.Serializable;
import java.util.Locale;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import ca.magex.crm.api.Crm;
import ca.magex.crm.api.system.id.OptionIdentifier;

public class Option implements Serializable {

	public static final boolean MUTABLE = true;
	
	public static final boolean IMMUTABLE = false;
	
	private static final long serialVersionUID = Crm.SERIAL_UID_VERSION;

	private OptionIdentifier optionId;
	
	private OptionIdentifier parentId;
	
	private Type type;
	
	private Status status;
	
	private Boolean mutable;
	
	private Localized name;
	
	public Option(OptionIdentifier optionId, OptionIdentifier parentId, Type type, Status status, Boolean mutable, Localized name) {
		super();
		this.optionId = optionId;
		this.parentId = parentId;
		this.type = type;
		this.status = status;
		this.mutable = mutable;
		this.name = name;
	}
	
	@SuppressWarnings("unchecked")
	public <T extends OptionIdentifier> T getOptionId() {
		return (T) optionId;
	}
	
	public OptionIdentifier getParentId() {
		return parentId;
	}
	
	public Type getType() {
		return type;
	}
	
	public String getCode() {
		return name.get(Lang.ROOT);
	}
	
	public Status getStatus() {
		return status;
	}
	
	public Option withStatus(Status status) {
		return new Option(optionId, parentId, type, status, mutable, name);
	}
	
	public Boolean getMutable() {
		return mutable;
	}
	
	public Localized getName() {
		return name;
	}
	
	public String getName(Locale locale) {
		return name.get(locale);
	}
	
	public Option withName(Localized name) {
		if (!StringUtils.equals(name.getCode(), this.name.getCode()))
			throw new IllegalArgumentException("Cannot change option codes");
		return new Option(optionId, parentId, type, status, mutable, name);
	}
	
	@SuppressWarnings("unchecked")
	public <I extends OptionIdentifier> Choice<I> asChoice() {
		return new Choice<I>((I)optionId);
	}
	
	public static <I extends OptionIdentifier> Choice<I> of(String text) {
		return new Choice<I>(text);
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
		ToStringBuilder builder = new ToStringBuilder(this, ToStringStyle.JSON_STYLE)
				.append("type", type.getCode())
				.append("optionId", optionId)
				.append("parentId", parentId)
				.append("status", status)
				.append("mutable", mutable)
				.append("name", name);
		
		return builder.build();
	}	
	
}
