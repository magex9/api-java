package ca.magex.crm.mapping.json;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import ca.magex.crm.mapping.data.DataArray;
import ca.magex.crm.mapping.data.DataElement;
import ca.magex.crm.mapping.data.DataFormatter;
import ca.magex.crm.mapping.data.DataParser;
import ca.magex.crm.mapping.data.DataText;

public class DataArrayTest {

	@Test
	public void testEmptyArray() throws Exception {
		DataArray array = new DataArray();
		assertEquals("[]", array.toString());
		assertEquals(0, array.size());
	}
	
	@Test
	public void testStringConstructor() throws Exception {
		DataArray array = new DataArray("[1,true,\"test\"]");
		assertEquals("[1,true,\"test\"]", DataFormatter.compact(array));
		assertEquals("[\n" + 
				"  1,\n" + 
				"  true,\n" + 
				"  \"test\"\n" + 
				"]", array.toString());
		assertEquals(3, array.size());
	}
	
	@Test
	public void testSingleQuote() throws Exception {
		List<DataElement> elements = new ArrayList<DataElement>();
		elements.add(new DataText("a'b"));
		DataArray array = new DataArray(elements);
		assertEquals("[\"a\\'b\"]", DataFormatter.compact(array));
		String compact = DataFormatter.compact(array);
		assertEquals(compact, DataFormatter.compact(DataParser.parseArray(compact)));
		assertEquals(compact, DataFormatter.formatted(DataParser.parseArray(compact)));
	}
	
	@Test
	public void testQuoteInVariable() throws Exception {
		List<DataElement> elements = new ArrayList<DataElement>();
		elements.add(new DataText("Quote's"));
		elements.add(new DataText("\"Double\" Quotes"));
		DataArray array = new DataArray(elements);
		assertEquals("[\"Quote\\'s\",\"\\\"Double\\\" Quotes\"]", DataFormatter.compact(array));
		String compact = DataFormatter.compact(array);
		String formatted = DataFormatter.formatted(array);
		
		assertEquals(compact, DataFormatter.compact(DataParser.parseArray(compact)));
		assertEquals(formatted, DataFormatter.formatted(DataParser.parseArray(compact)));
	}
	
}
