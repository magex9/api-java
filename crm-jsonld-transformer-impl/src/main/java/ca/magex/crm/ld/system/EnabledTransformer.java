package ca.magex.crm.ld.system;

import java.time.LocalDateTime;

import ca.magex.crm.api.system.Enabled;
import ca.magex.crm.api.system.Identifier;
import ca.magex.crm.ld.AbstractLinkedDataTransformer;
import ca.magex.crm.ld.data.DataObject;

public class EnabledTransformer extends AbstractLinkedDataTransformer<Enabled> {

	@Override
	public String getType() {
		return "enabled";
	}
	
	@Override
	public DataObject format(Enabled enabled) {
		return base()
			.with("identifer", enabled.getIdentifier())
			.with("enabled", datetimeToString(enabled.getEnabled()))
			.with("disabled", datetimeToString(enabled.getDisabled()));
	}

	@Override
	public Enabled parse(DataObject data) {
		validateContext(data);
		validateType(data);
		Identifier identifier = new Identifier(data.getString("identifier"));
		LocalDateTime enabled = parseDatetime(data.getString("enabled"));
		LocalDateTime disabled = parseDatetime(data.getString("disabled"));
		return new Enabled(identifier, enabled, disabled);
	}
			
}
