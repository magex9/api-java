package ca.magex.crm.ld.data;

import java.io.IOException;
import java.io.OutputStream;

import ca.magex.crm.ld.LinkedDataFormatter;

public final class DataPair extends DataElement {

	private final String key;
	
	private final DataElement value;
	
	public DataPair(String key, DataElement value) {
		super(digest(key + ":" + value.mid()));
		this.key = key;
		this.value = value;
	}
	
	public String key() {
		return key;
	}
	
	public DataElement value() {
		return value;
	}
	
	public void stream(OutputStream os, LinkedDataFormatter formatter) throws IOException {
		os.write("\"".getBytes());
		os.write(key.getBytes());
		os.write("\":".getBytes());
		if (formatter.isIndented())
			os.write(" ".getBytes());
		value.stream(os, formatter);
	}
	
}
