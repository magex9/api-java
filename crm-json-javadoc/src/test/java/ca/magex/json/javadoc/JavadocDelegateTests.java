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
		File outputFile = new File("src/generated/java/", CrudService.class.getName().replaceAll("\\.", "/") + "Slf4jLogger.java");
		String outputPackage = CrudService.class.getPackageName();
		JavadocSlf4jDecoratorBuilder.build("JUnit Crud Service Logger Test", inputFile, inputPackage, CrudService.class.getName(), outputFile, outputPackage, "CrudServiceSlf4jLogger");
	}
	
	@Test
	public void testCrudServiceDelegate() throws Exception {
		File inputFile = new File("src/test/java/", CrudService.class.getName().replaceAll("\\.", "/") + ".java");
		String inputPackage = CrudService.class.getPackageName();
		assertTrue(inputFile.exists());
		File outputFile = new File("src/generated/java/", CrudService.class.getName().replaceAll("\\.", "/") + "Delegate.java");
		String outputPackage = CrudService.class.getPackageName();
		JavadocDelegationBuilder.build("JUnit Crud Service Delegate Test", inputFile, inputPackage, CrudService.class.getName(), outputFile, outputPackage, "CrudServiceDelegate");
	}
	
}
