package ca.magex.crm.rest;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import ca.magex.crm.api.crm.Location;
import ca.magex.crm.api.crm.Organization;
import ca.magex.crm.api.services.SecuredOrganizationService;

public class LocationSerializer extends JsonSerializer<Location> {

	private SecuredOrganizationService organizations;
	
	public LocationSerializer(SecuredOrganizationService organizations) {
		this.organizations = organizations;
	}
	
	@Override
	public void serialize(Location location, JsonGenerator gen, SerializerProvider serializers) throws IOException {
		gen.writeStartObject();
		gen.writeStringField("locationId", location.getLocationId().toString());
		gen.writeStringField("organizationId", organization.getOrganizationId().toString());
		gen.writeStringField("status", organization.getStatus().toString().toLowerCase());
		gen.writeStringField("displayName", organization.getDisplayName());
		gen.writeStringField("locationName", organizations.findLocation(organization.getMainLocation()).getDisplayName());
		gen.writeEndObject();
	}

}
