package ca.magex.crm.restful.controllers;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import ca.magex.crm.api.common.Communication;
import ca.magex.crm.api.common.MailingAddress;
import ca.magex.crm.api.common.PersonName;
import ca.magex.crm.api.common.Telephone;
import ca.magex.crm.api.crm.LocationDetails;
import ca.magex.crm.api.crm.LocationSummary;
import ca.magex.crm.api.crm.OrganizationDetails;
import ca.magex.crm.api.crm.OrganizationSummary;
import ca.magex.crm.api.crm.PersonDetails;
import ca.magex.crm.api.crm.PersonSummary;
import ca.magex.crm.api.crm.UserDetails;
import ca.magex.crm.api.crm.UserSummary;
import ca.magex.crm.api.exceptions.ApiException;
import ca.magex.crm.api.exceptions.BadRequestException;
import ca.magex.crm.api.exceptions.ItemNotFoundException;
import ca.magex.crm.api.exceptions.PermissionDeniedException;
import ca.magex.crm.api.services.CrmLocationService;
import ca.magex.crm.api.services.CrmOptionService;
import ca.magex.crm.api.services.CrmOrganizationService;
import ca.magex.crm.api.services.CrmPersonService;
import ca.magex.crm.api.services.CrmUserService;
import ca.magex.crm.api.system.Localized;
import ca.magex.crm.api.system.Message;
import ca.magex.crm.api.system.Status;
import ca.magex.crm.api.system.Type;
import ca.magex.crm.restful.models.RestfulAction;
import ca.magex.json.model.JsonArray;
import ca.magex.json.model.JsonObject;
import ca.magex.json.model.JsonPair;
import ca.magex.json.model.JsonText;

@Controller
public class RestfulSwaggerController extends AbstractRestfulController {
	
	private static final Logger logger = LoggerFactory.getLogger(RestfulSwaggerController.class);

	@Value("${server.external.address:}") 
	private String serverAddress;
	
	@Value("${server.port:9002}") 
	private String serverPort;
	
	@Value("${server.servlet.context-path:/crm}") 
	private String contextPath;
	
	@GetMapping("/rest/api.json")
	public void getJsonConfig(HttpServletRequest req, HttpServletResponse res) throws IOException {		
		try (InputStream is = getClass().getResource("/crm.json").openStream()) {
			String server = StringUtils.isBlank(serverAddress) ?
					contextPath : "http://" + serverAddress + ":" + serverPort + contextPath;
			String contents = StreamUtils.copyToString(is, Charset.forName("UTF-8"))
					.replace("${server}", server);
			res.getWriter().append(contents);
			res.getWriter().flush();
		}
	}
	
	@GetMapping("/rest/oas.json")
	public void getOpenApiConfig(HttpServletRequest req, HttpServletResponse res) throws Exception {		
		res.getWriter().append(buildApiConfig().toString());
		res.getWriter().flush();
	}
	
	@ResponseBody
	@GetMapping("/rest")
	public String getSwaggerRestfulPage(HttpServletRequest req, HttpServletResponse res) throws IOException {
		try (InputStream html = getClass().getResourceAsStream("/crm-restful-swagger.html")) {
			String htmlContents = StreamUtils.copyToString(html, Charset.forName("UTF-8"));
			return htmlContents.replace("${contextPath}", contextPath);
		}
	}
	
	@GetMapping("/rest/actions")
	public void listOrganizationActions(HttpServletRequest req, HttpServletResponse res) throws IOException {
		handle(req, res, RestfulAction.class, (messages, transformer, locale) -> {
			List<RestfulAction> actions = new ArrayList<>();
			actions.add(new RestfulAction("organizations", new Localized("ORGANIZATIONS", "Organizations", "Organizations"), "get", "/rest/organizations"));
			return new JsonObject().with("actions", new JsonArray(actions.stream().map(a -> RestfulActionHandler.transformAction(a, crm, locale)).collect(Collectors.toList())));
		});
	}
	
