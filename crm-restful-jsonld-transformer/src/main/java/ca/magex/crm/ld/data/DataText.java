package ca.magex.crm.ld.data;

import java.io.IOException;
import java.io.OutputStream;

import ca.magex.crm.ld.LinkedDataFormatter;

public final class DataText extends DataElement {

	private final String value;
	
	public DataText(String value) {
		super(digest(value));
		this.value = value;
	}
	
	public String value() {
		return value;
	}
	
	public void stream(OutputStream os, LinkedDataFormatter formatter) throws IOException {
		os.write((value == null ? "null" : "\"" + value.replaceAll("\"", "\\\"") + "\"").getBytes());
	}
	
}
