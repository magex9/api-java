package ca.magex.crm.ld.data;

import java.util.ArrayList;
import java.util.List;

import static org.apache.commons.lang3.StringUtils.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DataParser {
	
	private static final Logger logger = LoggerFactory.getLogger(DataParser.class);

	public static DataElement parse(String text) {
		return new DataParser(text).parse();
	}
	
	private int index;
	
	private String text;
	
	private int length;
	
	private DataParser(String text) {
		this.index = 0;
		this.text = text;
		this.length = text.length();
	}
	
	private DataElement parse() {
		while (index < length) {
			char c = getCurrentChar("parse");
			if (isWhitespace(c)) {
				index++;
			} else if (isOpenCurlyBracket(c)) {
				index++;
				return parseObject();
			} else if (isOpenSquareBracket(c)) {
				index++;
				return parseArray();
			} else if (isQuote(c)) {
				index++;
				return new DataText(parseString());
			} else if (isDigit(c)) {
				return new DataNumber(parseNumber());
			} else if (c == 't' && 
					text.charAt(index + 1) == 'r' && 
					text.charAt(index + 2) == 'u' && 
					text.charAt(index + 3) == 'e') {
				index += 4;
				return new DataBoolean(true);
			} else if (c == 'f' && 
					text.charAt(index + 1) == 'a' && 
					text.charAt(index + 2) == 'l' && 
					text.charAt(index + 3) == 's' && 
					text.charAt(index + 4) == 'e') {
				index += 5;
				return new DataBoolean(false);
			} else if (c == 'n' && 
					text.charAt(index + 1) == 'u' && 
					text.charAt(index + 2) == 'l' && 
					text.charAt(index + 3) == 'l') {
				index += 4;
				return new DataElement();
			} else {
				throw new RuntimeException("Unepxected base value at index: " + index + " (" + elipse(index) + ")");
			}
		}
		throw new RuntimeException("Unable to parse the data element: " + elipse(0));
	}
	
	private DataObject parseObject() {
		List<DataPair> pairs = new ArrayList<DataPair>();
		while (index < length) {
			char c = getCurrentChar("parseObject");
			if (isWhitespace(c) || c == ',') {
				index++;
			} else if (isOpenCurlyBracket(c)) {
				index++;
				return parseObject();
			} else if (isCloseCurlyBracket(c)) {
				index++;
				return new DataObject(pairs);
			} else if (isQuote(c)) {
				pairs.add(parsePair());
			} else {
				throw new RuntimeException("Expected a pair value at index: " + index + " (" + elipse(index) + ")");
			}
		}
		throw new RuntimeException("Object not terminated");
	}
	
	private DataArray parseArray() {
		List<DataElement> elements = new ArrayList<DataElement>();
		while (index < length) {
			char c = getCurrentChar("parseArray");
			if (isWhitespace(c) || c == ',') {
				index++;
			} else if (isCloseSquareBracket(c)) {
				index++;
				return new DataArray(elements);
			} else if (isQuote(c)) {
				index++;
				elements.add(new DataText(parseString()));
			} else if (isDigit(c)) {
				elements.add(new DataNumber(parseNumber()));
			} else if (c == 't' && 
					text.charAt(index + 1) == 'r' && 
					text.charAt(index + 2) == 'u' && 
					text.charAt(index + 3) == 'e') {
				index += 4;
				elements.add(new DataBoolean(true));
			} else if (c == 'f' && 
					text.charAt(index + 1) == 'a' && 
					text.charAt(index + 2) == 'l' && 
					text.charAt(index + 3) == 's' && 
					text.charAt(index + 4) == 'e') {
				index += 5;
				elements.add(new DataBoolean(false));
			} else if (c == 'n' && 
					text.charAt(index + 1) == 'u' && 
					text.charAt(index + 2) == 'l' && 
					text.charAt(index + 3) == 'l') {
				index += 4;
				elements.add(new DataElement());
			} else {
				throw new RuntimeException("Expected an element value at index: " + index + " (" + elipse(index) + ")");
			}
		}
		throw new RuntimeException("Object not terminated");
	}
	
	private DataPair parsePair() {
		String key = null;
		while (index < length) {
			char c = getCurrentChar("parsePair");
			if (isWhitespace(c)) {
				index++;
			} else if (isQuote(c)) {
				index++;
				key = parseKey();
				break;
			} else {
				throw new RuntimeException("Expected a key at index: " + index + " (" + elipse(index) + ")");
			}
		}
		while (index < length) {
			char c = getCurrentChar("parsePair");
			if (isWhitespace(c)) {
				index++;
			} else if (c == ':') {
				index++;
				break;
			} else {
				throw new RuntimeException("Expected a key at index: " + index + " (" + elipse(index) + ")");
			}
		}
		DataElement value = parse();
		return new DataPair(key, value);
	}

	private String parseKey() {
		StringBuilder sb = new StringBuilder();
		while (index < length) {
			char c = getCurrentChar("parseKey");
			if (isAlphaNumeric(c) || c == '_' || c == '@' ) {
				sb.append(c);
				index++;
			} else if (isQuote(c)) {
				index++;
				return sb.toString();
			} else {
				throw new RuntimeException("Expected a text at index: " + index + " (" + elipse(index) + ")");
			}
		}
		throw new RuntimeException("String not terminated at: " + index + " (" + elipse(index) + ")");
	}

	private String parseString() {
		StringBuilder sb = new StringBuilder();
		while (index < length) {
			char c = getCurrentChar("parseString");
			if (isQuote(c)) {
				index++;
				return sb.toString();
			} else if (c == '\\' && isExcapeable(text.charAt(index + 1))) {
				sb.append(c);
				sb.append(text.charAt(index + 1));
				index += 1;
			} else if (isExtendedCharacter(c)) {
				sb.append(c);
				index++;
			} else {
				throw new RuntimeException("Expected a text at index: " + index + " (" + elipse(index) + ")");
			}
		}
		throw new RuntimeException("String not terminated at: " + index + " (" + elipse(index) + ")");
	}

	private Number parseNumber() {
		StringBuilder sb = new StringBuilder();
		boolean decimal = false;
		while (index < length) {
			char c = getCurrentChar("parseNumber");
			if (isDigit(c)) {
				sb.append(c);
				index++;
			} else if (c == '.' && !decimal) {
				sb.append(c);
				index++;
				decimal = true;
			} else {
				if (decimal) {
					return Float.valueOf(sb.toString());
				} else {
					return Integer.valueOf(sb.toString());
				}
			}
		}
		if (decimal) {
			return Float.valueOf(sb.toString());
		} else {
			return Integer.valueOf(sb.toString());
		}
	}

	private boolean isQuote(char c) {
		return c == '"' || c == '\'';
	}
	
	private boolean isOpenCurlyBracket(char c) {
		return c == '{';
	}
	
	private boolean isCloseCurlyBracket(char c) {
		return c == '}';
	}
	
	private boolean isOpenSquareBracket(char c) {
		return c == '[';
	}
	
	private boolean isCloseSquareBracket(char c) {
		return c == ']';
	}
	
	private boolean isDigit(char c) {
		return (int)'0' <= c && c <= (int)'9';
	}
	
	private boolean isLowercaseCharacter(char c) {
		return (int)'a' <= c && c <= (int)'z';
	}
	
	private boolean isUppercaseCharacter(char c) {
		return (int)'A' <= c && c <= (int)'Z';
	}
	
	public boolean isAlphaNumeric(char c) {
		return isDigit(c) || isLowercaseCharacter(c) || isUppercaseCharacter(c);
	}
	
	private boolean isExtendedCharacter(char c) {
		return 32 <= (int)c && (int)c <= 255;
	}
	
	private boolean isWhitespace(char c) {
		return isSpace(c) || isNewline(c) || isTab(c);
	}
	
	private boolean isSpace(char c) {
		return c == ' ';
	}
	
	private boolean isTab(char c) {
		return c == '\t';
	}
	
	private boolean isNewline(char c) {
		return c == '\n' || c == '\r';
	}
	
	private boolean isExcapeable(char c) {
		return c == '"' || c == '\\' || c == '/' || c == 'b' || c == 'f' || c == 'n' || c == 'r' || c == 't';
	}
	
	private char getCurrentChar(String function) {
		char c = text.charAt(index);
		if (logger.isDebugEnabled()) {
			int n = (int)c;
			String prefix = leftPad(Integer.toString(index), 4, "") + " " + leftPad(function, 12, " ") + ": ";
			if (n == 9) {
				logger.debug(prefix + "'\\t' " + ((int)c));
			} else if (n == 10) {
				logger.debug(prefix + "'\\n' " + ((int)c));
			} else {
				logger.debug(prefix + "'" + c + "'  " + ((int)c));
			}
		}
		return c;
	}
	
	private String elipse(int index) {
		int length = 30;
		if (index > text.length())
			throw new IndexOutOfBoundsException("Index is larger then text: " + index + " > " + text.length());
		int end = index + length;
		if (end > text.length())
			return text.substring(index, text.length());
		return text.substring(index, index + length) + "...";
	}
	
}
