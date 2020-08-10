package ca.magex.json.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public interface StringConverter {
	
	public static String firstLetterUpperCase(String text) {
		if (text == null)
			return null;
		if (text.length() == 0)
			return "";
		if (text.length() == 1)
			return Character.toString(text.charAt(0)).toUpperCase();
		return Character.toString(text.charAt(0)).toUpperCase() + text.substring(1);
	}

	public static String firstLetterLowerCase(String text) {
		if (text == null)
			return null;
		if (text.length() == 0)
			return "";
		if (text.length() == 1)
			return Character.toString(text.charAt(0)).toLowerCase();
		return Character.toString(text.charAt(0)).toLowerCase() + text.substring(1);
	}
	
	public static final Pattern UPPER_CASE = Pattern.compile("^[A-Z][A-Z0-9_]*$");
	
	public static Matcher upperCaseMatcher(String text) {
		if (text == null)
			throw new IllegalArgumentException("String is null");
		if (text.length() == 0)
			throw new IllegalArgumentException("String is blank");
		Matcher matcher = UPPER_CASE.matcher(text);
		if (!matcher.matches())
			throw new IllegalArgumentException("String is not upper case: " + text);
		return matcher;
	}
	
	public static String upperToLowerCase(String text) {
		upperCaseMatcher(text);
		StringBuilder sb = new StringBuilder();
		for (String part : text.split("_")) {
			sb.append("-");
			sb.append(part.toLowerCase());
		}
		return sb.substring(1);
	}

	public static String upperToCamelCase(String text) {
		upperCaseMatcher(text);
		StringBuilder sb = new StringBuilder();
		for (String part : text.split("_")) {
			sb.append(firstLetterUpperCase(part.toLowerCase()));
		}
		return firstLetterLowerCase(sb.substring(1));
	}
	
	public static String upperToTitleCase(String text) {
		upperCaseMatcher(text);
		StringBuilder sb = new StringBuilder();
		for (String part : text.split("_")) {
			sb.append(firstLetterUpperCase(part.toLowerCase()));
		}
		return sb.toString();
	}
	
	public static final Pattern LOWER_CASE = Pattern.compile("^[a-z][a-z0-9-]*$");
	
	public static Matcher lowerCaseMatcher(String text) {
		if (text == null)
			throw new IllegalArgumentException("String is null");
		if (text.length() == 0)
			throw new IllegalArgumentException("String is blank");
		Matcher matcher = LOWER_CASE.matcher(text);
		if (!matcher.matches())
			throw new IllegalArgumentException("String is not lower case: " + text);
		return matcher;
	}
	
	public static String lowerToUpperCase(String text) {
		lowerCaseMatcher(text);
		StringBuilder sb = new StringBuilder();
		for (String part : text.split("-")) {
			sb.append("_");
			sb.append(part.toUpperCase());
		}
		return sb.substring(1);
	}
	
	public static String lowerToCamelCase(String text) {
		upperCaseMatcher(text);
		StringBuilder sb = new StringBuilder();
		for (String part : text.split("-")) {
			sb.append(firstLetterUpperCase(part));
		}
		return firstLetterLowerCase(sb.substring(1));
	}
	
	public static String lowerToTitleCase(String text) {
		upperCaseMatcher(text);
		StringBuilder sb = new StringBuilder();
		for (String part : text.split("-")) {
			sb.append(firstLetterUpperCase(part));
		}
		return sb.toString();
	}
	
//	public static final Pattern CAMEL_CASE = Pattern.compile("^[a-z][A-Za-z0-9]*$");
//	
//	public static Matcher camelCaseMatcher(String text) {
//		if (text == null)
//			throw new IllegalArgumentException("String is null");
//		if (text.length() == 0)
//			throw new IllegalArgumentException("String is blank");
//		Matcher matcher = CAMEL_CASE.matcher(text);
//		if (!matcher.matches())
//			throw new IllegalArgumentException("String is not camel case: " + text);
//		return matcher;
//	}
//	
//	public static String camelToUpperCase(String text) {
//		
//	}
//	
//	public static String camelToCamelCase(String text) {
//		
//	}
//	
//	public static String camelToTitleCase(String text) {
//		
//	}
//	
//	public static final Pattern TITLE_CASE = Pattern.compile("^[A-Z][A-Z0-9_]*$");
//	
//	public static Matcher titleCaseMatcher(String text) {
//		if (text == null)
//			throw new IllegalArgumentException("String is null");
//		if (text.length() == 0)
//			throw new IllegalArgumentException("String is blank");
//		Matcher matcher = TITLE_CASE.matcher(text);
//		if (!matcher.matches())
//			throw new IllegalArgumentException("String is not title case: " + text);
//		return matcher;
//	}
//	
//	public static String titleToUpperCase(String text) {
//		
//	}
//	
//	public static String titleToCamelCase(String text) {
//		
//	}
//	
//	public static String titleToTitleCase(String text) {
//		
//	}

}
