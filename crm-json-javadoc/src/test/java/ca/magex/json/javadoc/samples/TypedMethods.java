package ca.magex.json.javadoc.samples;

import java.util.List;

public interface TypedMethods {

	public <T> List<T> find(T type);
	
	public <K> K load(String id);
	
}
