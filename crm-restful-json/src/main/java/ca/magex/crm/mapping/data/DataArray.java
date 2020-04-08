package ca.magex.crm.mapping.data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class DataArray extends DataElement {
	
	private final List<DataElement> elements;
	
	public DataArray(List<DataElement> elements) {
		super(digest(elements.stream().map(e -> e.mid()).collect(Collectors.joining(","))));
		this.elements = Collections.unmodifiableList(elements);
	}
	
	public DataArray with(Object value) {
		List<DataElement> values = new ArrayList<DataElement>(elements);
		values.add(cast(value));
		return new DataArray(values);
	}
	
	public Stream<DataElement> stream() {
		return elements.stream();
	}
	
	public List<DataElement> values() {
		return elements;
	}

}
