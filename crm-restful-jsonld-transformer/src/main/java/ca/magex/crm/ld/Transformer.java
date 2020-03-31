package ca.magex.crm.ld;

import ca.magex.crm.ld.data.DataObject;

public interface Transformer<T extends Object> {
	
	public Class<?> getType();
	
	public DataObject format(T obj);
	
	public T parse(DataObject data, String parentContext);
	
}