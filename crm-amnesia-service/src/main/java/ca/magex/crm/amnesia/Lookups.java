package ca.magex.crm.amnesia;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.beanutils.PropertyUtils;

import ca.magex.crm.api.exceptions.ItemNotFoundException;
import ca.magex.crm.api.system.Lang;

public class Lookups<LOOKUP extends Object, KEY extends Object> {

	private Class<?> lookupClass;
	
	private List<LOOKUP> options;
	
	private Map<KEY, LOOKUP> mapByCode;
	
	private Map<Locale, Map<String, LOOKUP>> mapByName;
	
	public Lookups(List<LOOKUP> options, Class<?> lookupClass, Class<?> codeClass) {
		try {
			this.lookupClass = lookupClass;
			this.options = options;
			this.mapByCode = new HashMap<KEY, LOOKUP>();
			this.mapByName = new HashMap<Locale, Map<String, LOOKUP>>();
			for (Locale locale : Lang.SUPPORTED) {
				mapByName.put(locale, new HashMap<String, LOOKUP>());
			}
			Method nameMethod = lookupClass.getMethod("getName", new Class[] { Locale.class });
			for (LOOKUP option : options) {
				@SuppressWarnings("unchecked")
				KEY key = (KEY)PropertyUtils.getProperty(option, "code");
				mapByCode.put(key, option);
				for (Locale locale : Lang.SUPPORTED) {
					mapByName.get(locale).put((String)nameMethod.invoke(option, locale), option);
				}
			}
		} catch (Exception e) {
			throw new RuntimeException("Problem building lookup: " + lookupClass, e);
		}
	}
	
	public List<LOOKUP> getOptions() {
		return options;
	}
	
	public LOOKUP findByCode(KEY code) {
		if (!mapByCode.containsKey(code))
			throw new ItemNotFoundException(lookupClass.getSimpleName() + " '" + code + "'");
		return mapByCode.get(code);
	}

	public LOOKUP findByName(Locale locale, String name) {
		if (!mapByName.containsKey(locale))
			throw new ItemNotFoundException(lookupClass.getSimpleName() + "[" + locale + "] '" + name + "'");
		if (!mapByName.get(locale).containsKey(name))
			throw new ItemNotFoundException(lookupClass.getSimpleName() + "[" + locale + "] '" + name + "'");
		return mapByName.get(locale).get(name);
	}
	
}
