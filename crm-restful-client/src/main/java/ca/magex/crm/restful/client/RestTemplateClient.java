package ca.magex.crm.restful.client;

import java.net.URI;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import ca.magex.crm.api.adapters.CrmServicesAdapter;
import ca.magex.crm.api.exceptions.BadRequestException;
import ca.magex.crm.api.exceptions.ItemNotFoundException;
import ca.magex.crm.api.exceptions.PermissionDeniedException;
import ca.magex.crm.api.services.CrmServices;
import ca.magex.crm.api.system.Lang;
import ca.magex.crm.api.system.Message;
import ca.magex.crm.restful.client.services.RestfulOptionService;
import ca.magex.crm.restful.client.services.RestfulOrganizationService;
import ca.magex.crm.spring.security.jwt.JwtRequest;
import ca.magex.crm.spring.security.jwt.JwtToken;
import ca.magex.crm.transform.json.MessageJsonTransformer;
import ca.magex.json.model.JsonElement;
import ca.magex.json.model.JsonObject;
import ca.magex.json.model.JsonParser;

public class RestTemplateClient {
	
	private static final Logger logger = LoggerFactory.getLogger(RestTemplateClient.class);
	
	private String server;
	
	private Locale locale;
	
	private String authToken;
	
	private RestTemplate restTemplate;
	
	private RestfulOptionService options;
	
	private RestfulOrganizationService organizations;
	
	private CrmServices services;
	
