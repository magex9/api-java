package ca.magex.json.model;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

public class JsonFormatter {
	
	public static final byte[] INDENT = "  ".getBytes();
	
	public static final byte[] EOL = "\n".getBytes();
	
	public static final byte[] BLANK = new byte[] { };

	private static final Map<Integer, byte[]> prefixes = new HashMap<Integer, byte[]>();
	
	private Integer indentation;
	
	private Stack<String> contexts;
	
	public JsonFormatter(boolean indented) {
		this(indented ? 0 : null);
	}
	
	private JsonFormatter(Integer indentation) {
		this.indentation = indentation;
		this.contexts = new Stack<String>();
	}
	
	public static final String compact(JsonElement data) {
		return new JsonFormatter(false).stringify(data);
	}
	
	public static final String formatted(JsonElement data) {
		return new JsonFormatter(true).stringify(data);
	}
	
	public Integer getIndentation() {
		return indentation;
	}
	
	public boolean isIndented() {
		return indentation != null;
	}
	
	public JsonFormatter indent() {
		if (indentation != null)
			indentation += 1;
		return this;
	}

	public JsonFormatter unindent() {
		if (indentation != null)
			indentation -= 1;
		return this;
	}

	public final byte[] prefix() {
		if (indentation == null)
			return BLANK;
		if (!prefixes.containsKey(indentation))
			prefixes.put(indentation, new String(new char[indentation]).replaceAll("\0", new String(INDENT)).getBytes());
		return prefixes.get(indentation);
	}
	
	public JsonFormatter setIndentation(Integer indentation) {
		if (this.indentation != null)
			this.indentation = indentation;
		return this;
	}
	
	public Stack<String> getContexts() {
		return contexts;
	}
	
	public final String stringify(JsonElement data) {
		return ((ByteArrayOutputStream)stream(data, new ByteArrayOutputStream())).toString(StandardCharsets.UTF_8);
	}
	
	public OutputStream stream(JsonElement data, OutputStream os) {
		if (os == null)
			throw new IllegalArgumentException("Unable to write to null OutputStream");
		if (data.getClass().equals(JsonElement.class)) { 
			try {
				os.write("null".getBytes());
			} catch (IOException e) {
				throw new IllegalArgumentException("Unable to write to output stream: " + os.getClass(), e);
			}
		} else {
			try {
				getClass().getMethod("stream", new Class[] { data.getClass(), OutputStream.class })
					.invoke(this, new Object[] { data, os });
			} catch (Exception e) {
				throw new IllegalArgumentException("Unable to write to output stream: " + os.getClass(), e);
			}
		}
		return os;
	}
	
	public void stream(JsonText data, OutputStream os) throws IOException {
		os.write((data.value() == null ? "null" : "\"" + data.value().replaceAll("\"", "\\\\\"").replaceAll("'", "\\\\'") + "\"").getBytes());
	}
	
	public void stream(JsonNumber data, OutputStream os) throws IOException {
		os.write((data.value() == null ? "null" : data.value().toString()).getBytes());
	}
	
	public void stream(JsonBoolean data, OutputStream os) throws IOException {
		os.write((data.value() == null ? "null" : data.value() ? "true" : "false").getBytes());
	}
	
	public void stream(JsonPair data, OutputStream os) throws IOException {
		os.write("\"".getBytes());
		os.write(data.key().getBytes());
		os.write("\":".getBytes());
		if (isIndented())
			os.write(" ".getBytes());
		stream(data.value(), os);
	}
	
	public void stream(JsonArray data, OutputStream os) throws IOException {
		os.write("[".getBytes());
		if (data.values().size() == 1) {
			stream(data.values().get(0), os);
		} else if (data.values().size() > 1) {
			if (isIndented())
				os.write(EOL);
			indent();
			for (int i = 0; i < data.values().size(); i++) {
				if (isIndented())
					os.write(prefix());
				stream(data.values().get(i), os);
				if (i < data.values().size() - 1)
					os.write(",".getBytes());
				if (isIndented())
					os.write(EOL);
			}
			unindent();
			if (isIndented())
				os.write(prefix());
		}
		os.write("]".getBytes());
	}
	
	public void stream(JsonObject data, OutputStream os) throws IOException {
		os.write("{".getBytes());
		if (data.pairs().size() == 0) {
			os.write("".getBytes());
		} else if (data.pairs().size() == 1 && !(data.pairs().get(0).value() instanceof JsonObject)) {
			stream(data.pairs().get(0), os);
		} else {
			if (isIndented())
				os.write(EOL);
			indent();
			for (int i = 0; i < data.pairs().size(); i++) {
				if (getContexts().isEmpty() || !data.pairs().get(i).key().equals("@context") || !((JsonText)data.pairs().get(i).value()).value().equals(getContexts().peek())) {
					if (isIndented())
						os.write(prefix());
					if (data.contains("@context", JsonText.class))
						getContexts().push(data.getString("@context"));
					stream(data.pairs().get(i), os);
					if (data.contains("@context", JsonText.class))
						getContexts().pop();
					if (i < data.pairs().size() - 1)
						os.write(",".getBytes());
					if (isIndented())
						os.write(EOL);
				}
			}
			unindent();
			if (isIndented())
				os.write(prefix());
		}
		os.write("}".getBytes());
	}

}
