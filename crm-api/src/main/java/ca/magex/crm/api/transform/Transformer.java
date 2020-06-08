package ca.magex.crm.api.transform;

import java.util.Locale;

public interface Transformer<S, T> {
	
	public Class<S> getSourceType();
	
	public Class<T> getTargetType();
	
	public T format(S source, Locale locale);
	
	public S parse(T target, Locale locale);
	
}
