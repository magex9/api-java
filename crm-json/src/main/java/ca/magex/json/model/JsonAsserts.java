package ca.magex.json.model;

import ca.magex.json.util.FormattedStringBuilder;

public class JsonAsserts {

	public static void print(JsonObject json, String name) {
		System.out.println("====================================================");
		System.out.println(json);
		System.out.println("====================================================");
		System.out.println("\t\t//JsonAsserts.print(" + name + ", \"" + name + "\");");
		System.out.println("\t\t" + buildAsserts(json, name, new FormattedStringBuilder()));
		System.out.println("====================================================");
	}

	private static String buildAsserts(JsonObject json, String prefix, FormattedStringBuilder sb) {
		StringBuilder keys = new StringBuilder();
		for (String key : json.keys()) {
			keys.append(", \"" + key + "\"");
		}
		sb.append("\t\tassertEquals(List.of(" + keys.substring(2) + "), " + prefix + ".keys());");
		for (String key : json.keys()) {
			JsonElement el = json.get(key);
			if (el instanceof JsonObject) {
				buildAsserts((JsonObject)el, prefix + ".getObject(\"" + key + "\")", sb);
			} else if (el instanceof JsonArray) {
				JsonArray array = (JsonArray)el;
				sb.append("\t\tassertEquals(" + array.size() + ", " + prefix + ".getArray(\"" + key + "\").size());");
				for (int i = 0; i < array.size(); i++) {
					if (array.get(i) instanceof JsonObject) {
						buildAsserts((JsonObject)array.get(i), prefix + ".getArray(\"" + key + "\").getObject(" + i + ")", sb);
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
					sb.append("\t\tassertEquals(" + key + ".toString(), " + prefix + ".getString(\"" + key + "\"));");
				} else {
					sb.append("\t\tassertEquals(\"" + json.getString(key) + "\", " + prefix + ".getString(\"" + key + "\"));");
				}
			} else {
				sb.append("\t\tassertEquals(" + json.get(key) + ", " + prefix + ".get(\"" + key + "\"));");
			}
		}
		return sb.toString().trim();
	}
	
}
