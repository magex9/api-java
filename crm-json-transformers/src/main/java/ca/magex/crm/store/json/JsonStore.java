package ca.magex.crm.store.json;

import java.io.IOException;

import ca.magex.crm.api.crm.OrganizationDetails;
import ca.magex.crm.api.services.CrmServices;
import ca.magex.crm.api.store.basic.BasicStore;
import ca.magex.crm.transform.json.OrganizationDetailsJsonTransformer;
import ca.magex.json.model.JsonObject;

public class JsonStore extends BasicStore {

	@Override
	public String encode(Object obj, CrmServices crm) throws IOException {
		if (obj instanceof OrganizationDetails) {
			new OrganizationDetailsJsonTransformer(crm).formatRoot((OrganizationDetails)obj);
		}
		
		
		throw new IllegalArgumentException("Object type not supported: " + obj.getClass());
	}
	
	@Override
	public Object decode(String text, CrmServices crm) throws IOException, ClassNotFoundException {
		JsonObject json = new JsonObject(text);
		
		
		throw new IllegalArgumentException("Object type not supported: " + text);
	}
	
}
