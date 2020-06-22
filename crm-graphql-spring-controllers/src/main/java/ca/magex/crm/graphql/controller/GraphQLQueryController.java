package ca.magex.crm.graphql.controller;

import org.springframework.context.annotation.Description;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@Description("Controller for handling the query UI")
@CrossOrigin
public class GraphQLQueryController {

	@GetMapping("/graphql/query")
	@ResponseBody // remove to go back to query page
	public String getQueryUI() {
		return "query";
	}
	
	
	@ResponseBody
	@PostMapping("/graphql/query")
	public String doQuery(@RequestBody String query) {
		
		return query.replace("query=", "");
	}
}