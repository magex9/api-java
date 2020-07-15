package ca.magex.crm.restful.controllers;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import ca.magex.crm.api.crm.OrganizationDetails;
import ca.magex.crm.api.crm.OrganizationSummary;
import ca.magex.crm.api.system.Status;
import ca.magex.json.model.JsonArray;
import ca.magex.json.model.JsonObject;
import ca.magex.json.model.JsonPair;
import ca.magex.json.model.JsonText;

@Controller
public class SwaggerRestfulController {

	@Value("${server.external.address:localhost}") 
	private String serverAddress;
	
	@Value("${server.port:9002}") 
	private String serverPort;
	
	@Value("${server.servlet.context-path:/crm}") 
	private String contextPath;
	
	@GetMapping("/rest/api.json")
	public void getJsonConfig(HttpServletRequest req, HttpServletResponse res) throws IOException {		
		try (InputStream is = getClass().getResource("/crm.json").openStream()) {
			String contents = StreamUtils.copyToString(is, Charset.forName("UTF-8"))
				.replace("${serverAddress}", serverAddress)
				.replace("${serverPort}", serverPort)
				.replace("${contextPath}", contextPath);
			res.getWriter().append(contents);
			res.getWriter().flush();
		}
	}
	
	@GetMapping("/rest/oapi.json")
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
			.with("components", buildApiComponents())
		;
	}
	
	public JsonPair buildApiPaths() throws Exception {
		return new JsonPair("paths", "...");
	}
		
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
//			.with(buildApiSchema(OrganizationSummary.class))
//			.with(buildApiSchema(OrganizationDetails.class))
			.with(buildApiSchema(Status.class))
		);
	}

//	"Status": {
//        "description": "The status of an entity",
//        "type": "string",
//        "enum": [
//          "active",
//          "inactive",
//          "pending"
//        ]
//      },
	
	private JsonPair buildApiSchema(Class<?> cls) throws Exception {
		JsonObject jsondoc = readJsondoc(cls);
		return new JsonPair(cls.getSimpleName(), new JsonObject()
			.with("description", jsondoc.contains("description") ? jsondoc.getString("description") : "N/A")
			.with("type", "object")
			.with("required", findRequiredFields(jsondoc))
			.with("properties", findFieldDefinitions(jsondoc)));
	}
	
	public JsonObject findFieldDefinitions(JsonObject jsondoc) throws Exception {
		List<JsonPair> pairs = new ArrayList<JsonPair>();
		if (jsondoc.contains("extends")) {
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
				.with("format", "identifier");
		} else if (fielddoc.contains("type", JsonObject.class) && fielddoc.getObject("type").getString("class").equals("IdentifierList")) {
			return new JsonObject()
				.with("description", fielddoc.getString("description"))
				.with("type", "array")
				.with("items", new JsonObject()
					.with("type", "string")
					.with("format", "identifier")
				);
		} else if (fielddoc.contains("type", JsonText.class) && fielddoc.getString("type").equals("String")) {
			return new JsonObject()
				.with("description", fielddoc.getString("description"))
				.with("type", "string");
		} else {
			return new JsonObject()
				.with("$ref", "#/components/schemas/" + fielddoc.getString("type"));
		}
	}

//	"fields": [
//	           {
//	             "name": "mainLocationId",
//	             "description": "identifier for the main location of the organization",
//	             "modifiers": ["private"],
//	             "type": "LocationIdentifier",
//	             "annotations": [{"name": "NotNull"}]
//	           },
//	           {
//	             "name": "mainContactId",
//	             "description": "identifier for the main contact of the organization",
//	             "modifiers": ["private"],
//	             "type": "PersonIdentifier",
//	             "annotations": [{"name": "NotNull"}]
//	           },
	
	public List<String> findRequiredFields(JsonObject jsondoc) throws Exception {
		List<String> fields = new ArrayList<String>();
		if (jsondoc.contains("extends")) {
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
		}
	}
	
		
		
//			  "paths": {
//			    "/rest/organizations": {
//			      "get": {
//			        "summary": "List all organizations",
//			        "operationId": "findOrganizations",
//			        "tags": ["Organizations"],
//			        "parameters": [
//			          {
//			            "in": "query",
//			            "name": "displayName",
//			            "description": "Find an organization by its name",
//			            "required": false,
//			            "schema": {"type": "string"}
//			          },
//			          {
//			            "in": "query",
//			            "name": "status",
//			            "description": "Find organizations in a certian status",
//			            "required": false,
//			            "default": "active",
//			            "schema": {"$ref": "#/components/schemas/Status"}
//			          },
//			          {
//			            "in": "query",
//			            "name": "page",
//			            "description": "Page number",
//			            "required": false,
//			            "default": 1,
//			            "schema": {"type": "integer"}
//			          },
//			          {
//			            "in": "query",
//			            "name": "limit",
//			            "description": "The number of items on the page",
//			            "required": false,
//			            "default": 10,
//			            "schema": {"type": "integer"}
//			          },
//			          {
//			            "in": "query",
//			            "name": "order",
//			            "description": "The proper to order the results by",
//			            "required": false,
//			            "default": "displayName",
//			            "schema": {
//			              "type": "string",
//			              "enum": [
//			                "displayName",
//			                "status"
//			              ]
//			            }
//			          },
//			          {
//			            "in": "query",
//			            "name": "direction",
//			            "description": "The order to sort the propert by",
//			            "default": "asc",
//			            "required": false,
//			            "schema": {
//			              "type": "string",
//			              "enum": [
//			                "asc",
//			                "desc"
//			              ]
//			            }
//			          }
//			        ],
//			        "responses": {
//			          "200": {
//			            "description": "A paged array of organizations",
//			            "headers": {
//			              "X-Rate-Limit-Limit": {
//			                "description": "The number of allowed requests in the current period",
//			                "schema": {"type": "integer"}
//			              },
//			              "X-Rate-Limit-Remaining": {
//			                "description": "The number of remaining requests in the current period",
//			                "schema": {"type": "integer"}
//			              },
//			              "X-Rate-Limit-Reset": {
//			                "description": "The number of seconds left in the current period",
//			                "schema": {"type": "integer"}
//			              },
//			              "x-next": {
//			                "description": "A link to the next page of responses",
//			                "schema": {"type": "string"}
//			              }
//			            },
//			            "content": {
//			              "application/json": {
//			                "schema": {
//			                  "type": "array",
//			                  "items": {"$ref": "#/components/schemas/OrganizationSummary"}
//			                }
//			              }
//			            }
//			          },
//			          "default": {
//			            "description": "System error",
//			            "content": {
//			              "application/json": {
//			                "schema": {"$ref": "#/components/schemas/SystemException"}
//			              }
//			            }
//			          }
//			        }
//			      },
	
}