	public JsonObject buildApiConfig() throws Exception {
		return new JsonObject()
			.with("openapi", "3.0.0")
			.with("info", new JsonObject()
				.with("version", "1.0.0")
				.with("title", "Customer Relationship Management")
			)
			.with("security", new JsonArray()
				.with(new JsonObject()
					.with("bearerAuth", new JsonArray())
				)
			)
			.with("servers", new JsonArray()
				.with(new JsonObject()
					.with("url", "http://" + serverAddress + ":" + serverPort + contextPath)
					.with("description", "Local development (uses test data)")
				)
			)
			.with("paths", buildApiPaths())
			.with(buildApiComponents())
		;
	}
	
	public JsonObject buildApiPaths() throws Exception {
		JsonObject paths = new JsonObject();
		paths = appendPaths(RestfulOrganizationsController.class, CrmOrganizationService.class, "Organizations", paths);
		paths = appendPaths(RestfulLocationsController.class, CrmLocationService.class, "Locations", paths);
		paths = appendPaths(RestfulPersonsController.class, CrmPersonService.class, "Persons", paths);
		paths = appendPaths(RestfulUsersController.class, CrmUserService.class, "Users", paths);
		paths = appendPaths(RestfulOptionsController.class, CrmOptionService.class, "Options", paths);
		return paths;
	}
	
	public JsonObject appendPaths(Class<?> controller, Class<?> api, String tag, JsonObject paths) throws Exception {
		JsonObject jsondoc = readJsondoc(controller);
		JsonObject apidoc = readJsondoc(api);
		JsonArray tags = new JsonArray().with(tag);
		
		for (JsonObject methoddoc : jsondoc.getArray("methods", JsonObject.class)) {
			try {
				JsonObject mapping = findMapping(methoddoc);
				String path = mapping.getString("path");
				String method = mapping.getString("method");
				JsonObject desc = buildPathInfo(methoddoc, apidoc, tags);
				if (!paths.contains(path))
					paths = paths.with(path, new JsonObject());
				paths = paths.with(path, paths.getObject(path).with(method, desc));
			} catch (NoSuchElementException e) {
				logger.info("Skipping method: " + methoddoc.getString("name"));
			}
		}
		
		return paths;
	}
	
	public JsonObject findMapping(JsonObject methoddoc) {
		if (! methoddoc.contains("annotations"))
			throw new NoSuchElementException("No anntations tag found on method: " + methoddoc);
		for (JsonObject annotation : methoddoc.getArray("annotations", JsonObject.class)) {
			String name = annotation.getString("name");
			if (name.equals("GetMapping")) {
				return new JsonObject()
					.with("method", "get")
					.with("path", annotation.getString("value"));
			} else if (name.equals("PostMapping")) {
				return new JsonObject()
					.with("method", "post")
					.with("path", annotation.getString("value"));
			} else if (name.equals("PutMapping")) {
				return new JsonObject()
					.with("method", "put")
					.with("path", annotation.getString("value"));
			} else if (name.equals("DeleteMapping")) {
				return new JsonObject()
					.with("method", "delete")
					.with("path", annotation.getString("value"));
			} else if (name.equals("PatchMapping")) {
				return new JsonObject()
					.with("method", "patch")
					.with("path", annotation.getString("value"));
			}
		}
		throw new NoSuchElementException("Unable to find annotation on method: " + methoddoc);
	}
	
	public JsonObject buildPathInfo(JsonObject methoddoc, JsonObject apidoc, JsonArray tags) {
		Optional<JsonObject> apimethod = apidoc.getArray("methods", JsonObject.class).stream()
			.filter(m -> m.getString("name").equals(methoddoc.getString("name")))
			.findFirst();
		
		if (!apimethod.isPresent())
			throw new NoSuchElementException("Unable to find api method: " + methoddoc.getString("name"));
		
		return new JsonObject()
			.with("summary", apimethod.get().contains("description") ? apimethod.get().getString("description") : "Coming soon...")
			.with("operationId", methoddoc.getString("name"))
			.with("tags", tags);
	}
	
//	  "paths": {
//	    "/rest/organizations": {
//	      "get": {
//	        "summary": "List all organizations",
//	        "operationId": "findOrganizations",
//	        "tags": ["Organizations"],
//	      },
		
	public JsonPair buildApiComponents() throws Exception {
		return new JsonPair("components", new JsonObject()
			.with("securitySchemes", new JsonObject()
				.with("bearerAuth", new JsonObject()
					.with("type", "http")
					.with("scheme", "bearer")
					.with("bearerFormat", "JWT")
				)
			)
			.with(buildApiSchemas())
		);
	}
		
