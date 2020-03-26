package ca.magex.crm.ld.data;

import java.io.IOException;
import java.io.OutputStream;

public final class DataNumber extends DataElement {

	private final Number value;
	
	public DataNumber(Number value) {
		this.value = value;
	}
	
	public Number value() {
		return value;
	}
	
	public void stream(OutputStream os, Integer indentation) throws IOException {
		os.write((value == null ? "null" : value.toString()).getBytes());
	}
	
}
