package ca.magex.crm.mapping.data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class DataObject extends DataElement {

	private final List<DataPair> pairs;
	
	private final Map<String, DataElement> map;
	
	private final List<String> keys;
	
	public DataObject() {
		this(new ArrayList<DataPair>());
	}
	
	public DataObject(String text) {
		this(DataParser.parseObject(text).pairs);
	}
	
	public DataObject(List<DataPair> pairs) {
		super(digest(pairs.stream().map(e -> e.mid()).collect(Collectors.joining(","))));
		this.pairs = Collections.unmodifiableList(pairs);
		this.map = Collections.unmodifiableMap(pairs.stream().collect(Collectors.toMap(DataPair::key, DataPair::value)));
		this.keys = pairs.stream().map(p -> p.key()).collect(Collectors.toList());
	}
	
	public DataObject with(String key, Object value) {
		List<DataPair> values = new ArrayList<DataPair>(pairs);
		values.add(new DataPair(key, cast(value)));
		return new DataObject(values);
	}
	
	public Stream<DataPair> stream() {
		return pairs.stream();
	}
	
	public List<DataPair> pairs() {
		return pairs;
	}
	
	public List<String> keys() {
		return keys;
	}
	
	public List<DataElement> values() {
		return pairs.stream().map(p -> p.value()).collect(Collectors.toList());
	}
	
	public boolean contains(String key) {
		return map.get(key) != null && !map.get(key).getClass().equals(DataElement.class);
	}

	public boolean contains(String key, Class<? extends DataElement> cls) {
		return contains(key) && map.get(key).getClass().equals(cls);
	}
	
	public int size() {
		return map.size();
	}
	
	public boolean isEmpty() {
		return map.isEmpty();
	}
	
	public DataElement get(String key) {
		return map.get(key);
	}
	
	public DataObject getObject(String key) {
		return ((DataObject)map.get(key));
	}

	public DataArray getArray(String key) {
		return ((DataArray)map.get(key));
	}

	public String getString(String key) {
		try {
			return ((DataText)map.get(key)).value();
		} catch (Exception e) {
			throw new IllegalArgumentException(e);
		}
	}
	
	public Integer getInt(String key) {
		return ((DataNumber)map.get(key)).value().intValue();
	}
	
	public Long getLong(String key) {
		return ((DataNumber)map.get(key)).value().longValue();
	}
	
	public Float getFloat(String key) {
		return ((DataNumber)map.get(key)).value().floatValue();
	}
	
	public Boolean getBoolean(String key) {
		return ((DataBoolean)map.get(key)).value();
	}
	
	public LocalDate getDate(String key) {
		return LocalDate.parse(((DataText)map.get(key)).value(), DateTimeFormatter.ISO_DATE);
	}
	
	public LocalDateTime getDateTime(String key) {
		return LocalDateTime.parse(((DataText)map.get(key)).value(), DateTimeFormatter.ISO_DATE_TIME);
	}
	
}
