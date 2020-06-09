package ca.magex.json.javadoc;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.nio.charset.StandardCharsets;

import org.apache.commons.io.FileUtils;
import org.junit.Test;

import ca.magex.json.model.JsonObject;

public class JavadocBuilderTests {
	
	@Test
	public void testBasicEntity() throws Exception {
		File input = new File("src/test/java/ca/magex/json/javadoc/BasicEntity.java");
		File output = new File("src/test/resources/javadoc/BasicEntity.json");
		FileUtils.forceMkdir(output.getParentFile());
		JsonObject json = JavadocBuilder.processClass(input);
		//FileUtils.writeStringToFile(output, json.toString(), StandardCharsets.UTF_8);
		assertEquals(FileUtils.readFileToString(output, StandardCharsets.UTF_8), json.toString());
	}

}
