package ca.magex.crm.mapping;

import ca.magex.crm.mapping.data.DataObject;

public interface Transformer<T extends Object> {
	
	public Class<?> getType();
	
	public DataObject format(T obj);
	
	public T parse(DataObject data, String parentContext);
	
}
