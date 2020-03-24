package ca.magex.crm.graphql.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ca.magex.crm.graphql.service.GraphQLService;
import graphql.ExecutionResult;

@RequestMapping("/bookQuery")
@RestController
public class BookController {
    private static Logger logger = LoggerFactory.getLogger(BookController.class);

    private GraphQLService graphQLService;

    @Autowired
    public BookController(GraphQLService graphQLService) {
        this.graphQLService=graphQLService;
    }

    @PostMapping
    public ResponseEntity<Object> getAllBooks(@RequestBody String query) throws JSONException {
        logger.info("Entering getAllBooks@BookController");
        JSONObject request = new JSONObject(query);
        ExecutionResult execute = graphQLService.getGraphQL().execute(request.getString("query"));
        return new ResponseEntity<>(execute, HttpStatus.OK);
    }

}