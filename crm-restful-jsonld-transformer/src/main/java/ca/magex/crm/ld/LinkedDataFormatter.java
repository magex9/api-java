package ca.magex.crm.ld;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

public class LinkedDataFormatter {

	public static LinkedDataFormatter compact() { return new LinkedDataFormatter(null, false); }
	
	public static LinkedDataFormatter json() { return new LinkedDataFormatter(0, false); }
	
	public static LinkedDataFormatter full() { return new LinkedDataFormatter(0, true); }
	
	private static final byte[] INDENT = "  ".getBytes();
	
	public static final byte[] EOL = "\n".getBytes();
	
	private static final byte[] BLANK = new byte[] { };
	
	private static final Map<Integer, byte[]> prefixes = new HashMap<Integer, byte[]>();
	
	private Integer indentation;
	
	private boolean typed;
	
	private Stack<String> contexts;
	
	public LinkedDataFormatter() {
		this(null, true);
	}
	
	public LinkedDataFormatter(Integer indentation, boolean typed) {
		this.indentation = indentation;
		this.typed = typed;
		this.contexts = new Stack<String>();
	}

	public Integer getIndentation() {
		return indentation;
	}
	
	public boolean isIndented() {
		return indentation != null;
	}
	
	public LinkedDataFormatter indent() {
		if (indentation != null)
			indentation += 1;
		return this;
	}

	public LinkedDataFormatter unindent() {
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
	
	public LinkedDataFormatter setIndentation(Integer indentation) {
		this.indentation = indentation;
		return this;
	}
	
	public boolean isTyped() {
		return typed;
	}
	
	public LinkedDataFormatter setTyped(boolean typed) {
		this.typed = typed;
		return this;
	}
	
	public Stack<String> getContexts() {
		return contexts;
	}
	
}