	public JsonPair buildApiSchemas() throws Exception {
		return new JsonPair("schemas", new JsonObject()
			.with(buildApiSchema(OrganizationSummary.class))
			.with(buildApiSchema(OrganizationDetails.class))
			.with(buildApiSchema(LocationSummary.class))
			.with(buildApiSchema(LocationDetails.class))
			.with(buildApiSchema(PersonSummary.class))
			.with(buildApiSchema(PersonDetails.class))
			.with(buildApiSchema(UserSummary.class))
			.with(buildApiSchema(UserDetails.class))
			.with(buildApiSchema(PersonName.class))
			.with(buildApiSchema(Telephone.class))
			.with(buildApiSchema(Communication.class))
			.with(buildApiSchema(MailingAddress.class))
			.with(buildApiSchema(Message.class))
			.with(buildApiSchema(Type.class))
			.with(buildApiSchema(Status.class))
			.with(buildBadRequestException())
			.with(buildPermissionDeniedException())
			.with(buildItemNotFoundException())
			.with(buildApiException())
		);
	}

	private JsonPair buildBadRequestException() throws Exception {
		return new JsonPair(BadRequestException.class.getSimpleName(), new JsonObject()
			.with("description", "The HTTP 400 The request could not be understood by the server due to malformed syntax. The client SHOULD NOT repeat the request without modifications.")
			.with("type", "object")
			.with("required", new JsonArray()
				.with("code")
				.with("messages")
			)
			.with("properties", new JsonObject()
				.with("code", new JsonObject()
					.with("description", "The http status code")
					.with("type", "integer")
					.with("pattern", 400)
				)
				.with("messages", new JsonObject()
					.with("type", "array")
					.with("items", new JsonObject()
						.with("$ref", "#!/componenst/schema/Message")
					)
				)
			)
		);
	}
	
	private JsonPair buildPermissionDeniedException() throws Exception {
		return new JsonPair(PermissionDeniedException.class.getSimpleName(), new JsonObject()
			.with("description", "The HTTP 403 Forbidden client error status response code indicates that the server understood the request but refuses to authorize it.")
			.with("type", "object")
			.with("required", new JsonArray()
				.with("code")
				.with("reason")
			)
			.with("properties", new JsonObject()
				.with("code", new JsonObject()
					.with("description", "The http status code")
					.with("type", "integer")
					.with("pattern", 403)
				)
				.with("reason", new JsonObject()
					.with("type", "string")
				)
			)
		);
	}
	
	private JsonPair buildItemNotFoundException() throws Exception {
		return new JsonPair(ItemNotFoundException.class.getSimpleName(), new JsonObject()
			.with("description", "The HTTP 404 The server has not found anything matching the Request-URI.")
			.with("type", "object")
			.with("required", new JsonArray()
				.with("code")
				.with("reason")
			)
			.with("properties", new JsonObject()
				.with("code", new JsonObject()
					.with("description", "The http status code")
					.with("type", "integer")
					.with("pattern", 404)
				)
				.with("reason", new JsonObject()
					.with("type", "string")
				)
			)
		);
	}
	
	private JsonPair buildApiException() throws Exception {
		return new JsonPair(ApiException.class.getSimpleName(), new JsonObject()
			.with("description", "The HTTP 500 The server encountered an unexpected condition which prevented it from fulfilling the request.")
			.with("type", "object")
			.with("required", new JsonArray()
				.with("code")
				.with("stack")
			)
			.with("properties", new JsonObject()
				.with("code", new JsonObject()
					.with("description", "The http status code")
					.with("type", "integer")
					.with("pattern", 500)
				)
				.with("stack", new JsonObject()
					.with("description", "Information about what went wrong on the server.")
					.with("type", "array")
					.with("items", new JsonObject()
						.with("type", "string")
					)
				)
			)
		);
	}

