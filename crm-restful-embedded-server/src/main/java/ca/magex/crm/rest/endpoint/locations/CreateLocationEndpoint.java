package ca.magex.crm.rest.endpoint.locations;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ca.magex.crm.api.common.MailingAddress;
import ca.magex.crm.api.crm.LocationDetails;
import ca.magex.crm.api.exceptions.ItemNotFoundException;
import ca.magex.crm.api.exceptions.PermissionDeniedException;
import ca.magex.crm.api.lookup.Country;
import ca.magex.crm.api.services.SecuredOrganizationService;
import ca.magex.crm.api.system.Identifier;
import ca.magex.crm.ld.crm.LocationDetailsTransformer;
import ca.magex.crm.ld.data.DataObject;
import ca.magex.crm.rest.endpoint.AbstractEndpoint;

public class CreateLocationEndpoint extends AbstractEndpoint<LocationDetails> {
	
	public CreateLocationEndpoint(SecuredOrganizationService service) {
		super(service, new LocationDetailsTransformer());
	}
	
	public boolean isExpectedPath(String path) {
		return path.matches("/api/locations");
	}
	
	public String getMethod() {
		return "POST";
	}

	public String execute(HttpServletRequest req, HttpServletResponse res) throws ItemNotFoundException, PermissionDeniedException, IOException {
		DataObject data = body(req);
		//Identifier 
		Identifier organizationId = new Identifier(data.getString("organizationId"));
		String displayName = body(req).getString("displayName");
		String reference = body(req).getString("reference");
		String street = body(req).getObject("address").getString("street");
		String city = body(req).getObject("address").getString("city");
		String province = body(req).getObject("address").getString("province");
		String country = body(req).getObject("address").getObject("country").getString("code");
		String postalCode = body(req).getObject("address").getString("postalCode");
		MailingAddress address = new MailingAddress(street, city, province, new Country(country, country), postalCode);
		return getTransformer().format(getService().createLocation(organizationId, displayName, reference, address)).stringify(formatter(req));
	}
	
}
