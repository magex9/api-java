package ca.magex.crm.api.common;

public class Telephone {

	private Integer number;

	private Integer extension;

	public Telephone(Integer number, Integer extension) {
		super();
		this.number = number;
		this.extension = extension;
	}

	public Integer getNumber() {
		return number;
	}

	public Integer getExtension() {
		return extension;
	}

}
