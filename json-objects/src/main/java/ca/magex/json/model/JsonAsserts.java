package ca.magex.json.model;

import ca.magex.json.util.FormattedStringBuilder;

public class JsonAsserts {

	public static void print(JsonObject json, String name) {
		print(json, name, false);
	}
	
	public static void print(JsonObject json, String name, boolean escaped) {
		System.out.println("====================================================");
		System.out.println(json);
		System.out.println("====================================================");
		if (escaped) {
			System.out.println("\t\t//JsonAsserts.print(" + name + ", \"" + name + "\", true);");
			System.out.println("\t\t" + buildAsserts(json, name, new FormattedStringBuilder(), ".replaceAll(\"\\n\", \"\\\\\\\\n\").replaceAll(\"\\r\", \"\\\\\\\\r\")"));
		} else {
			System.out.println("\t\t//JsonAsserts.print(" + name + ", \"" + name + "\");");
			System.out.println("\t\t" + buildAsserts(json, name, new FormattedStringBuilder(), ""));
		}
		System.out.println("====================================================");
	}

	private static String buildAsserts(JsonObject json, String prefix, FormattedStringBuilder sb, String escape) {
		StringBuilder keys = new StringBuilder();
		for (String key : json.keys()) {
			keys.append(", \"" + key + "\"");
		}
		sb.append("\t\tassertEquals(List.of(" + (json.keys().isEmpty() ? "" : keys.substring(2)) + "), " + prefix + ".keys());");
		for (String key : json.keys()) {
			JsonElement el = json.get(key);
			if (el instanceof JsonObject) {
				buildAsserts((JsonObject)el, prefix + ".getObject(\"" + key + "\")", sb, escape);
			} else if (el instanceof JsonArray) {
				JsonArray array = (JsonArray)el;
				sb.append("\t\tassertEquals(" + array.size() + ", " + prefix + ".getArray(\"" + key + "\").size());");
				for (int i = 0; i < array.size(); i++) {
					JsonElement child = array.get(i);
					if (child instanceof JsonObject) {
						buildAsserts((JsonObject)child, prefix + ".getArray(\"" + key + "\").getObject(" + i + ")", sb, escape);
					} else if (child instanceof JsonNumber) {
						sb.append("\t\tassertEquals(" + array.getNumber(i) + ", " + prefix + ".getArray(\"" + key + "\").getNumber(" + i + "));");
					} else if (child instanceof JsonBoolean) {
						sb.append("\t\tassertEquals(" + array.getBoolean(i) + ", " + prefix + ".getArray(\"" + key + "\").getBoolean(" + i + "));");
					} else if (child instanceof JsonText) {
						if (key.endsWith("Id")) {
							sb.append("\t\tassertEquals(" + key + ".getId(), " + prefix + ".getArray(\"" + key + "\").getString(" + i + "));");
						} else {
							sb.append("\t\tassertEquals(\"" + array.getString(i).replaceAll("\n", "\\\\n").replaceAll("\r", "\\\\r") + "\", " + prefix + ".getArray(\"" + key + "\").getString(" + i + ")" + escape + ");");
						}
					} else {
						sb.append("\t\tassertEquals(" + array.get(i) + ", " + prefix + ".getArray(\"" + key + "\").get(" + i + "));");
					}
				}
			} else if (el instanceof JsonNumber) {
				sb.append("\t\tassertEquals(" + json.getNumber(key) + ", " + prefix + ".getNumber(\"" + key + "\"));");
			} else if (el instanceof JsonBoolean) {
				sb.append("\t\tassertEquals(" + json.getBoolean(key) + ", " + prefix + ".getBoolean(\"" + key + "\"));");
			} else if (el instanceof JsonText) {
				if (key.endsWith("Id")) {
					sb.append("\t\tassertEquals(" + key + ".getId(), " + prefix + ".getString(\"" + key + "\"));");
				} else {
					sb.append("\t\tassertEquals(\"" + json.getString(key).replaceAll("\n", "\\\\n").replaceAll("\r", "\\\\r") + "\", " + prefix + ".getString(\"" + key + "\")" + escape + ");");
				}
			} else {
				sb.append("\t\tassertEquals(" + json.get(key) + ", " + prefix + ".get(\"" + key + "\"));");
			}
		}
		return sb.toString().trim();
	}

	public static void print(JsonArray json, String name) {
		System.out.println("====================================================");
		System.out.println(json);
		System.out.println("====================================================");
		System.out.println("\t\t//JsonAsserts.print(" + name + ", \"" + name + "\");");
		System.out.println("\t\t" + buildAsserts(json, name, new FormattedStringBuilder()));
		System.out.println("====================================================");
	}

	private static String buildAsserts(JsonArray json, String prefix, FormattedStringBuilder sb) {
		return buildAsserts(json, prefix, sb, "");
	}
	
	private static String buildAsserts(JsonArray json, String prefix, FormattedStringBuilder sb, String escape) {
		sb.append("\t\tassertEquals(" + json.size() + ", " + prefix + ".size());");
		for (int i = 0; i < json.size(); i++) {
			JsonElement child = json.get(i);
			if (child instanceof JsonObject) {
				buildAsserts((JsonObject)child, prefix + ".getObject(" + i + ")", sb, escape);
			} else if (child instanceof JsonNumber) {
				sb.append("\t\tassertEquals(" + json.getNumber(i) + ", " + prefix + ".getNumber(" + i + "));");
			} else if (child instanceof JsonBoolean) {
				sb.append("\t\tassertEquals(" + json.getBoolean(i) + ", " + prefix + ".getBoolean(" + i + "));");
			} else if (child instanceof JsonText) {
				sb.append("\t\tassertEquals(\"" + json.getString(i).replaceAll("\n", "\\\\n").replaceAll("\r", "\\\\r") + "\", " + prefix + ".getString(" + i + ").replaceAll(\"\\n\", \"\\\\\\\\n\").replaceAll(\"\\r\", \"\\\\\\\\r\"));");
			} else {
				sb.append("\t\tassertEquals(" + json.get(i) + ", " + prefix + ".get(" + i + "));");
			}
		}
		return sb.toString().trim();
	}
	
}
