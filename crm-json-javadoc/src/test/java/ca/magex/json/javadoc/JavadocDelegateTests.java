package ca.magex.json.javadoc;

import static org.junit.Assert.assertTrue;

import java.io.File;

import org.junit.Test;

import ca.magex.json.javadoc.samples.CrudService;

public class JavadocDelegateTests {
	
	@Test
	public void testCrudServiceLogger() throws Exception {
		File inputFile = new File("src/test/java/", CrudService.class.getName().replaceAll("\\.", "/") + ".java");
		String inputPackage = CrudService.class.getPackageName();
		assertTrue(inputFile.exists());
		File outputFile = new File("src/generated/java/", CrudService.class.getName().replaceAll("\\.", "/") + "LoggerDelegate.java");
		String outputPackage = CrudService.class.getPackageName();
		LoggerDelegationBuilder.build(inputFile, inputPackage, outputFile, outputPackage, "CrudServiceLoggerDelegate");
	}
	
}
