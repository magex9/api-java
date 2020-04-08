package ca.magex.crm.mapping.json;

import static org.junit.Assert.*;

import org.junit.Test;

import ca.magex.crm.mapping.data.DataArray;
import ca.magex.crm.mapping.data.DataFormatter;

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
	
}
