package ca.magex.crm.api.common;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class Telephone {

	private Long number;

	private Long extension;

	public Telephone(Long number, Long extension) {
		super();
		this.number = number;
		this.extension = extension;
	}

	public Long getNumber() {
		return number;
	}

	public Long getExtension() {
		return extension;
	}
	
	@Override
	public int hashCode() {
		return HashCodeBuilder.reflectionHashCode(this);
	}
	
	@Override
	public boolean equals(Object obj) {
		return EqualsBuilder.reflectionEquals(this, obj);
	}

}
