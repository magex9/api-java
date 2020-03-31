package ca.magex.crm.rest.endpoint;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface Endpoint {

	public boolean isInterestedIn(HttpServletRequest req);

	public String execute(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException;

}