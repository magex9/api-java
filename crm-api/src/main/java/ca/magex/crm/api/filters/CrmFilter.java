package ca.magex.crm.api.filters;

import java.io.Serializable;
import java.text.Normalizer;
import java.util.Comparator;

import org.apache.commons.lang3.StringUtils;

public interface CrmFilter<T> extends Serializable {

	public boolean apply(T instance);
	
	default public Comparator<T> getComparator(Paging paging) {
		return paging.new PagingComparator<T>();
	};
	
	default public boolean containsIgnoreCaseAndAccent(final CharSequence str, final CharSequence searchStr) {
		return StringUtils.containsIgnoreCase(normalize(str), normalize(searchStr));
	}
	
	default public String normalize(CharSequence text) {
		return Normalizer.normalize(text, Normalizer.Form.NFD).replaceAll("[^\\p{ASCII}]", "");
	}
	
}
