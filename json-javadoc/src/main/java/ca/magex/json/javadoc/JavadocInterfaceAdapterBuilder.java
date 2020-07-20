package ca.magex.json.javadoc;

import static ca.magex.json.javadoc.JavadocDelegationBuilder.buildGenerics;
import static ca.magex.json.javadoc.JavadocDelegationBuilder.buildType;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ca.magex.json.model.JsonArray;
import ca.magex.json.model.JsonObject;
import ca.magex.json.util.FormattedStringBuilder;

public class JavadocInterfaceAdapterBuilder {

	private static final Logger logger = LoggerFactory.getLogger(JavadocSlf4jDecoratorBuilder.class);

	public static void build(String description, File sourceDir, List<String> interfaces, List<String> passiveInterfaces, File adapterFile, String adapterPackage, String adapterClass) throws IOException {
		logger.info("Building adapters for \"" + interfaces.stream().collect(Collectors.joining(", ")) + "\" from " + sourceDir.getAbsolutePath());
		if (!sourceDir.isDirectory())
			throw new FileNotFoundException("Could not find source directory: " + sourceDir.getAbsolutePath());
		JsonObject docs = JsondocBuilder.processDirectory(sourceDir); 
		
		FormattedStringBuilder sb = new FormattedStringBuilder();
		sb.append("package " + adapterPackage + ";");
		sb.append("");
		sb.append("/**");
		sb.append(" * AUTO-GENERATED: This file is auto-generated by " + JavadocInterfaceAdapterBuilder.class.getName());
		sb.append(" * ");
		sb.append(" * " + description);
		sb.append(" */");
		sb.indent("public class " + adapterClass + " implements " + Stream.concat(interfaces.stream(), passiveInterfaces.stream()).collect(Collectors.joining(", ")) + " {");
		sb.append("");
		for (String iface : interfaces) {
			sb.append("protected " + iface + " " + variable(iface) + ";");
			sb.append("");
		}
		sb.indent("public " + adapterClass + "(" + interfaces.stream().map(i -> i + " " + variable(i)).collect(Collectors.joining(", ")) + ") {");
		for (String iface : interfaces) {
			sb.append("this." + variable(iface) + " = " + variable(iface) + ";");
		}
		sb.unindent("}");
		sb.append("");
		for (String iface : interfaces) {
			buildMethods(sb, docs, iface);
		}
		sb.unindent("}");
		
		logger.info("Writing file: " + adapterFile.getAbsolutePath());
		FileUtils.writeStringToFile(adapterFile, sb.toString(), StandardCharsets.UTF_8);
	}

	private static String variable(String iface) {
		String name = iface.substring(iface.lastIndexOf('.') + 1);
		return name.substring(0, 1).toLowerCase() + name.substring(1);
	}
	
	private static void buildMethods(FormattedStringBuilder sb, JsonObject docs, String iface) {		
		for (JsonObject method : docs.getObject(iface).getArray("methods", JsonObject.class)) {
			buildMethod(sb, docs.getObject(iface), method);
		}
	}
	
	private static void buildMethod(FormattedStringBuilder sb, JsonObject cls, JsonObject method) {
		if (method.contains("modifiers", JsonArray.class) && method.getArray("modifiers", String.class).contains("static"))
			return;
		String methodParams = !method.contains("parameters") ? "" :
			method.getArray("parameters", JsonObject.class).stream()
				.map(o -> buildType(cls, o) + " " + o.getString("name"))
				.collect(Collectors.joining(", "));
		String invokeParams = !method.contains("parameters") ? "" :
			method.getArray("parameters", JsonObject.class).stream()
			.map(o -> o.getString("name"))
			.collect(Collectors.joining(", "));
		String throwable = !method.contains("exceptions") ? "" :
			" throws " + method.getArray("exceptions", String.class).stream().collect(Collectors.joining(", "));
		String generics = buildGenerics(cls, method);
		String returnType = buildType(cls, method);
		sb.append("@Override");
		sb.indent("public " + (generics == null || generics.length() < 1 ? "" : generics + " ") + returnType + " " + method.getString("name") + "(" + methodParams + ")" + throwable + " {");
		if (returnType.equals("void")) {
			sb.append(variable(cls.getString("name")) + "." + method.getString("name") + "(" + invokeParams + ");");
		} else {
			sb.append("return " + variable(cls.getString("name")) + "." + method.getString("name") + "(" + invokeParams + ");");
		}
		sb.unindent("}");
		sb.append("");
	}

}
