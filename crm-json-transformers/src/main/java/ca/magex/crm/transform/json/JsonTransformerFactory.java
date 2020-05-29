package ca.magex.crm.transform.json;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import ca.magex.crm.api.transform.Transformer;
import ca.magex.json.model.JsonElement;

@Component
@SuppressWarnings("unchecked")
public class JsonTransformerFactory {

	private ApplicationContext ctx;
	
	private Map<Class<?>, Transformer<?, JsonElement>> transformers;
	
	public JsonTransformerFactory(ApplicationContext ctx) {
		this.ctx = ctx;
		this.transformers = new HashMap<Class<?>, Transformer<?, JsonElement>>();
	}
	
	@PostConstruct
	public void initialize() {
		ctx.getBeansOfType(Transformer.class).values().forEach(t -> {
			transformers.put(t.getSourceType(), t);
		});
	}
	
	public <S> Transformer<S, JsonElement> findByClass(Class<S> cls) {
		return (Transformer<S, JsonElement>) transformers.get(cls);
	}
	
}
