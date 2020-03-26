package ca.magex.crm.ld.common;

import ca.magex.crm.api.common.Communication;
import ca.magex.crm.api.common.Telephone;
import ca.magex.crm.api.lookup.Language;
import ca.magex.crm.ld.AbstractLinkedDataTransformer;
import ca.magex.crm.ld.data.DataObject;
import ca.magex.crm.ld.lookup.LanguageTransformer;

public class CommunicationTransformer extends AbstractLinkedDataTransformer<Communication> {

	private LanguageTransformer languageTransformer;
	
	private TelephoneTransformer telephoneTransformer;
	
	public CommunicationTransformer() {
		this.languageTransformer = new LanguageTransformer();
		this.telephoneTransformer = new TelephoneTransformer();
	}
	
	@Override
	public Class<?> getType() {
		return Communication.class;
	}
	
	@Override
	public DataObject format(Communication communication) {
		return base()
			.with("email", communication.getEmail())
			.with("jobTitle", communication.getJobTitle())
			.with("language", languageTransformer.format(communication.getLanguage()))
			.with("homePhone", telephoneTransformer.format(communication.getHomePhone()))
			.with("faxNumber", communication.getFaxNumber());
	}

	@Override
	public Communication parse(DataObject data) {
		validateContext(data);
		validateType(data);
		String email = data.getString("email");
		String jobTitle = data.getString("jobTitle");
		Language language = languageTransformer.parse(data.get("language"));
		Telephone homePhone = telephoneTransformer.parse(data.get("homePhone"));
		Long faxNumber = data.getLong("faxNumber");
		return new Communication(jobTitle, language, email, homePhone, faxNumber);
	}
			
}
