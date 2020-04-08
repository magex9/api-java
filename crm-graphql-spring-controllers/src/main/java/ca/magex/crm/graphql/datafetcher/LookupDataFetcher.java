package ca.magex.crm.graphql.datafetcher;

import java.lang.reflect.Method;
import java.util.Locale;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.ReflectionUtils;

import graphql.schema.DataFetcher;

public class LookupDataFetcher {

	private static Logger logger = LoggerFactory.getLogger(LookupDataFetcher.class);
	
	public DataFetcher<String> getNameByLocale(Locale locale) {
		return (environment) -> {
			logger.debug("Entering getNameByLocale@" + LookupDataFetcher.class.getSimpleName());
			Object source = environment.getSource();
			Method getName = ReflectionUtils.findMethod(source.getClass(), "getName", Locale.class);
			return (String) ReflectionUtils.invokeMethod(getName, source, locale);
		};
	}
}
