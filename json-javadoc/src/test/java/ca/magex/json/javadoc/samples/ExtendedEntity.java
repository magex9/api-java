package ca.magex.json.javadoc.samples;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * A basic entity test
 * @author scott
 *
 */
public final class ExtendedEntity extends BasicEntity implements Cloneable, Serializable {

	private static final long serialVersionUID = -5716584170934858984L;
	
	public static final List<Locale> locales = List.of(Locale.CANADA, Locale.CANADA_FRENCH);
	
	private Map<BasicEntity, Map<Locale, List<? extends BasicEntity>>> cache;
	
	public ExtendedEntity(Locale... locales) {
		this.cache = new HashMap<BasicEntity, Map<Locale,List<? extends BasicEntity>>>();
	}
	
	public Map<Locale, List<? extends BasicEntity>> findChildren(Locale locale) {
		return cache.get(this);
	}
	
	public List<Locale> getLocales() {
		return locales;
	}
	
}
