package ca.magex.crm.amnesia.services;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.io.IOUtils;

import ca.magex.crm.api.exceptions.ItemNotFoundException;

public class Lookups<LOOKUP extends Object, KEY extends Object> {

	private List<LOOKUP> options;
	
	private Map<KEY, LOOKUP> mapByCode;
	
	private Map<Locale, Map<String, LOOKUP>> mapByName;

	@SuppressWarnings("unchecked")
	public Lookups(Class<?> lookupClass, Class<?> codeClass) {
		this(loadLookup(lookupClass, codeClass).stream().map(o -> (LOOKUP)o).collect(Collectors.toList()), lookupClass, codeClass);
	}
	
	public Lookups(List<LOOKUP> options, Class<?> lookupClass, Class<?> codeClass) {
		try {
			mapByCode = new HashMap<KEY, LOOKUP>();
			mapByName = new HashMap<Locale, Map<String, LOOKUP>>();
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
	
	private static List<?> loadLookup(Class<?> lookupClass, Class<?> codeClass) {
		try {
			Constructor<?> constructor = (Constructor<?>)lookupClass.getConstructor(codeClass, String.class, String.class);
			return Arrays.asList(IOUtils.toString(Lookups.class.getClassLoader()
					.getResourceAsStream("lookups/" + lookupClass.getSimpleName() + ".csv"), StandardCharsets.UTF_8)
					.replaceAll("\r", "")
					.split("\n"))
				.stream().map(line -> {
					try {
						String[] parts = line.split("\t+");
						Object code = parts[0];
						if (codeClass.equals(Integer.class)) {
							code = Integer.parseInt(parts[0]);
						}
						return constructor.newInstance(code, parts[1], parts[2]);
					} catch (Exception e) {
						throw new RuntimeException(e);
					}
				}).collect(Collectors.toList());
		} catch (Exception e) {
			throw new RuntimeException("Unable to create list: " + lookupClass, e);
		}
	}
	
	public List<LOOKUP> getOptions() {
		return options;
	}
	
	public LOOKUP findByCode(KEY code) {
		if (!mapByCode.containsKey(code))
			throw new ItemNotFoundException("Unable to find lookup by code: " + code);
		return mapByCode.get(code);
	}

	public LOOKUP findByName(Locale locale, String name) {
		if (!mapByName.containsKey(locale))
			throw new ItemNotFoundException("Locale is not supported: " + locale);
		if (!mapByName.get(locale).containsKey(name))
			throw new ItemNotFoundException("Unable to find lookup by name: " + name);
		return mapByName.get(locale).get(name);
	}
	
}
