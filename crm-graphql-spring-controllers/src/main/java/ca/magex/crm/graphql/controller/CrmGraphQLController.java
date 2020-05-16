package ca.magex.crm.graphql.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Interface for the GraphQLController used for exposing the service through eureka
 * 
 * @author Jonny
 */
public interface CrmGraphQLController {

	@PostMapping("/graphql")
	public ResponseEntity<Object> doQuery(@RequestBody String request, HttpServletRequest req, HttpServletResponse res) throws JSONException;
	
	@GetMapping("/graphql")
	public ResponseEntity<Object> doQueryAsGet(@RequestParam String query, HttpServletRequest req, HttpServletResponse res) throws JSONException;
}
