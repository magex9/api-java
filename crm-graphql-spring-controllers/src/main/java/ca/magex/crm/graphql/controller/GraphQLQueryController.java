package ca.magex.crm.graphql.controller;

import org.springframework.context.annotation.Description;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@Description("Controller for handling the query UI")
public class GraphQLQueryController {

	@GetMapping("/graphql/query")
	public String getQueryUI() {
		return "query";
	}
}