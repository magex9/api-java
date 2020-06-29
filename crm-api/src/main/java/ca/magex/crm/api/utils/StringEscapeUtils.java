package ca.magex.crm.api.utils;

import java.util.regex.Pattern;

/**
 * Extension to Apache String Escape Utils
 * 
 * @author Jonny
 */
public class StringEscapeUtils extends org.apache.commons.text.StringEscapeUtils {

	private static Pattern SPECIAL_REGEX_CHARS = Pattern.compile("[{}()\\[\\].+*?^$\\\\|]");
	
	/**
	 * Returns a String with all regex characters escaped
	 * @param input
	 * @return
	 */
	public static String escapeRegex(final String input) {
		return SPECIAL_REGEX_CHARS.matcher(input).replaceAll("\\\\$0");
	}
}