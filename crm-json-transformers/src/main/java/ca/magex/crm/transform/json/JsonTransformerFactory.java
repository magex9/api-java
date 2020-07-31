package ca.magex.crm.transform.json;

import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.reflections.Reflections;

import ca.magex.crm.api.services.CrmOptionService;
import ca.magex.crm.api.system.Identifier;
import ca.magex.crm.api.transform.Transformer;
import ca.magex.json.model.JsonElement;

public class JsonTransformerFactory {

	private Map<Class<?>, Transformer<?, JsonElement>> transformers;
	
	public JsonTransformerFactory(CrmOptionService options) {
		this(options, List.of("ca.magex.crm.transform.json"));
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public JsonTransformerFactory(CrmOptionService options, List<String> packages) {
		this.transformers = new HashMap<Class<?>, Transformer<?, JsonElement>>();
		for (String pkg : packages) {
			for (Class<? extends Transformer> transformer : new Reflections(pkg).getSubTypesOf(Transformer.class)) {
				try {
					if (!Modifier.isAbstract(transformer.getModifiers())) {
						Transformer<?, JsonElement> instance = transformer.getConstructor(CrmOptionService.class).newInstance(options);
						transformers.put(instance.getSourceType(), instance);
					}
				} catch (ReflectiveOperationException e) {
					throw new RuntimeException("Unable to create transformer: " + transformer.getClass().getName(), e);
				}
			}
		}
	}
	
	@SuppressWarnings("unchecked")
	public <S> Transformer<S, JsonElement> findByClass(Class<S> cls) {
		if (Identifier.class.isAssignableFrom(cls))
			return (Transformer<S, JsonElement>) transformers.get(Identifier.class);
		return (Transformer<S, JsonElement>) transformers.get(cls);
	}
	
}
