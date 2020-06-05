package ca.magex.crm.amnesia;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang3.StringUtils;

import ca.magex.crm.api.system.Lang;

public class Lookups<LOOKUP extends Object, KEY extends Object> {

	private List<LOOKUP> options;
	
	private Map<Locale, Map<String, LOOKUP>> mapByName;

	public Lookups(List<LOOKUP> options, Class<?> lookupClass, Class<?> codeClass) {
		try {
			this.options = options;
			this.mapByName = new HashMap<Locale, Map<String, LOOKUP>>();
			this.mapByName.put(Lang.ROOT, new HashMap<String, LOOKUP>());
			for (Locale locale : Lang.SUPPORTED) {
				mapByName.put(locale, new HashMap<String, LOOKUP>());
			}
			Method nameMethod = lookupClass.getMethod("getName", new Class[] { Locale.class });
			for (LOOKUP option : options) {
				@SuppressWarnings("unchecked")
				KEY key = (KEY) PropertyUtils.getProperty(option, "code");
				mapByName.get(Lang.ROOT).put(StringUtils.upperCase(key.toString()), option);
				for (Locale locale : Lang.SUPPORTED) {
					mapByName.get(locale).put((String) nameMethod.invoke(option, locale), option);
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
		return mapByName.get(Lang.ROOT).get(StringUtils.upperCase(code.toString()));
	}

	public LOOKUP findByName(Locale locale, String name) {
		if (Lang.ROOT.equals(locale)) {
			return mapByName.get(Lang.ROOT).get(StringUtils.upperCase(name));
		}
		if (!mapByName.containsKey(locale)) {
			return null;
		}
		if (!mapByName.get(locale).containsKey(name)) {
			return null;
		}
		return mapByName.get(locale).get(name);
	}
}