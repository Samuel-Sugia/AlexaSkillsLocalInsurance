package warehouse.mongodb;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bson.Document;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;

import streamhandler.LocalInsuranceStreamHandler;

public class FeedbackMongoImpl {
	private static final Logger logger = LogManager.getLogger(FeedbackMongoImpl.class);
	private static ObjectMapper mapper = new ObjectMapper();
	private static final String DATABASE_NAME = "local_insurance";
	private static final String COLLECTION_NAME = "feedback";
	private static ExecutorService executor = Executors.newCachedThreadPool();
	private MongoClient client = null;
	
	public FeedbackMongoImpl() {
		client = LocalInsuranceStreamHandler.getMongoClient();
		if(client==null) {
			client = MongoClients.create(LocalInsuranceStreamHandler.getConnectionString());
		}
	}
	
	public void CreateFeedback(Object obj) throws Exception{

			String json = mapper.writeValueAsString(obj);
			Document doc = Document.parse(json);
			client.getDatabase(DATABASE_NAME).getCollection(COLLECTION_NAME).insertOne(doc);
			
	}

	public void CreateFeedback(String json) {
		executor.execute(() -> {
				Document doc = Document.parse(json);
				client.getDatabase(DATABASE_NAME).getCollection(COLLECTION_NAME).insertOne(doc);
		});
	}
	
}
