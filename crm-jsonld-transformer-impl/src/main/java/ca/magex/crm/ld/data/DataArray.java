package ca.magex.crm.ld.data;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import ca.magex.crm.ld.LinkedDataFormatter;

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
	
	public void stream(OutputStream os, LinkedDataFormatter formatter) throws IOException {
		os.write("[".getBytes());
		if (elements.size() == 1) {
			elements.get(0).stream(os, null);
		} else if (elements.size() > 1) {
			if (formatter.isIndented())
				os.write(LinkedDataFormatter.EOL);
			formatter.indent();
			for (int i = 0; i < elements.size(); i++) {
				if (formatter.isIndented())
					os.write(formatter.prefix());
				elements.get(i).stream(os, formatter);
				if (i < elements.size() - 1)
					os.write(",".getBytes());
				if (formatter.isIndented())
					os.write(LinkedDataFormatter.EOL);
			}
			formatter.unindent();
			if (formatter.isIndented())
				os.write(formatter.prefix());
		}
		os.write("]".getBytes());
	}

}