	private JsonPair buildApiSchema(Class<?> cls) throws Exception {
		JsonObject jsondoc = readJsondoc(cls);
		if (jsondoc.getString("type").equals("enum")) {
			return new JsonPair(cls.getSimpleName(), new JsonObject()
				.with("description", jsondoc.contains("description") ? jsondoc.getString("description") : "N/A")
				.with("type", "object")
				.with("oneOf", buildEnumConstants(jsondoc)));
		} else if (jsondoc.getString("type").equals("class") || jsondoc.getString("type").equals("class")) {
			return new JsonPair(cls.getSimpleName(), new JsonObject()
				.with("description", jsondoc.contains("description") ? jsondoc.getString("description") : "N/A")
				.with("type", "object")
				.with("required", findRequiredFields(jsondoc))
				.with("properties", findFieldDefinitions(jsondoc)));
		} else {
			throw new IllegalArgumentException("Unsupported type of element");
		}
	}
	
	private JsonArray buildEnumConstants(JsonObject jsondoc) {
		return new JsonArray(jsondoc.getArray("constants", JsonObject.class).stream()
			.map(o -> new JsonObject()
				.with("enum", new JsonArray().with(o.getString("name").toLowerCase()))
				.with("description", o.getString("description"))
			).collect(Collectors.toList()));
	}

	public JsonObject findFieldDefinitions(JsonObject jsondoc) throws Exception {
		List<JsonPair> pairs = new ArrayList<JsonPair>();
		if (jsondoc.contains("extends") && !jsondoc.getArray("extends", String.class).contains("RuntimeException")) {
			String parentName = jsondoc.getArray("extends", String.class).get(0);
			String parentPackage = jsondoc.getObject("imports").contains(parentName) ?
				jsondoc.getObject("imports").getString(parentName) :
				jsondoc.getString("package");
			pairs.addAll(findFieldDefinitions(readJsondoc(parentPackage + "." + parentName)).pairs());
		}
		if (jsondoc.contains("fields")) {
			pairs.addAll(jsondoc.getArray("fields", JsonObject.class)
				.stream()
				.map(o -> new JsonPair(o.getString("name"), buildFieldDefinition(jsondoc, o, o.getString("name"))))
				.collect(Collectors.toList()));
		}
		return new JsonObject(pairs);
	}

	private JsonObject buildFieldDefinition(JsonObject jsondoc, JsonObject fielddoc, String fieldName) {
		if (fieldName.endsWith("Id")) {
			return new JsonObject()
				.with("description", fielddoc.getString("description"))
				.with("type", "string")
				.with("format", fielddoc.getString("type"));
		} else if (fielddoc.contains("type", JsonObject.class) && fielddoc.getObject("type").getString("class").equals("IdentifierList")) {
			return new JsonObject()
				.with("description", fielddoc.getString("description"))
				.with("type", "array")
				.with("items", new JsonObject()
					.with("type", "string")
					.with("format", fielddoc.getObject("type").getArray("generics", String.class).get(0))
				);
		} else if (fielddoc.contains("type", JsonText.class) && fielddoc.getString("type").equals("String")) {
			return new JsonObject()
				.with("description", fielddoc.contains("description") ? fielddoc.getString("description") : "N/A")
				.with("type", "string")
				.with("regexp", findFieldFieldRegex(fielddoc))
				.with("min", findFieldMinValue(fielddoc))
				.with("max", findFieldMaxValue(fielddoc));
		} else if (fielddoc.contains("type", JsonObject.class) && fielddoc.getObject("type").getString("class").equals("Choice")) {
			return new JsonObject()
				.with("description", fielddoc.contains("description") ? fielddoc.getString("description") : "N/A")
				.with("oneOf", new JsonArray()
					.with(new JsonObject()
						.with("type", "string")
						.with("format", fielddoc.getObject("type").getArray("generics", String.class).get(0)))
					.with(new JsonObject()
						.with("type", "string")));
		} else if (fielddoc.contains("type", JsonObject.class)) {
			return new JsonObject()
				.with("$ref", "#/components/schemas/" + fielddoc.get("type"));
		} else {
			return new JsonObject()
				.with("$ref", "#/components/schemas/" + fielddoc.getString("type"));
		}
	}
	
	private JsonObject findAnnotation(JsonObject jsondoc, String name) {
		if (jsondoc.contains("annotations")) {
			Optional<JsonObject> annotation = jsondoc.getArray("annotations", JsonObject.class).stream().filter(a -> a.getString("name").equals(name)).findAny();
			if (annotation.isPresent()) {
				return annotation.get();
			}
		}
		return null;
	}
	
