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

public class PrescriptionMongoImpl {
	private static final Logger logger = LogManager.getLogger(PrescriptionMongoImpl.class);
	private static ObjectMapper mapper = new ObjectMapper();
	private static final String DATABASE_NAME = "local_insurance";
	private static final String COLLECTION_NAME = "prescription";
	private static ExecutorService executor = Executors.newCachedThreadPool();
	private MongoClient client = null;
	
	public PrescriptionMongoImpl() {
		client = LocalInsuranceStreamHandler.getMongoClient();
		if(client==null) {
			client = MongoClients.create(LocalInsuranceStreamHandler.getConnectionString());
		}
	}
	
	public void CreatePrescription(Object obj) throws Exception{

			String json = mapper.writeValueAsString(obj);
			Document doc = Document.parse(json);
			client.getDatabase(DATABASE_NAME).getCollection(COLLECTION_NAME).insertOne(doc);
			
	}

	public void CreatePrescription(String json) {
		executor.execute(() -> {
				Document doc = Document.parse(json);
				client.getDatabase(DATABASE_NAME).getCollection(COLLECTION_NAME).insertOne(doc);
		});
	}
	
}
