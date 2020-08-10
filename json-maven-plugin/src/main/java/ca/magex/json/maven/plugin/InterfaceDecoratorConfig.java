package ca.magex.json.maven.plugin;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import ca.magex.json.javadoc.JavadocDelegationBuilder;
import ca.magex.json.javadoc.JavadocSlf4jDecoratorBuilder;

public class InterfaceDecoratorConfig {
	
	private String description;
	
	private List<String> interfaces;

	private String targetPackage;
	
	public String getDescription() {
		return description;
	}
	
	public void setDescription(String description) {
		this.description = description;
	}

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
	
	public void build(File sourceDir, File targetDir) {
		for (String iface : interfaces) {
			buildDelegate(description, sourceDir, iface, targetDir, targetPackage);
			buildLogger(description, sourceDir, iface, targetDir, targetPackage);
		}
	}
	
	public static void buildDelegate(String description, File sourceDir, String sourceInterface, File targetDir, String targetPackage) {
		try {
			String sourcePackage = sourceInterface.substring(0, sourceInterface.lastIndexOf('.')); 
			String sourceClass = sourceInterface.substring(sourceInterface.lastIndexOf('.') + 1);
			String targetClass = sourceClass + "Delegate"; 
			File sourceFile = new File(sourceDir, sourcePackage.replaceAll("\\.", "/") + "/" + sourceClass +".java");
			File targetFile = new File(targetDir, targetPackage.replaceAll("\\.", "/") + "/" + targetClass + ".java");
			JavadocDelegationBuilder.build(description, sourceFile, sourcePackage, sourceClass, targetFile, targetPackage, targetClass);
		} catch (IOException e) {
			throw new RuntimeException("Problem building delegate class: " + sourceInterface + " to " + targetPackage, e);
		}
	}
	
	public static void buildLogger(String description, File sourceDir, String sourceInterface, File targetDir, String targetPackage) {
		try {
			String sourcePackage = sourceInterface.substring(0, sourceInterface.lastIndexOf('.')); 
			String sourceClass = sourceInterface.substring(sourceInterface.lastIndexOf('.') + 1);
			String targetClass = sourceClass + "Slf4jDecorator"; 
			File sourceFile = new File(sourceDir, sourcePackage.replaceAll("\\.", "/") + "/" + sourceClass +".java");
			File targetFile = new File(targetDir, targetPackage.replaceAll("\\.", "/") + "/" + targetClass + ".java");
			JavadocSlf4jDecoratorBuilder.build(description, sourceFile, sourcePackage, sourceClass, targetFile, targetPackage, targetClass);
		} catch (IOException e) {
			throw new RuntimeException("Problem building slf4j decorator class: " + sourceInterface + " to " + targetPackage, e);
		}
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this, ToStringStyle.JSON_STYLE);
	}

}
