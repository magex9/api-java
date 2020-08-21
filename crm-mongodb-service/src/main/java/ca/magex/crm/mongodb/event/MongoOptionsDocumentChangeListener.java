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
import ca.magex.crm.api.system.id.IdentifierFactory;
import ca.magex.crm.api.system.id.OptionIdentifier;

/**
 * Listener than handles changes to an options document
 * 
 * @author Jonny
 */
public class MongoOptionsDocumentChangeListener extends AbstractMongoDocumentChangeListener {
	
	private static Pattern OPTION_PATH_PATTERN = Pattern.compile("options\\.([0-9]+)(\\..+)?");
	
	public MongoOptionsDocumentChangeListener(CrmEventObserver observer, MongoDatabase mongoCrm, String env, ThreadFactory tf) {
		super(observer, mongoCrm, env, tf);
	}
	
	@Override
	public String getCollectionName() {
		return "options";
	}
	
	@Override
	public void documentReplaced(Document fullDocumnet) {
		/* here the entire document was replaced... we don't know what changed */
		List<Document> options = (List<Document>) fullDocumnet.getList("options", Document.class);
		for (Document option : options) {
			OptionIdentifier optionId = IdentifierFactory.forOptionId(option.getString("optionId"));
			/* pass null date time because we don't know why the document was replaced (this generally happens through a tool like MongoDB Compass) */
			observer.optionUpdated(null, optionId);
		}	
	}
	
	@Override
	public void documentModified(Document fullDocument, UpdateDescription updatedDescription) {
		/* here we should be able to get the path to what was inserted/updated */
		BsonDocument updatedFields = updatedDescription.getUpdatedFields();
		Set<Integer> indices = new HashSet<>();
		for (String key : updatedFields.keySet()) {
			Matcher m = OPTION_PATH_PATTERN.matcher(key);
			if (m.matches()) {
				indices.add(Integer.valueOf(m.group(1)));
			}
		}
		List<Document> list = (List<Document>) fullDocument.getList("options", Document.class);
		for (Integer index : indices) {
			OptionIdentifier optionId = IdentifierFactory.forOptionId(list.get(index).getString("optionId"));
			Long lastModified = list.get(index).getLong("lastModified");
			logger.debug("Option " + optionId + " was modified on " + new Date(lastModified));
			observer.optionUpdated(lastModified, optionId);
		}
	}
}
