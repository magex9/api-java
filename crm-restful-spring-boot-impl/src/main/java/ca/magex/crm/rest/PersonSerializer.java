package ca.magex.crm.rest;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import ca.magex.crm.api.crm.Organization;
import ca.magex.crm.api.services.SecuredOrganizationService;

public class PersonSerializer extends JsonSerializer<Organization> {

	private SecuredOrganizationService organizations;
	
	public PersonSerializer(SecuredOrganizationService organizations) {
		this.organizations = organizations;
	}
	
	@Override
	public void serialize(Organization organization, JsonGenerator gen, SerializerProvider serializers) throws IOException {
		gen.writeStartObject();
		gen.writeStringField("organizationId", organization.getOrganizationId().toString());
		gen.writeStringField("status", organization.getStatus().toString().toLowerCase());
		gen.writeStringField("displayName", organization.getDisplayName());
		gen.writeEndObject();
	}

}
