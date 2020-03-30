package ca.magex.crm.ld.system;

import java.time.LocalDateTime;

import ca.magex.crm.api.system.Activation;
import ca.magex.crm.api.system.Identifier;
import ca.magex.crm.ld.AbstractLinkedDataTransformer;
import ca.magex.crm.ld.data.DataObject;

public class ActivationTransformer extends AbstractLinkedDataTransformer<Activation> {

	@Override
	public Class<?> getType() {
		return Activation.class;
	}
	
	@Override
	public DataObject format(Activation activation) {
		return base()
			.with("identifer", activation.getIdentifier())
			.with("enabled", datetimeToString(activation.getEnabled()))
			.with("disabled", datetimeToString(activation.getDisabled()));
	}

	@Override
	public Activation parse(DataObject data, String parentContext) {
		validateContext(data, parentContext);
		validateType(data);
		Identifier identifier = new Identifier(data.getString("identifier"));
		LocalDateTime enabled = parseDatetime(data.getString("enabled"));
		LocalDateTime disabled = parseDatetime(data.getString("disabled"));
		return new Activation(identifier, enabled, disabled);
	}
			
}
