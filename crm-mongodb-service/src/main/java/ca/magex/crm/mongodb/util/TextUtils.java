package ca.magex.crm.mongodb.util;

import java.text.Normalizer;
import java.text.Normalizer.Form;

import org.apache.commons.lang3.StringUtils;

public class TextUtils {

	/**
	 * Returns a Searchable String that can be indexed in Mongo
	 * @param value
	 * @return
	 */
	public static String toSearchable(String value) {
		String normalized = Normalizer
				.normalize(value, Form.NFD)
				.replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
		return StringUtils.toRootLowerCase(normalized);
	}
}
