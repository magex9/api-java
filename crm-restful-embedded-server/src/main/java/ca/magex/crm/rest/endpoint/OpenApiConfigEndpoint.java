package ca.magex.crm.rest.endpoint;

import java.io.IOException;
import java.io.InputStream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import ca.magex.crm.api.exceptions.ItemNotFoundException;
import ca.magex.crm.api.exceptions.PermissionDeniedException;
import ca.magex.crm.ld.LinkedDataFormatter;
import ca.magex.crm.ld.data.DataObject;
import ca.magex.crm.ld.data.DataParser;
import ca.magex.crm.rest.endpoint.Endpoint;

public class OpenApiConfigEndpoint implements Endpoint {
	
	private DataObject config;
	
	public OpenApiConfigEndpoint() {
		this.config = buildConfig();
	}
	
	public boolean isInterestedIn(HttpServletRequest req) {
		return req.getPathInfo().startsWith("/config");
	}
	
	public String execute(HttpServletRequest req, HttpServletResponse res) throws ItemNotFoundException, PermissionDeniedException, IOException {
		return config.stringify(LinkedDataFormatter.full());
	}
	
	protected DataObject buildConfig() {
		try {
			InputStream is = getClass().getClassLoader().getResourceAsStream("crm.yaml");
			ObjectMapper yamlReader = new ObjectMapper(new YAMLFactory());
			Object obj = yamlReader.readValue(is, Object.class);
			ObjectMapper jsonWriter = new ObjectMapper();
			String json = jsonWriter.writeValueAsString(obj);
			return (DataObject) DataParser.parse(json);
		} catch (Exception e) {
			throw new RuntimeException("Unable to parse config file", e);
		}
	}
		
}
