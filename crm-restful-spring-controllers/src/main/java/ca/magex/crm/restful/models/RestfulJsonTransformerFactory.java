package ca.magex.crm.restful.models;

import java.util.List;

import ca.magex.crm.api.services.CrmOptionService;
import ca.magex.crm.transform.json.JsonTransformerFactory;

public class RestfulJsonTransformerFactory extends JsonTransformerFactory {

	public static final String RESTFUL_TRANSFORMER_PACKAGE = "ca.magex.crm.restful.models";
	
	public RestfulJsonTransformerFactory(CrmOptionService options) {
		super(options, List.of(JsonTransformerFactory.BASE_TRANSFORMER_PACKAGE, RESTFUL_TRANSFORMER_PACKAGE));
	}
	
}
