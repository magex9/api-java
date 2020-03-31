package ca.magex.crm.rest.endpoint;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ca.magex.crm.api.exceptions.ItemNotFoundException;
import ca.magex.crm.api.exceptions.PermissionDeniedException;

public class ItemNotFoundEndpoint implements Endpoint {
	
	public boolean isInterestedIn(HttpServletRequest req) {
		return true;
	}

	public String execute(HttpServletRequest req, HttpServletResponse res) throws ItemNotFoundException, PermissionDeniedException, IOException {
		return "{\"error\":404,\"type\":\"Path not found: " + req.getPathInfo() + "\"}";
	}
	
}
