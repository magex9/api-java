package ca.magex.crm.mongodb.event;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ThreadFactory;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.bson.BsonDocument;
import org.bson.Document;
import org.slf4j.LoggerFactory;

import com.mongodb.client.ChangeStreamIterable;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Aggregates;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.changestream.ChangeStreamDocument;
import com.mongodb.client.model.changestream.FullDocument;
import com.mongodb.client.model.changestream.OperationType;

import ca.magex.crm.api.system.id.IdentifierFactory;
import ca.magex.crm.api.system.id.OptionIdentifier;

public class MongoOptionsChangeListener implements Runnable {

	private MongoDatabase mongoCrm;
	private String env;
	private ThreadFactory tf;
	private Thread t;

	public MongoOptionsChangeListener(MongoDatabase mongoCrm, String env, ThreadFactory tf) {
		this.mongoCrm = mongoCrm;
		this.env = env;
		this.tf = tf;
	}

	@PostConstruct
	public void start() {
		Logger.getLogger(getClass().getName()).info("Starting Mongo Change Listener");
		t = tf.newThread(this);
		t.start();
		Logger.getLogger(getClass().getName()).info("Started Mongo Change Listener");
	}

	@PreDestroy
	public void stop() {
		Logger.getLogger(getClass().getName()).info("Stopping Mongo Change Listener");
		if (t != null) {
			t.interrupt();

		}
		Logger.getLogger(getClass().getName()).info("Stopped Mongo Change Listener");
	}

	@Override
	public void run() {
		ChangeStreamIterable<Document> watcher = mongoCrm
				.getCollection("options")
				.watch(List.of(
						Aggregates.match(Filters.eq("fullDocument.env", env))));
		Iterator<ChangeStreamDocument<Document>> iter = watcher
				.fullDocument(FullDocument.UPDATE_LOOKUP)
				.iterator();
		while (iter.hasNext()) {
			try {
				ChangeStreamDocument<Document> doc = iter.next();
				final OperationType operationType = doc.getOperationType();
				switch(operationType) {
					case REPLACE:
						/* here the entire document was replaced... we don't know what changed */
						break;
					case INSERT:
					case UPDATE:
						/* here we should be able to get the path to what was inserted/updated */
						BsonDocument updatedFields = doc.getUpdateDescription().getUpdatedFields();
						Set<Integer> indices = new HashSet<>();
						for (String key : updatedFields.keySet()) {
							Matcher m = Pattern.compile("options\\.([0-9]+)\\..+").matcher(key);
							if (m.matches()) {
								indices.add(Integer.valueOf(m.group(1)));
							}
						}
						List<Document> list = (List<Document>) doc.getFullDocument().getList("options", Document.class);
						for (Integer index : indices) {							
							OptionIdentifier optionId = IdentifierFactory.forOptionId(list.get(index).getString("optionId"));							
							System.out.println("Option " + optionId + " was modified");
						}
						break;
					default:
						LoggerFactory.getLogger(getClass().getName()).debug("Ignoring Change Operation of tyep: " + operationType);
				}				
			}
			catch(Exception e) {
				LoggerFactory.getLogger(getClass()).error("Error handling change", e);
			}
		}
	};
}
