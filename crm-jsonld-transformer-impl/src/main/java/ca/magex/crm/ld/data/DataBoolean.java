package ca.magex.crm.ld.data;

import java.io.IOException;
import java.io.OutputStream;

public final class DataBoolean extends DataElement {

	private final Boolean value;
	
	public DataBoolean(Boolean value) {
		this.value = value;
	}
	
	public Boolean value() {
		return value;
	}
	
	public void stream(OutputStream os, Integer indentation) throws IOException {
		os.write((value == null ? "null" : value ? "true" : "false").getBytes());
	}
	
}