	private String findFieldFieldRegex(JsonObject fielddoc) {
		if (fielddoc.contains("annotations")) {
			Optional<JsonObject> annotation = fielddoc.getArray("annotations", JsonObject.class).stream().filter(a -> a.getString("name").equals("Pattern")).findAny();
			if (annotation.isPresent()) {
				Optional<String> value = annotation.get().getArray("properties", JsonObject.class).stream().filter(p -> p.getString("key").equals("regexp")).map(p -> p.getString("value")).findAny();
				if (value.isPresent())
					return value.get();
			}
		}
		return null;
	}
	
	private Integer findFieldMinValue(JsonObject fielddoc) {
		if (fielddoc.contains("annotations")) {
			Optional<JsonObject> size = fielddoc.getArray("annotations", JsonObject.class).stream().filter(a -> a.getString("name").equals("Size")).findAny();
			if (size.isPresent()) {
				Optional<String> minValue = size.get().getArray("properties", JsonObject.class).stream().filter(p -> p.getString("key").equals("min")).map(p -> p.getString("value")).findAny();
				if (minValue.isPresent())
					return Integer.parseInt(minValue.get());
			}
		}
		return null;
	}
	
	private Integer findFieldMaxValue(JsonObject fielddoc) {
		if (fielddoc.contains("annotations")) {
			Optional<JsonObject> size = fielddoc.getArray("annotations", JsonObject.class).stream().filter(a -> a.getString("name").equals("Size")).findAny();
			if (size.isPresent()) {
				Optional<String> maxValue = size.get().getArray("properties", JsonObject.class).stream().filter(p -> p.getString("key").equals("max")).map(p -> p.getString("value")).findAny();
				if (maxValue.isPresent())
					return Integer.parseInt(maxValue.get());
			}
		}
		return null;
	}
	
	public List<String> findRequiredFields(JsonObject jsondoc) throws Exception {
		List<String> fields = new ArrayList<String>();
		if (jsondoc.contains("extends") && !jsondoc.getArray("extends", String.class).contains("RuntimeException")) {
			String parentName = jsondoc.getArray("extends", String.class).get(0);
			String parentPackage = jsondoc.getObject("imports").contains(parentName) ?
				jsondoc.getObject("imports").getString(parentName) :
				jsondoc.getString("package");
			fields.addAll(findRequiredFields(readJsondoc(parentPackage + "." + parentName)));
		}
		if (jsondoc.contains("fields")) {
			fields.addAll(jsondoc.getArray("fields", JsonObject.class)
				.stream()
				.filter(o -> isFieldRequired(jsondoc, o.getString("name")))
				.map(o -> o.getString("name"))
				.collect(Collectors.toList()));
		}
		return fields;
	}
	
	public boolean isFieldRequired(JsonObject jsondoc, String fieldName) {
		JsonObject field = jsondoc.getArray("fields", JsonObject.class)
			.stream()
			.filter(o -> fieldName.equals(o.getString("name")))
			.findFirst().get();
		if (!field.contains("annotations"))
			return false;
		List<String> annotations = field.getArray("annotations", JsonObject.class)
			.stream()
			.map(o -> o.getString("name"))
			.collect(Collectors.toList());
		return annotations.contains("NotNull") || annotations.contains("NotEmpty");
	}
	
	public List<String> findFields(JsonObject jsondoc) {
		return jsondoc.getArray("fields", JsonObject.class)
			.stream().map(o -> o.getString("name")).collect(Collectors.toList());
	}

	public JsonObject readJsondoc(Class<?> cls) throws Exception {
		return readJsondoc(cls.getName());
	}
	
	public JsonObject readJsondoc(String cls) throws Exception {
		String jsondoc = cls.replaceAll("\\.", "/") + ".json";
		try (InputStream is = getClass().getClassLoader().getResourceAsStream(jsondoc)) {
			return new JsonObject(StreamUtils.copyToString(is, Charset.forName("UTF-8")));
		} catch (Exception e) {
			throw new NoSuchElementException("Unable to find: " + jsondoc);
		}
	}
	
}
