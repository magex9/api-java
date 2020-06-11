package ca.magex.crm.maven.plugin;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import ca.magex.json.javadoc.JavadocDelegationBuilder;
import ca.magex.json.javadoc.JavadocSlf4jDecoratorBuilder;

public class InterfaceDecoratorConfig {
	
	private List<String> interfaces;

	private String targetPackage;

	public List<String> getInterfaces() {
		return interfaces;
	}
	
	public void setInterfaces(List<String> interfaces) {
		this.interfaces = interfaces;
	}
	
	public String getTargetPackage() {
		return targetPackage;
	}
	
	public void setTargetPackage(String targetPackage) {
		this.targetPackage = targetPackage;
	}
	
	public void build(File basedir) {
		for (String iface : interfaces) {
			buildDelegate(basedir, iface, targetPackage);
			buildLogger(basedir, iface, targetPackage);
		}
	}
	
	public static void buildDelegate(File basedir, String sourceInterface, String targetPackage) {
		try {
			File inputDir = new File(basedir, "src/main/java");
			String inputPackage = sourceInterface.substring(0, sourceInterface.lastIndexOf('.')); 
			String inputClass = sourceInterface.substring(sourceInterface.lastIndexOf('.') + 1);
			File outputDir = new File("src/main/generated");
			String outputPackage = targetPackage; 
			String outputClass = inputClass + "Delegate"; 
			File inputFile = new File(inputDir, inputPackage.replaceAll("\\.", "/") + "/" + inputClass +".java");
			File outputFile = new File(outputDir, outputPackage.replaceAll("\\.", "/") + "/" + outputClass + ".java");
			JavadocDelegationBuilder.build(inputFile, inputPackage, inputClass, outputFile, outputPackage, outputClass);
		} catch (IOException e) {
			throw new RuntimeException("Problem building delegate class: " + sourceInterface + " to " + targetPackage, e);
		}
	}
	
	public static void buildLogger(File basedir, String sourceInterface, String targetPackage) {
		try {
			File inputDir = new File(basedir, "src/main/java");
			String inputPackage = sourceInterface.substring(0, sourceInterface.lastIndexOf('.')); 
			String inputClass = sourceInterface.substring(sourceInterface.lastIndexOf('.') + 1);
			File outputDir = new File("src/main/generated");
			String outputPackage = targetPackage; 
			String outputClass = inputClass + "Slf4jDecorator"; 
			File inputFile = new File(inputDir, inputPackage.replaceAll("\\.", "/") + "/" + inputClass +".java");
			File outputFile = new File(outputDir, outputPackage.replaceAll("\\.", "/") + "/" + outputClass + ".java");
			JavadocSlf4jDecoratorBuilder.build(inputFile, inputPackage, inputClass, outputFile, outputPackage, outputClass);
		} catch (IOException e) {
			throw new RuntimeException("Problem building slf4j decorator class: " + sourceInterface + " to " + targetPackage, e);
		}
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this, ToStringStyle.JSON_STYLE);
	}

}
