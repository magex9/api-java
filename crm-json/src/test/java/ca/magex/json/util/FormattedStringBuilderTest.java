package ca.magex.json.util;

import static org.junit.Assert.*;

import org.junit.Test;

public class FormattedStringBuilderTest {

	@Test
	public void testBuildEmptyString() throws Exception {
		assertEquals("", new FormattedStringBuilder().toString());
	}
	
	@Test
	public void testAppending() throws Exception {
		FormattedStringBuilder sb = new FormattedStringBuilder();
		sb.append("test");
		assertEquals("test\n", sb.toString());
	}
	
	@Test
	public void testMultipleIndent() throws Exception {
		FormattedStringBuilder sb = new FormattedStringBuilder();
		sb.indent("{");
		sb.indent("data: {");
		sb.append("name: 'test'");
		sb.unindent("}");
		sb.unindent("}");
		assertEquals("{\n\tdata: {\n\t\tname: 'test'\n\t}\n}\n", sb.toString());
	}
	
}
