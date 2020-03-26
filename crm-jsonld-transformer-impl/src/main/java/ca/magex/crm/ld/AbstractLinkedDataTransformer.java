package ca.magex.crm.ld;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import ca.magex.crm.api.system.Identifier;
import ca.magex.crm.ld.data.DataArray;
import ca.magex.crm.ld.data.DataElement;
import ca.magex.crm.ld.data.DataObject;
import ca.magex.crm.ld.data.DataPair;
import ca.magex.crm.ld.data.DataText;

public abstract class AbstractLinkedDataTransformer<T extends Object> implements Transformer<T> {
	
	public static final String PREFIX = "http://magex9.github.io/data/";
	
	public String getSchemaBase() {
		return "http://magex9.github.io/apis";
	}
	
	public String getTopicsReference(Object ref) {
		return PREFIX + ref;
	}
	
	public Identifier getTopicId(DataObject data) {
		String ref = data.getString("@id");
		if (!ref.startsWith(PREFIX))
			throw new IllegalArgumentException("Reference is not a topic: " + ref);
		return new Identifier(ref.substring(PREFIX.length()));
	}
	
	public String datetimeToString(LocalDateTime dateTime) {
		return dateTime.format(DateTimeFormatter.ISO_DATE_TIME);
	}
	
	public LocalDateTime parseDatetime(String dateTime) {
		return LocalDateTime.parse(dateTime, DateTimeFormatter.ISO_DATE_TIME);
	}
	
	public DataObject base() {
		List<DataPair> pairs = new ArrayList<DataPair>();
		pairs.add(new DataPair("@context", new DataText(getSchemaBase())));
		pairs.add(new DataPair("@type", new DataText(getType())));
		return new DataObject(pairs);
	}
	
	public DataObject value(Object obj) {
		return base().with("@value", obj.toString().toLowerCase());
	}
	
	public DataObject format(Identifier identifer) {
		return base().with("@id", getTopicsReference(identifer));
	}
	
	public DataArray format(List<T> objs) {
		List<DataElement> elements = new ArrayList<DataElement>();
		for (T obj : objs) {
			elements.add(format(obj));
		}
		return new DataArray(elements);
	}

	public List<T> parse(DataArray array) {
		List<T> result = new ArrayList<T>();
		for (DataElement el : array.values()) {
			result.add(parse(el));
		}
		return result;
	}
	
	@SuppressWarnings("unchecked")
	public T parse(Object obj) {
		try {
			return (T)this.getClass().getMethod("parse", new Class[] { obj.getClass() }).invoke(this, obj);
		} catch (Exception e) {
			throw new IllegalArgumentException("Unable to find parser for: " + obj.getClass().getCanonicalName());
		}
	}
	
	public void validateContext(DataObject data) {
		if (!data.contains("@context"))
			throw new IllegalArgumentException("dataLD missing @context");
		if (!data.getString("@context").equals(getSchemaBase()))
			throw new IllegalArgumentException("@context does not match, expected " + getSchemaBase() + " but got " + data.getString("@context"));
	}
	
	public void validateType(DataObject data) {
		if (!data.contains("@type"))
			throw new IllegalArgumentException("dataLD missing @type");
		if (!data.getString("@type").equals(getType()))
			throw new IllegalArgumentException("@type does not match, expected " + getType() + " but got " + data.getString("@type"));
	}
	
}
