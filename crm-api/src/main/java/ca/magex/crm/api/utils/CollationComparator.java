package ca.magex.crm.api.utils;

import java.text.Collator;
import java.text.RuleBasedCollator;
import java.util.Comparator;
import java.util.Locale;

import org.apache.commons.lang3.StringUtils;

/**
 * Comparator for Strings that uses a Collator for comparison
 * 
 * @author Jonny
 */
public class CollationComparator implements Comparator<String> {
	
	private Collator collator = null;
	
	public CollationComparator() {
		collator = RuleBasedCollator.getInstance(Locale.CANADA_FRENCH);
		collator.setStrength(Collator.SECONDARY);
		collator.setDecomposition(Collator.NO_DECOMPOSITION);
	}
	
	@Override
	public int compare(String o1, String o2) {
		int result = collator.compare(o1, o2);
		if (result != 0) {
			return result;
		}
		return StringUtils.compare(o1, o2);
	}		
}
