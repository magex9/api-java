package ca.magex.json.util;

import ca.magex.json.model.JsonObject;

public interface Transformer<T extends Object> {
	
	public Class<T> getType();
	
	public JsonObject format(T obj);
	
	public T parse(JsonObject data);
	
}
