package ca.magex.json.javadoc;

import static org.junit.Assert.*;

import java.io.File;
import java.nio.charset.StandardCharsets;

import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Test;

import ca.magex.json.javadoc.samples.BasicEntity;
import ca.magex.json.javadoc.samples.CrudService;
import ca.magex.json.javadoc.samples.ExtendedEntity;
import ca.magex.json.javadoc.samples.NestedGenerics;
import ca.magex.json.javadoc.samples.TypedGenerics;
import ca.magex.json.javadoc.samples.TypedMethods;
import ca.magex.json.model.JsonObject;

public class JavadocBuilderTests {
	
	private File basedir;
	
	@Before
	public void setup() {
		basedir = new File("src/test/java/ca/magex/json/javadoc/samples");
	}
	
	@Test
	public void testBasicEntity() throws Exception {
		compare(BasicEntity.class);
	}
	
	@Test
	public void testExtendedEntity() throws Exception {
		compare(ExtendedEntity.class);
	}
	
	@Test
	public void testNestedGenerics() throws Exception {
		compare(NestedGenerics.class);
	}
	
	@Test
	public void testTypedGenerics() throws Exception {
		compare(TypedGenerics.class);
	}
	
	@Test
	public void testTypedMethods() throws Exception {
		compare(TypedMethods.class);
	}
	
	@Test
	public void testCrudService() throws Exception {
		compare(CrudService.class);
	}
	
	private void compare(Class<?> cls) throws Exception {
		File input = new File(basedir, cls.getSimpleName() + ".java");
		assertTrue(input.exists());
		File output = new File("src/generated/resources/javadoc", cls.getSimpleName() + ".json");
		compare(input, output);
	}
	
	private void compare(File input, File output) throws Exception {
		FileUtils.forceMkdir(output.getParentFile());
		JsonObject json = JavadocBuilder.processFile(input);
		FileUtils.writeStringToFile(output, json.toString(), StandardCharsets.UTF_8);
		assertEquals(FileUtils.readFileToString(output, StandardCharsets.UTF_8), json.toString());
	}

}
