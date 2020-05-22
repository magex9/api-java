package ca.magex.json.model;

import static org.apache.commons.lang3.StringUtils.leftPad;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JsonParser {
	
	private static final Logger logger = LoggerFactory.getLogger(JsonParser.class);

	public static JsonElement parse(String text) {
		return new JsonParser(text).parse();
	}
	
	public static JsonElement parse(InputStream is) throws IOException {
		return parse(readInputStream(is));
	}
	
	public static JsonElement parse(File file) throws FileNotFoundException, IOException {
		return parse(new FileInputStream(file));
	}
	
	public static String readInputStream(InputStream is) throws IOException {
		if (is == null)
			throw new IOException("Cannot read a null input stream");
		StringBuilder sb = new StringBuilder();
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new InputStreamReader(is));
			String line;
			while ((line = reader.readLine()) != null) {
				sb.append(line);
				sb.append("\n");
			}
		} finally {
			reader.close();
			is.close();
		}
		return StringUtils.chomp(sb.toString());
	}
	
	public static String readFile(File file) throws FileNotFoundException, IOException {
		return readInputStream(new FileInputStream(file));
	}
	
	public static File writeFile(File file, String content) throws IOException {
		BufferedWriter writer = null;
		try {
			writer = new BufferedWriter(new FileWriter(file));
			writer.append(content);
		} finally {
			writer.close();
		}
		return file;
	}
	
	public static JsonObject parseObject(String text) {
		return (JsonObject)parse(text);
	}
	
	public static JsonObject parseObject(InputStream is) throws IOException {
		return (JsonObject)parse(is);
	}
	
	public static JsonObject parseObject(File file) throws FileNotFoundException, IOException {
		return (JsonObject)parse(file);
	}
	
	public static JsonArray parseArray(String text) {
		return (JsonArray)parse(text);
	}
	
	public static JsonArray parseArray(InputStream is) throws IOException {
		return (JsonArray)parse(is);
	}
	
	public static JsonArray parseArray(File file) throws FileNotFoundException, IOException {
		return (JsonArray)parse(file);
	}
	
	private int index;
	
	private String text;
	
	private int length;
	
	private JsonParser(String text) {
		this.index = 0;
		this.text = text;
		this.length = text.length();
	}
	
	private JsonElement parse() {
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
				return new JsonText(parseString());
			} else if (isDigit(c)) {
				return new JsonNumber(parseNumber());
			} else if (c == 't' && 
					text.charAt(index + 1) == 'r' && 
					text.charAt(index + 2) == 'u' && 
					text.charAt(index + 3) == 'e') {
				index += 4;
				return new JsonBoolean(true);
			} else if (c == 'f' && 
					text.charAt(index + 1) == 'a' && 
					text.charAt(index + 2) == 'l' && 
					text.charAt(index + 3) == 's' && 
					text.charAt(index + 4) == 'e') {
				index += 5;
				return new JsonBoolean(false);
			} else if (c == 'n' && 
					text.charAt(index + 1) == 'u' && 
					text.charAt(index + 2) == 'l' && 
					text.charAt(index + 3) == 'l') {
				index += 4;
				return new JsonElement();
			} else {
				throw new RuntimeException("Unepxected base value at index: " + index + " (" + elipse(index) + ")");
			}
		}
		throw new RuntimeException("Unable to parse empty text");
	}
	
	private JsonObject parseObject() {
		List<JsonPair> pairs = new ArrayList<JsonPair>();
		while (index < length) {
			char c = getCurrentChar("parseObject");
			if (isWhitespace(c) || c == ',') {
				index++;
			} else if (isCloseCurlyBracket(c)) {
				index++;
				return new JsonObject(pairs);
			} else if (isQuote(c)) {
				pairs.add(parsePair());
			} else {
				throw new RuntimeException("Expected a pair value at index: " + index + " (" + elipse(index) + ")");
			}
		}
		throw new RuntimeException("Object not terminated");
	}
	
	private JsonArray parseArray() {
		List<JsonElement> elements = new ArrayList<JsonElement>();
		while (index < length) {
			char c = getCurrentChar("parseArray");
			if (isWhitespace(c) || c == ',') {
				index++;
			} else if (isOpenCurlyBracket(c)) {
				index++;
				elements.add(parseObject());
			} else if (isCloseSquareBracket(c)) {
				index++;
				return new JsonArray(elements);
			} else if (isQuote(c)) {
				index++;
				elements.add(new JsonText(parseString()));
			} else if (isDigit(c)) {
				elements.add(new JsonNumber(parseNumber()));
			} else if (c == 't' && 
					text.charAt(index + 1) == 'r' && 
					text.charAt(index + 2) == 'u' && 
					text.charAt(index + 3) == 'e') {
				index += 4;
				elements.add(new JsonBoolean(true));
			} else if (c == 'f' && 
					text.charAt(index + 1) == 'a' && 
					text.charAt(index + 2) == 'l' && 
					text.charAt(index + 3) == 's' && 
					text.charAt(index + 4) == 'e') {
				index += 5;
				elements.add(new JsonBoolean(false));
			} else if (c == 'n' && 
					text.charAt(index + 1) == 'u' && 
					text.charAt(index + 2) == 'l' && 
					text.charAt(index + 3) == 'l') {
				index += 4;
				elements.add(new JsonElement());
			} else {
				throw new RuntimeException("Expected an element value at index: " + index + " (" + elipse(index) + ")");
			}
		}
		throw new RuntimeException("Object not terminated");
	}
	
	private JsonPair parsePair() {
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
		JsonElement value = parse();
		return new JsonPair(key, value);
	}

	private String parseKey() {
		StringBuilder sb = new StringBuilder();
		while (index < length) {
			char c = getCurrentChar("parseKey");
			if (isAlphaNumeric(c) || c == '_' || c == '@' || c == '/' || c == '$' || c == '-' || c == '{' || c == '}') {
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
				sb.append(text.charAt(index + 1));
				index += 2;
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
					try {
						return Float.valueOf(sb.toString());
					} catch (NumberFormatException e) {
						return Double.valueOf(sb.toString());
					}
				} else {
					try {
						return Integer.valueOf(sb.toString());
					} catch (NumberFormatException e) {
						return Long.valueOf(sb.toString());
					}
				}
			}
		}
		if (decimal) {
			try {
				return Float.valueOf(sb.toString());
			} catch (NumberFormatException e) {
				return Double.valueOf(sb.toString());
			}
		} else {
			try {
				return Integer.valueOf(sb.toString());
			} catch (NumberFormatException e) {
				return Long.valueOf(sb.toString());
			}
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
		return c == '"' || c == '\'' || c == '\\' || c == '/' || c == 'b' || c == 'f' || c == 'n' || c == 'r' || c == 't';
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
