package ca.magex.crm.resource;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import ca.magex.crm.api.exceptions.ApiException;

@Component
public class CrmLookupLoader {

	/**
	 * returns a list of lookups
	 * @param <T>
	 * @param clazz
	 * @param lookup
	 * @return
	 */
	public <T> List<T> loadLookup(Class<T> clazz, String lookup) {
		List<T> list = new ArrayList<T>();
		URL url = getClass().getResource("/lookups/" + lookup);
		try (InputStream is = url.openStream()) {
			CSVParser parser = CSVParser.parse(new InputStreamReader(is, "UTF-8"), CSVFormat.DEFAULT);			
			list.addAll(parser.getRecords()
				.stream()
				.map((r) -> {
					if (r.size() != 3) {
						throw new ApiException("Resource entry '" + r  + "' does not contain 3 columns");
					}
					try {
						return (T) clazz.getConstructor(
								String.class, 
								String.class, 
								String.class).newInstance(
										StringUtils.trim(r.get(0)), 
										StringUtils.trim(r.get(1)), 
										StringUtils.trim(r.get(2)));
					}
					catch (Exception e) {
						throw new ApiException("Error parsing resource entry '" + r + "'", e);
					}
				})
				.collect(Collectors.toList()));
			

		} catch (IOException ioe) {
			throw new ApiException("Error loading resource lookup '" + lookup + "'", ioe);
		}
		return list;
	}
}
