package ca.magex.json.javadoc.samples;

import java.io.Serializable;
import java.util.List;

public interface TypedMethods {

	public <T> List<T> find(T type);
	
	public <K extends Serializable> Long load(String id, Class<K> cls);
	
}
