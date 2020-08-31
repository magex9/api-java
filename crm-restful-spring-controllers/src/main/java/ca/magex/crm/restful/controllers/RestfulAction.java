package ca.magex.crm.restful.controllers;

import ca.magex.crm.api.system.Localized;

public class RestfulAction {

	private String action;
	
	private Localized label;
	
	private String method;
	
	private String link;

	public RestfulAction(String action, Localized label, String method, String link) {
		super();
		this.action = action;
		this.label = label;
		this.method = method;
		this.link = link;
	}
	
	public String getAction() {
		return action;
	}
	
	public Localized getLabel() {
		return label;
	}
	
	public String getMethod() {
		return method;
	}
	
	public String getLink() {
		return link;
	}
	
}
