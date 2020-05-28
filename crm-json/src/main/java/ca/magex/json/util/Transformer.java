package ca.magex.json.util;

import java.util.Locale;

import ca.magex.json.model.JsonElement;

public interface Transformer<T extends Object> {
	
	public Class<T> getType();
	
	public JsonElement format(T obj, Locale locale);
	
	public T parse(JsonElement json, Locale locale);
	
}
