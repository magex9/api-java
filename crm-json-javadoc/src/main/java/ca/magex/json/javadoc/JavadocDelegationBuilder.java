package ca.magex.json.javadoc;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ca.magex.json.model.JsonArray;
import ca.magex.json.model.JsonObject;
import ca.magex.json.model.JsonPair;
import ca.magex.json.model.JsonText;
import ca.magex.json.util.FormattedStringBuilder;

public class JavadocDelegationBuilder {

	private static final Logger logger = LoggerFactory.getLogger(JavadocSlf4jDecoratorBuilder.class);
	
	public static void build(File inputFile, String inputPackage, String inputClass, File outputFile, String outputPackage, String outputClass) throws IOException {
		logger.info("Building logger from " + inputFile.getAbsolutePath());
		if (!inputFile.isFile())
			throw new FileNotFoundException("Could not find input interface: " + inputFile.getAbsolutePath());
		JsonObject cls = JavadocBuilder.processFile(inputFile);
		FormattedStringBuilder sb = new FormattedStringBuilder();
		sb.append("package " + outputPackage + ";");
		sb.append("");
		sb.append("import " + inputPackage + "." + cls.getString("name") + ";");
		sb.append("");
		for (JsonPair pair : cls.getObject("imports").pairs()) {
			sb.append("import " + pair.text() + "." + pair.key() + ";");
		}
		sb.append("");
		sb.indent("public class " + outputClass + buildGenerics(cls) + " implements " + cls.getString("name") + buildGenerics(cls) + " {");
		sb.append("");
		sb.append("private " + cls.getString("name") + buildGenerics(cls) + " delegate;");
		sb.append("");
		sb.indent("public " + outputClass + "(" + cls.getString("name") + buildGenerics(cls) + " delegate) {");
		sb.append("this.delegate = delegate;");
		sb.unindent("}");
		sb.append("");
		for (JsonObject method : cls.getArray("methods").values(JsonObject.class)) {
			buildMethod(sb, cls, method);
		}
		sb.unindent("}");
		
		logger.info("Writing file: " + outputFile.getAbsolutePath());
		FileUtils.writeStringToFile(outputFile, sb.toString(), StandardCharsets.UTF_8);
	}
	
	private static String buildGenerics(JsonObject obj) {
		if (!obj.contains("generics", JsonArray.class))
			return "";
		JsonArray generics = obj.getArray("generics");
		if (generics == null || generics.isEmpty())
			return "";
		return "<" + generics.stream().map(e -> ((JsonText)e).value()).collect(Collectors.joining(", ")) + ">";
	}
	
	public static String buildType(JsonObject json) {
		if (!json.contains("type")) {
			return "void";
		} else if (json.contains("type", JsonText.class)) {
			return json.getString("type");
		} else if (json.contains("type", JsonObject.class)) {
			StringBuilder sb = new StringBuilder();
			sb.append(json.getObject("type").getString("class"));
			sb.append(buildGenerics(json.getObject("type")));
			return sb.toString();
		} else {
			return json.get("type").toString();
		}
	}
	
	private static void buildMethod(FormattedStringBuilder sb, JsonObject cls, JsonObject method) {
		String methodParams = !method.contains("parameters") ? "" :
			method.getArray("parameters", JsonObject.class).stream()
				.map(o -> buildType(o) + " " + o.getString("name"))
				.collect(Collectors.joining(", "));
		String invokeParams = !method.contains("parameters") ? "" :
			method.getArray("parameters", JsonObject.class).stream()
			.map(o -> o.getString("name"))
			.collect(Collectors.joining(", "));
		String throwable = !method.contains("exceptions") ? "" :
			" throws " + method.getArray("exceptions", String.class).stream().collect(Collectors.joining(", "));
		String returnType = buildType(method);
		sb.append("@Override");
		sb.indent("public " + returnType + " " + method.getString("name") + "(" + methodParams + ")" + throwable + " {");
		if (returnType.equals("void")) {
			sb.append("delegate." + method.getString("name") + "(" + invokeParams + ");");
		} else {
			sb.append("return delegate." + method.getString("name") + "(" + invokeParams + ");");
		}
		sb.unindent("}");
		sb.append("");
	}
	
}
