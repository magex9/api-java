package ca.magex.crm.ld.data;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.HashMap;
import java.util.Map;

public class DataElement {
	
	public static final byte[] INDENT = "  ".getBytes();
	
	public static final byte[] EOL = "\n".getBytes();
	
	public static final byte[] BLANK = new byte[] { };
	
	public static final Map<Integer, byte[]> prefixes = new HashMap<Integer, byte[]>();
	
	private final String mid;

	public DataElement() {
		this.mid = digest(stringify(0));
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
		} else if (el instanceof String) {
			return new DataText((String)el);
		}
		throw new IllegalArgumentException("Unsupported type of element to convert to a data element: " + el.getClass());
	}

	public static final String digest(Object obj) {
		if (obj == null || (obj instanceof String && ((String)obj).equals("null")))
			return "";
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			md.update(obj.toString().getBytes());
			byte[] digest = md.digest();
			return new String(digest);
		} catch (Exception e) {
			throw new RuntimeException("Unable to digest md5 text", e);
		}
	}
	
	public final byte[] prefix(Integer indentation) {
		if (indentation == null)
			return BLANK;
		if (!prefixes.containsKey(indentation))
			prefixes.put(indentation, new String(new char[indentation]).replaceAll("\0", new String(INDENT)).getBytes());
		return prefixes.get(indentation);
	}
	
	public final String compact() {
		return stringify(null);
	}
	
	public final String formatted() {
		return stringify(0);
	}
	
	protected final String stringify(Integer indentation) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try {
			stream(baos, indentation);
		} catch (IOException e) {
			throw new RuntimeException("Problem building string", e);
		}
		return baos.toString(StandardCharsets.UTF_8);
	}
	
	public void stream(OutputStream os, Integer indentation) throws IOException {
		os.write("null".getBytes());
	}
	
	@Override
	public final String toString() {
		return stringify(null);
	}

}
