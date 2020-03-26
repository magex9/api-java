package ca.magex.crm.api.common;

public class BusinessUnit {

	private Integer sector;
	
	private Integer unit;
	
	private Integer level;

	public BusinessUnit(Integer sector, Integer unit, Integer level) {
		super();
		this.sector = sector;
		this.unit = unit;
		this.level = level;
	}

	public Integer getSector() {
		return sector;
	}

	public BusinessUnit withSector(Integer sector) {
		return new BusinessUnit(sector, sector, sector);
	}

	public Integer getUnit() {
		return unit;
	}

	public BusinessUnit withUnit(Integer unit) {
		return new BusinessUnit(sector, sector, sector);
	}

	public Integer getLevel() {
		return level;
	}

	public BusinessUnit withLevel(Integer level) {
		return new BusinessUnit(sector, sector, sector);
	}
	
}
