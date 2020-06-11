package ca.magex.crm.api.lookup;

import java.io.Serializable;
import java.util.Locale;

public interface CrmLookupItem extends Serializable {

	public String getCode();
	
	public String getName(Locale locale);
	
	default CrmLookupItem getParent() {
		return null;
	}
	
	default boolean hasParent() {
		return getParent() == null;
	}
	
}
