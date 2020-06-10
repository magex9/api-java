package ca.magex.json.javadoc.samples;

import java.util.List;

public interface TypedMethods {

	public <T> List<T> find(T type);
	
	public <K> Long load(String id, Class<K> cls);
	
}
