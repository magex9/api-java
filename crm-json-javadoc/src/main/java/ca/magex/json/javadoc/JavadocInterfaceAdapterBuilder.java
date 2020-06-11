package ca.magex.json.javadoc;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ca.magex.json.model.JsonArray;
import ca.magex.json.model.JsonObject;
import ca.magex.json.model.JsonText;
import ca.magex.json.util.FormattedStringBuilder;

public class JavadocInterfaceAdapterBuilder {

	private static final Logger logger = LoggerFactory.getLogger(JavadocSlf4jDecoratorBuilder.class);

	public static void build(File sourceDir, List<String> interfaces, File adapterFile, String adapterPackage, String adapterClass) throws IOException {
		logger.info("Building adapters for \"" + interfaces.stream().collect(Collectors.joining(", ")) + "\" from " + sourceDir.getAbsolutePath());
		if (!sourceDir.isDirectory())
			throw new FileNotFoundException("Could not find source directory: " + sourceDir.getAbsolutePath());
		JsonObject docs = JavadocBuilder.processDirectory(sourceDir);
		
		FormattedStringBuilder sb = new FormattedStringBuilder();
		sb.append("package " + adapterPackage + ";");
		sb.append("");
		sb.indent("public class " + adapterClass + " implements " + interfaces.stream().collect(Collectors.joining(", ")) + " {");
		sb.append("");
		for (String iface : interfaces) {
			sb.append("private " + iface + " " + variable(iface) + ";");
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

	private static String buildGenerics(JsonObject cls, JsonObject obj) {
		if (!obj.contains("generics", JsonArray.class))
			return "";
		JsonArray generics = obj.getArray("generics");
		if (generics == null || generics.isEmpty())
			return "";
		return "<" + generics.stream().map(e -> {
			String type = ((JsonText)e).value();
			if (cls.getObject("imports").contains(type)) {
				return cls.getObject("imports").getString(type) + "." + type;
			} else {
				return type;
			}
		}).collect(Collectors.joining(", ")) + ">";
	}
	
	public static String buildType(JsonObject cls, JsonObject json) {
		if (!json.contains("type")) {
			return "void";
		} else if (json.contains("type", JsonText.class)) {
			if (cls.getObject("imports").contains(json.getString("type")))
				return cls.getObject("imports").getString(json.getString("type")) + "." + json.getString("type");
			return json.getString("type");
		} else if (json.contains("type", JsonObject.class)) {
			StringBuilder sb = new StringBuilder();
			if (cls.getObject("imports").contains(json.getObject("type").getString("class"))) {
				sb.append(cls.getObject("imports").getString(json.getObject("type").getString("class")) + "." + json.getObject("type").getString("class"));
			} else {
				sb.append(json.getObject("type").getString("class"));
			}
			sb.append(buildGenerics(cls, json.getObject("type")));
			return sb.toString();
		} else {
			return json.get("type").toString();
		}
	}
	
	private static void buildMethod(FormattedStringBuilder sb, JsonObject cls, JsonObject method) {
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
		String returnType = buildType(cls, method);
		sb.append("@Override");
		sb.indent("public " + returnType + " " + method.getString("name") + "(" + methodParams + ")" + throwable + " {");
		if (returnType.equals("void")) {
			sb.append(variable(cls.getString("name")) + "." + method.getString("name") + "(" + invokeParams + ");");
		} else {
			sb.append("return " + variable(cls.getString("name")) + "." + method.getString("name") + "(" + invokeParams + ");");
		}
		sb.unindent("}");
		sb.append("");
	}

}
