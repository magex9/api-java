package ca.magex.crm.ld.data;

import java.io.IOException;
import java.io.OutputStream;

public final class DataText extends DataElement {

	private final String value;
	
	public DataText(String value) {
		this.value = value;
	}
	
	public String value() {
		return value;
	}
	
	public void stream(OutputStream os, Integer indentation) throws IOException {
		os.write((value == null ? "null" : "\"" + value.replaceAll("\"", "\\\"") + "\"").getBytes());
	}
	
}
