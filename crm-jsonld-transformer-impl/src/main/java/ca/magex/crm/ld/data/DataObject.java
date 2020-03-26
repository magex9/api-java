package ca.magex.crm.ld.data;

import java.io.IOException;
import java.io.OutputStream;
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
	
	public DataObject(List<DataPair> pairs) {
		super(digest(pairs.stream().map(e -> e.mid()).collect(Collectors.joining(","))));
		this.pairs = Collections.unmodifiableList(pairs);
		this.map = Collections.unmodifiableMap(pairs.stream().collect(Collectors.toMap(DataPair::key, DataPair::value)));
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
		return pairs.stream().map(p -> p.key()).collect(Collectors.toList());
	}
	
	public List<DataElement> values() {
		return pairs.stream().map(p -> p.value()).collect(Collectors.toList());
	}
	
	public boolean contains(String key) {
		return map.containsKey(key);
	}

	public boolean contains(String key, Class<?> cls) {
		return map.containsKey(key) && map.get(key).getClass().equals(cls);
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
		return ((DataText)map.get(key)).value();
	}
	
	public Integer getInt(String key) {
		return (Integer)((DataNumber)map.get(key)).value();
	}
	
	public Float getFloat(String key) {
		return (Float)((DataNumber)map.get(key)).value();
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
	
	public void stream(OutputStream os, Integer indentation) throws IOException {
		boolean indented = indentation != null;
		byte[] prefix = prefix(indentation);
		os.write("{".getBytes());
		if (pairs.size() == 0) {
			os.write("".getBytes());
		} else if (pairs.size() == 1 && !(pairs.get(0).value() instanceof DataObject)) {
			pairs.get(0).stream(os, null);
		} else {
			if (indented)
				os.write(EOL);
			for (int i = 0; i < pairs.size(); i++) {
				if (indented) {
					os.write(prefix);
					os.write(INDENT);
				}
				pairs.get(i).stream(os, indented ? indentation + 1 : null);
				if (i < pairs.size() - 1)
					os.write(",".getBytes());
				if (indented)
					os.write(EOL);
			}
			if (indented)
				os.write(prefix);
		}
		os.write("}".getBytes());
	}

}
