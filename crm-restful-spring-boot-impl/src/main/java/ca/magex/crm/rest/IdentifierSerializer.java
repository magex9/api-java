package ca.magex.crm.rest;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import ca.magex.crm.api.system.Identifier;

public class IdentifierSerializer extends JsonSerializer<Identifier> {

	@Override
	public void serialize(Identifier value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
		gen.writeString(value.toString());
	}

}
