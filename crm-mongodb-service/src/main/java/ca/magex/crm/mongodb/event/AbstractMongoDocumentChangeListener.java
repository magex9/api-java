package ca.magex.crm.mongodb.event;

import java.util.List;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mongodb.MongoException;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Aggregates;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.changestream.ChangeStreamDocument;
import com.mongodb.client.model.changestream.FullDocument;
import com.mongodb.client.model.changestream.OperationType;
import com.mongodb.client.model.changestream.UpdateDescription;

import ca.magex.crm.api.event.CrmEventObserver;

/**
 * Base class for our Mongo Change Listeners
 * 
 * @author Jonny
 */
public abstract class AbstractMongoDocumentChangeListener implements Runnable {

	protected Logger logger = LoggerFactory.getLogger(getClass().getName());

	protected MongoDatabase mongoCrm;
	protected CrmEventObserver observer;
	protected String env;
	protected ThreadFactory tf;
	protected Thread t;
	protected boolean stopIssued = false;
	
	
	protected AbstractMongoDocumentChangeListener(CrmEventObserver observer, MongoDatabase mongoCrm, String env, ThreadFactory tf) {
		this.observer = observer;
		this.mongoCrm = mongoCrm;
		this.env = env;
		this.tf = tf;
	}

	@PostConstruct
	public void start() {
		logger.info("Starting Mongo Change Listener");
		t = tf.newThread(this);
		t.setName(t.getName() + "-" + getCollectionName());
		t.start();
		logger.info("Started Mongo Change Listener");
	}

	@PreDestroy
	public void stop() {
		logger.info("Stopping Mongo Change Listener");
		stopIssued = true;
		if (t != null) {			
			t.interrupt();
		}
		logger.info("Stopped Mongo Change Listener");
	}
	
	/**
	 * returns the collection to monitor
	 * @return
	 */
	public abstract String getCollectionName();
	
	public abstract void documentReplaced(Document fullDocumnet);
	
	public abstract void documentModified(Document fullDocument, UpdateDescription updatedDescription);

	@Override
	public void run() {
		MongoCursor<ChangeStreamDocument<Document>> cursor = null;
		main: while(true) {
			try {
				if (cursor == null) {
					cursor = mongoCrm
							.getCollection(getCollectionName())
							.watch(List.of(
									Aggregates.match(Filters.eq("fullDocument.env", env))))
							.fullDocument(FullDocument.UPDATE_LOOKUP)
							.iterator();
				}
				while (cursor.hasNext()) {
					try {
						ChangeStreamDocument<Document> doc = cursor.next();
						final OperationType operationType = doc.getOperationType();
						switch(operationType) {
							case REPLACE:
								documentReplaced(doc.getFullDocument());
								break;
							case INSERT:
							case UPDATE:
								documentModified(doc.getFullDocument(), doc.getUpdateDescription());								
								break;
							default:
								logger.debug("Ignoring Change Operation of tyep: " + operationType);
						}				
					}
					catch(Exception e) {
						logger.error("Error handling change", e);
					}
				}
			}
			catch(MongoException ie) {
				if (stopIssued) {
					break main;
				}
				logger.warn("MongoCursor was Interrupted, attempting to reconnect", ie);
				cursor = null;
				int reconnect = 1;
				/* attempt to reconnect until a stop has been issued */
				reconnect: while (!stopIssued) {
					if (reconnect < 5) {
						reconnect++;
					}
					try {
						/* use an exponential back-off reconnect strategy up to 2^5 (32 second) wait period */
						long waitDuration = (long) Math.pow(2.0, reconnect);
						Thread.sleep(TimeUnit.SECONDS.toMillis(waitDuration));
						cursor = mongoCrm
							.getCollection(getCollectionName())
							.watch(List.of(
									Aggregates.match(Filters.eq("fullDocument.env", env))))
							.fullDocument(FullDocument.UPDATE_LOOKUP)
							.iterator();
						break reconnect;
					}
					catch(Exception e) {
						logger.warn("Reconnect failed: " + e.getMessage());
					}
				}				
			}
		}
	};
}
