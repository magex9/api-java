package ca.magex.crm.springboot.config;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.web.servlet.handler.SimpleUrlHandlerMapping;
import org.springframework.web.servlet.resource.ResourceHttpRequestHandler;

@Configuration
public class FaviconConfiguration {

	@Bean
	public SimpleUrlHandlerMapping customFaviconHandlerMapping() {
		SimpleUrlHandlerMapping mapping = new SimpleUrlHandlerMapping();
		mapping.setOrder(Integer.MIN_VALUE);
		mapping.setUrlMap(Map.of(
				"/android-chrome-192x192.png", faviconRequestHandler(),
				"/android-chrome-512x512.png", faviconRequestHandler(),
				"/apple-touch-icon.png", faviconRequestHandler(),
				"/favicon-16x16.png", faviconRequestHandler(),
				"/favicon-32x32.png", faviconRequestHandler(),
				"/favicon.ico", faviconRequestHandler()
				));
		return mapping;
	}

	@Bean
	protected ResourceHttpRequestHandler faviconRequestHandler() {
		ResourceHttpRequestHandler requestHandler = new ResourceHttpRequestHandler();
		ClassPathResource classPathResource = new ClassPathResource("favicon/");
		List<Resource> locations = Arrays.asList(classPathResource);
		requestHandler.setLocations(locations);
		return requestHandler;
	}
}
