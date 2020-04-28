package ca.magex.crm.api.system;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class Lang {

	public static final Locale ENGLISH = Locale.CANADA;
	
	public static final Locale FRENCH = Locale.CANADA_FRENCH;
	
	public static final List<Locale> SUPPORTED = Arrays.asList(ENGLISH, FRENCH);
	
	public static boolean isEnglish(Locale locale) {
		if (locale == null)
			throw new IllegalArgumentException("Locale is null");
		if (locale == ENGLISH)
			return true;
		if (locale == FRENCH)
			return false;
		throw new IllegalArgumentException("Locale is not english or french");
	}
	
	public static Locale parse(String lang) {
		if (lang == null)
			return null;
		if (lang.equals("en") || lang.equals("eng") || lang.equals(ENGLISH.toString()))
			return ENGLISH;
		if (lang.equals("fr") || lang.equals("fra") || lang.equals(FRENCH.toString()))
			return FRENCH;
		throw new IllegalArgumentException("Locale is not english or french");
	}
	
}
