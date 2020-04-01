package ca.magex.crm.rest.endpoint;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import ca.magex.crm.api.exceptions.ItemNotFoundException;
import ca.magex.crm.api.exceptions.PermissionDeniedException;
import ca.magex.crm.ld.LinkedDataFormatter;
import ca.magex.crm.ld.data.DataObject;
import ca.magex.crm.ld.data.DataParser;
import ca.magex.crm.rest.servlet.MimeType;

public class OpenApiConfigEndpoint implements Endpoint {
	
	private Map<MimeType, String> configs;
	
	public OpenApiConfigEndpoint() {
		this.configs = new HashMap<MimeType, String>();
	}
	
	public boolean isInterestedIn(HttpServletRequest req) {
		return req.getPathInfo().startsWith("/config");
	}
	
	public String execute(HttpServletRequest req, HttpServletResponse res) throws ItemNotFoundException, PermissionDeniedException, IOException {
		String accepts = req.getHeader("Accept");
		if (accepts == null) {
			return getJsonConfig();
		} else if (accepts.equals(MimeType.YAML.toString())) {
			return getYamlConfig();
		} else {
			return getJsonConfig();
		}
	}
		
	private InputStream getConfigInputStream() {
		return getClass().getClassLoader().getResourceAsStream("crm.yaml");
	}
	
	private String getYamlConfig() {
		if (!configs.containsKey(MimeType.YAML)) {
			try {
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				IOUtils.copy(getConfigInputStream(), baos);
				configs.put(MimeType.YAML, new String(baos.toByteArray()));
			} catch (Exception e) {
				throw new RuntimeException("Unable to parse config file", e);
			}
		}
		return configs.get(MimeType.YAML);
	}
	
	private String getJsonConfig() {
		if (!configs.containsKey(MimeType.JSON)) {
			try {
				ObjectMapper yamlReader = new ObjectMapper(new YAMLFactory());
				Object obj = yamlReader.readValue(getConfigInputStream(), Object.class);
				ObjectMapper jsonWriter = new ObjectMapper();
				String json = jsonWriter.writeValueAsString(obj);
				configs.put(MimeType.JSON, ((DataObject) DataParser.parse(json)).stringify(LinkedDataFormatter.full()));
			} catch (Exception e) {
				throw new RuntimeException("Unable to parse config file", e);
			}
		}
		return configs.get(MimeType.JSON);
	}
		
}
