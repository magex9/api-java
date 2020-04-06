package ca.magex.crm.ld.data;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

import org.apache.commons.codec.digest.DigestUtils;

import ca.magex.crm.ld.LinkedDataFormatter;

public class DataElement {
	
	private final String mid;

	public DataElement() {
		this.mid = digest(stringify(new LinkedDataFormatter().setIndentation(0).setTyped(true)));
	}
	
	public DataElement(String mid) {
		this.mid = mid;
	}
	
	public final String mid() {
		return mid;
	}
	
	public static DataElement cast(Object el) {
		if (el == null) {
			return new DataElement();
		} else if (el instanceof DataElement) {
			return (DataElement)el;
		} else if (el instanceof String) {
			return new DataText((String)el);
		} else if (el instanceof Number) {
			return new DataNumber((Number)el);
		}
		throw new IllegalArgumentException("Unsupported type of element to convert to a data element: " + el.getClass());
	}

	public static final String digest(Object obj) {
		if (obj == null || (obj instanceof String && ((String)obj).equals("null")))
			return "";
		return DigestUtils.md5Hex(obj.toString());
	}
	
	public final String compact() {
		return stringify(LinkedDataFormatter.compact());
	}
	
	public final String formatted() {
		return stringify(LinkedDataFormatter.full());
	}
	
	public final String stringify(LinkedDataFormatter formatter) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try {
			stream(baos, formatter);
		} catch (IOException e) {
			throw new RuntimeException("Problem building string", e);
		}
		return baos.toString(StandardCharsets.UTF_8);
	}
	
	public void stream(OutputStream os, LinkedDataFormatter formatter) throws IOException {
		os.write("null".getBytes());
	}
	
	@Override
	public final String toString() {
		return stringify(new LinkedDataFormatter().setIndentation(null).setTyped(true));
	}

}
