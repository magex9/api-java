package ca.magex.crm.api.lookup;

import java.io.Serializable;
import java.util.Locale;

public interface CrmLookupItem extends Serializable {

	public String getCode();
	
	public String getName(Locale locale);
}