	public RestTemplateClient(String server, Locale locale, String username, String password) {
		this.server = server;
		this.locale = locale;
		this.restTemplate = new RestTemplate();
		this.restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory());
		this.authToken = authenticateJwt(username, password);
		this.options = new RestfulOptionService(this);
		this.organizations = new RestfulOrganizationService(this);
		this.services = new CrmServicesAdapter(
			null, 
			options, 
			organizations, 
			null, 
			null, 
			null
		);
	}
	
	private String authenticateJwt(String username, String password) {
		ResponseEntity<JwtToken> response = restTemplate.exchange(
				RequestEntity
						.post(URI.create(server + "/authenticate"))
						.contentType(MediaType.APPLICATION_JSON)
						.body(new JwtRequest(username, password)),
				JwtToken.class);
		if (!response.getStatusCode().is2xxSuccessful()) {
			logger.error("Error authenticating the user: " + response.getStatusCode());
			throw new RuntimeException(response.getStatusCode().getReasonPhrase());
		}
		logger.info("Got authentication token successfully");
		return response.getBody().getToken();
	}
	
	private HttpEntity<String> buildHeaders(JsonObject body, Locale locale) {
		HttpHeaders headers = new HttpHeaders();
		headers.setBearerAuth(authToken);
		if (locale == null) {
			headers.setContentType(new MediaType("application", "json+ld"));
		} else if (Lang.ROOT.equals(locale)) {
			headers.setContentType(new MediaType("application", "json"));
		} else if (Lang.ENGLISH.equals(locale)) {
			headers.setContentType(new MediaType("application", "json"));
			headers.set("Locale", "en");
		} else if (Lang.FRENCH.equals(locale)) {
			headers.setContentType(new MediaType("application", "json"));
			headers.set("Locale", "fr");
		} else {
			throw new RuntimeException("Unsupported locale: " + locale);
		}
		if (body == null)
			return new HttpEntity<String>(headers);
		return new HttpEntity<String>(body.toString(), headers);
	}
	
	public Locale getLocale() {
		return locale;
	}
	
	public String getServer() {
		return server;
	}
	
	public String getAuthToken() {
		return authToken;
	}
	
	public RestfulOptionService getOptions() {
		return options;
	}
	
	public RestfulOrganizationService getOrganizations() {
		return organizations;
	}
	
	public CrmServices getServices() {
		return services;
	}
	
	public <T extends JsonElement> T get(Object endpoint) {
		return get(endpoint, new JsonObject());
	}
	
	@SuppressWarnings("unchecked")
	public <T extends JsonElement> T get(Object endpoint, JsonObject body) {
		String url = server + "/rest" + endpoint.toString();
		logger.info("Get request: " + url);
		try {
			UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(url);
			for (String key : body.keys()) {
				builder.queryParam(key, JsonObject.unwrap(body.get(key)).toString());
			}
			ResponseEntity<String> response = restTemplate.exchange(builder.toUriString(), HttpMethod.GET, buildHeaders(null, locale), String.class);
			logger.debug("Get response: " + response.getBody());
			return (T)JsonParser.parse(response.getBody());
		} catch (HttpClientErrorException.NotFound e) {
			throw new ItemNotFoundException(url);
		} catch (HttpClientErrorException.Forbidden e) {
			throw new PermissionDeniedException(url);
		} catch (HttpClientErrorException.BadRequest e) {
			MessageJsonTransformer transformer = new MessageJsonTransformer(services);
			List<Message> messages = JsonParser.parseArray(e.getResponseBodyAsString())
				.stream()
				.map(el -> transformer.parse(el, locale))
				.collect(Collectors.toList());
			throw new BadRequestException(url, messages);
		}
	}
	
	@SuppressWarnings("unchecked")
	public <T extends JsonElement> T post(Object endpoint, JsonObject body) {
		String url = server + "/rest" + endpoint.toString();
		logger.info("Post request: " + url + " (" + body + ")");
		try {
			ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, buildHeaders(body, locale), String.class);
			logger.debug("Post response: " + response.getBody());
			return (T)JsonParser.parse(response.getBody());
		} catch (HttpClientErrorException.NotFound e) {
			throw new ItemNotFoundException(url);
		} catch (HttpClientErrorException.Forbidden e) {
			throw new PermissionDeniedException(url);
		} catch (HttpClientErrorException.BadRequest e) {
			MessageJsonTransformer transformer = new MessageJsonTransformer(services);
			List<Message> messages = JsonParser.parseArray(e.getResponseBodyAsString())
				.stream()
				.map(el -> transformer.parse(el, locale))
				.collect(Collectors.toList());
			throw new BadRequestException(url, messages);
		}
	}
	
	@SuppressWarnings("unchecked")
	public <T extends JsonElement> T put(Object endpoint, JsonObject body) {
		String url = server + "/rest" + endpoint.toString();
		logger.info("Put request: " + url + " (" + body + ")");
		try {
			ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.PUT, buildHeaders(body, locale), String.class);
			logger.debug("Put response: " + response.getBody());
			return (T)JsonParser.parse(response.getBody());
		} catch (HttpClientErrorException.NotFound e) {
			throw new ItemNotFoundException(url);
		} catch (HttpClientErrorException.Forbidden e) {
			throw new PermissionDeniedException(url);
		} catch (HttpClientErrorException.BadRequest e) {
			MessageJsonTransformer transformer = new MessageJsonTransformer(services);
			List<Message> messages = JsonParser.parseArray(e.getResponseBodyAsString())
				.stream()
				.map(el -> transformer.parse(el, locale))
				.collect(Collectors.toList());
			throw new BadRequestException(url, messages);
		}
	}
	
	@SuppressWarnings("unchecked")
	public <T extends JsonElement> T patch(Object endpoint, JsonObject body) {
		String url = server + "/rest" + endpoint.toString();
		logger.info("Patch request: " + url + " (" + body + ")");
		try {
			ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.PATCH, buildHeaders(body, locale), String.class);
			logger.debug("Patch response: " + response.getBody());
			return (T)JsonParser.parse(response.getBody());
		} catch (HttpClientErrorException.NotFound e) {
			throw new ItemNotFoundException(url);
		} catch (HttpClientErrorException.Forbidden e) {
			throw new PermissionDeniedException(url);
		} catch (HttpClientErrorException.BadRequest e) {
			MessageJsonTransformer transformer = new MessageJsonTransformer(services);
			List<Message> messages = JsonParser.parseArray(e.getResponseBodyAsString())
				.stream()
				.map(el -> transformer.parse(el, locale))
				.collect(Collectors.toList());
			throw new BadRequestException(url, messages);
		}
	}

}
