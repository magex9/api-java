package ca.magex.crm.mongodb.event;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ThreadFactory;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bson.BsonDocument;
import org.bson.Document;

import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.changestream.UpdateDescription;

import ca.magex.crm.api.event.CrmEventObserver;
import ca.magex.crm.api.system.id.LocationIdentifier;
import ca.magex.crm.api.system.id.OrganizationIdentifier;
import ca.magex.crm.api.system.id.PersonIdentifier;
import ca.magex.crm.api.system.id.UserIdentifier;

/**
 * Listener than handles changes to an options document
 * 
 * @author Jonny
 */
public class MongoOrganizationsDocumentChangeListener extends AbstractMongoDocumentChangeListener {
	
	private static Pattern LOCATION_PATH_PATTERN = Pattern.compile("locations\\.([0-9]+)(\\..+)?");
	private static Pattern PERSON_PATH_PATTERN = Pattern.compile("persons\\.([0-9]+)(\\..+)?");
	private static Pattern USER_PATH_PATTERN = Pattern.compile("users\\.([0-9]+)(\\..+)?");
	
	public MongoOrganizationsDocumentChangeListener(CrmEventObserver observer, MongoDatabase mongoCrm, String env, ThreadFactory tf) {
		super(observer, mongoCrm, env, tf);
	}
	
	@Override
	public String getCollectionName() {
		return "organizations";
	}
	
	@Override
	public void documentReplaced(Document fullDocument) {
		/* here the entire document was replaced... we don't know what changed, so we need to parse the document and call the update on everything */
		{
			OrganizationIdentifier organizationId = new OrganizationIdentifier(fullDocument.getString("organizationId"));		
			/* pass null date time because we don't know why the document was replaced (this generally happens through a tool like MongoDB Compass) */
			observer.organizationUpdated(null, organizationId);
		}
		
		/* locations */
		List<Document> locations = (List<Document>) fullDocument.getList("locations", Document.class);
		for (Document location : locations) {
			LocationIdentifier locationId = new LocationIdentifier(location.getString("locationId"));
			/* pass null date time because we don't know why the document was replaced (this generally happens through a tool like MongoDB Compass) */
			observer.locationUpdated(null, locationId);
		}	
		
		/* locations */
		List<Document> persons = (List<Document>) fullDocument.getList("persons", Document.class);
		for (Document person : persons) {
			PersonIdentifier personId = new PersonIdentifier(person.getString("personId"));
			/* pass null date time because we don't know why the document was replaced (this generally happens through a tool like MongoDB Compass) */
			observer.personUpdated(null, personId);
		}
		
		/* users */
		List<Document> users = (List<Document>) fullDocument.getList("users", Document.class);
		for (Document user : users) {
			UserIdentifier userId = new UserIdentifier(user.getString("userId"));
			/* pass null date time because we don't know why the document was replaced (this generally happens through a tool like MongoDB Compass) */
			observer.userUpdated(null, userId);
		}
	}
	
	@Override
	public void documentModified(Document fullDocument, UpdateDescription updatedDescription) {
		/* here we should be able to get the path to what was inserted/updated */
		BsonDocument updatedFields = updatedDescription.getUpdatedFields();
		Set<Integer> locationIndices = new HashSet<>();
		Set<Integer> personIndices = new HashSet<>();
		Set<Integer> userIndices = new HashSet<>();
		boolean orgUpdated = false;
		for (String key : updatedFields.keySet()) {
			Matcher m = LOCATION_PATH_PATTERN.matcher(key);
			if (m.matches()) {
				locationIndices.add(Integer.valueOf(m.group(1)));
				continue;
			} 
			m = PERSON_PATH_PATTERN.matcher(key);
			if (m.matches()) {
				personIndices.add(Integer.valueOf(m.group(1)));
				continue;
			}
			m = USER_PATH_PATTERN.matcher(key);
			if (m.matches()) {
				userIndices.add(Integer.valueOf(m.group(1)));
				continue;
			}
			/* if the update wasn't to one of the locations, persons, or users, then we assume it was to the organization itself */
			orgUpdated = true;
		}
		
		if (orgUpdated) {
			OrganizationIdentifier organizationId = new OrganizationIdentifier(fullDocument.getString("organizationId"));
			observer.organizationUpdated(null, organizationId);
		}
		
		if (locationIndices.size() > 0) {
			List<Document> locations = (List<Document>) fullDocument.getList("locations", Document.class);
			for (Integer index : locationIndices) {
				LocationIdentifier locationId = new LocationIdentifier(locations.get(index).getString("locationsId"));
				Long lastModified = locations.get(index).getLong("lastModified");
				logger.debug("Location " + locationId + " was modified on " + new Date(lastModified));
				observer.locationUpdated(lastModified, locationId);
			}
		}
		
		if (personIndices.size() > 0) {
			List<Document> persons = (List<Document>) fullDocument.getList("persons", Document.class);
			for (Integer index : personIndices) {
				PersonIdentifier personId = new PersonIdentifier(persons.get(index).getString("personId"));
				Long lastModified = persons.get(index).getLong("lastModified");
				logger.debug("Person " + personId + " was modified on " + new Date(lastModified));
				observer.personUpdated(lastModified, personId);
			}
		}
		
		if (userIndices.size() > 0) {
			List<Document> users = (List<Document>) fullDocument.getList("users", Document.class);
			for (Integer index : userIndices) {
				UserIdentifier userId = new UserIdentifier(users.get(index).getString("userId"));
				Long lastModified = users.get(index).getLong("lastModified");
				logger.debug("User " + userId + " was modified on " + new Date(lastModified));
				observer.userUpdated(lastModified, userId);
			}
		}
	}
}
