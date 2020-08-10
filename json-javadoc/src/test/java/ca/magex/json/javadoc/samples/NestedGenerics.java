package ca.magex.json.javadoc.samples;

import java.util.List;
import java.util.Locale;
import java.util.Map;

public interface NestedGenerics {

	public Map<Locale, List<? extends BasicEntity>> find();
	
}
