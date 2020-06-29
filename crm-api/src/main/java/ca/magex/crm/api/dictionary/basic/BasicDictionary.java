package ca.magex.crm.api.dictionary.basic;

import java.util.HashMap;
import java.util.Map;

import ca.magex.crm.api.dictionary.CrmDictionary;
import ca.magex.crm.api.system.Localized;

public class BasicDictionary implements CrmDictionary {

	private Map<String, Localized> messages;
	
	public BasicDictionary initialize() {
		messages = new HashMap<>();
		addMessage("validation.group.status.required", "Status is mandatory for a group");
		addMessage("validation.group.status.pending", "Pending statuses should not have identifiers");
		addMessage("validation.organization.status.required", "Status is mandatory for an organization");
		return this;
	}
	
	public void addMessage(String key, String english) {
		addMessage(key, english, english + " FR");
	}
	
	public void addMessage(String key, String english, String french) {
		messages.put(key, new Localized(key, english, french));
	}
	
	@Override
	public Localized getMessage(String key) {
		return messages.get(key);
	}

}
