package ca.magex.crm.ld.lookup;

import ca.magex.crm.api.lookup.Language;
import ca.magex.crm.api.services.CrmServices;
import ca.magex.crm.ld.AbstractLinkedDataTransformer;
import ca.magex.crm.ld.data.DataObject;

public class LanguageTransformer extends AbstractLinkedDataTransformer<Language> {

	public LanguageTransformer(CrmServices crm) {

	}

	@Override
	public Class<?> getType() {
		return Language.class;
	}
	
	@Override
	public DataObject format(Language language) {
		return base()
			.with("@value", language.getCode())
			.with("name", language.getName());
	}

	@Override
	public Language parse(DataObject data, String parentContext) {
		validateContext(data, parentContext);
		validateType(data);
		String code = data.getString("@value");
		String name = data.getString("name");
		return new Language(code, name);
	}
			
}
