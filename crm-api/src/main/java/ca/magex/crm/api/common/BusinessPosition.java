package ca.magex.crm.api.common;

import java.io.Serializable;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import ca.magex.crm.api.Crm;

public class BusinessPosition implements Serializable {

	private static final long serialVersionUID = Crm.SERIAL_UID_VERSION;

	private String sector;
	
	private String unit;
	
	private String classification;

	public BusinessPosition(String sector, String unit, String classification) {
		super();
		this.sector = sector;
		this.unit = unit;
		this.classification = classification;
	}

	public String getSector() {
		return sector;
	}

	public BusinessPosition withSector(String sector) {
		return new BusinessPosition(sector, unit, classification);
	}

	public String getUnit() {
		return unit;
	}

	public BusinessPosition withUnit(String unit) {
		return new BusinessPosition(sector, unit, classification);
	}

	public String getClassification() {
		return classification;
	}

	public BusinessPosition withClassification(String classification) {
		return new BusinessPosition(sector, unit, classification);
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
