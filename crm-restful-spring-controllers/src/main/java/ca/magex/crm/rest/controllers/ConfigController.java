package ca.magex.crm.rest.controllers;

import java.io.IOException;
import java.io.InputStream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import ca.magex.crm.ld.LinkedDataFormatter;
import ca.magex.crm.ld.data.DataObject;
import ca.magex.crm.ld.data.DataParser;

@Controller
public class ConfigController {

	@GetMapping("/api.yaml")
	private void getYamlConfig(HttpServletRequest req, HttpServletResponse res) throws Exception {
		res.setStatus(200);
		try (InputStream is = getClass().getResource("/public/crm.yaml").openStream()) {
			IOUtils.copy(is, res.getOutputStream());
		} catch (IOException ioe) {
			throw new RuntimeException("Error loading crm.yaml");
		}
	}

	@GetMapping("/api.json")
	private void getJsonConfig(HttpServletRequest req, HttpServletResponse res) throws Exception {
		try (InputStream is = getClass().getResource("/public/crm.yaml").openStream()) {
			ObjectMapper yamlReader = new ObjectMapper(new YAMLFactory());
			Object obj = yamlReader.readValue(is, Object.class);
			ObjectMapper jsonWriter = new ObjectMapper();
			String json = jsonWriter.writeValueAsString(obj);
			res.setStatus(200);
			res.getWriter().write(((DataObject) DataParser.parse(json)).stringify(LinkedDataFormatter.full()));
		} catch (IOException ioe) {
			throw new RuntimeException("Error loading crm.yaml");
		}
	}
	
}