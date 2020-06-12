package ca.magex.crm.maven.plugin;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import ca.magex.json.javadoc.JavadocInterfaceAdapterBuilder;

public class InterfaceAdapterConfig {

	private String description;
	
	private List<String> interfaces;

	private String targetClass;
	
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

	public String getTargetClass() {
		return targetClass;
	}

	public void setTargetClass(String targetClass) {
		this.targetClass = targetClass;
	}
	
	public void build(File sourceDir, File targetDir) {
		try {
			String adapterPackage = targetClass.substring(0, targetClass.lastIndexOf('.')); 
			String adapterClass = targetClass.substring(targetClass.lastIndexOf('.') + 1);
			File adapterFile = new File(targetDir, targetClass.replaceAll("\\.", "/") + ".java");
			JavadocInterfaceAdapterBuilder.build(description, sourceDir, interfaces, adapterFile, adapterPackage, adapterClass);
		} catch (IOException e) {
			throw new RuntimeException("Problem building adapter class: " + interfaces + " to " + targetClass, e);
		}
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this, ToStringStyle.JSON_STYLE);
	}

}
