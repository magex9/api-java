package ca.magex.crm.restful.controllers;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import ca.magex.crm.api.crm.OrganizationDetails;
import ca.magex.crm.api.crm.OrganizationSummary;
import ca.magex.json.model.JsonArray;
import ca.magex.json.model.JsonObject;
import ca.magex.json.model.JsonPair;

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
			//.with(buildApiSchema(OrganizationSummary.class))
			.with(buildApiSchema(OrganizationDetails.class))
		);
	}

	private JsonPair buildApiSchema(Class<?> cls) throws Exception {
		JsonObject json = readJsondoc(cls);
		return new JsonPair(cls.getSimpleName(), new JsonObject()
			.with("description", json.getString("description"))
			.with("type", "object"));
	}
	
	public JsonObject readJsondoc(Class<?> cls) throws Exception {
		String jsondoc = cls.getName().replaceAll("\\.", "/") + ".json";
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
