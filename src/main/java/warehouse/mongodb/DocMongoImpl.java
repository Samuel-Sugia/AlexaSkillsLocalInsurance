package warehouse.mongodb;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bson.Document;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Sorts;

import models.Doctor;
import streamhandler.LocalInsuranceStreamHandler;

public class DocMongoImpl {
	private static final Logger logger = LogManager.getLogger(DocMongoImpl.class);
	private static ObjectMapper mapper = new ObjectMapper();
	private static final String DATABASE_NAME = "local_insurance";
	private static final String COLLECTION_NAME = "doctor";
	private static ExecutorService executor = Executors.newCachedThreadPool();
	private MongoClient client = null;
	
	public DocMongoImpl() {
		client = LocalInsuranceStreamHandler.getMongoClient();
		if(client==null) {
			client = MongoClients.create(LocalInsuranceStreamHandler.getConnectionString());
		}
	}
	
	public void CreateDoctor(Object obj) throws Exception{

			String json = mapper.writeValueAsString(obj);
			Document doc = Document.parse(json);
			client.getDatabase(DATABASE_NAME).getCollection(COLLECTION_NAME).insertOne(doc);
			
	}

	public void CreateDoctor(String json) {
		executor.execute(() -> {
				Document doc = Document.parse(json);
				client.getDatabase(DATABASE_NAME).getCollection(COLLECTION_NAME).insertOne(doc);
		});
	}
	
	public List<Doctor> findDoctors(String strZip, String strSpecialty, String strSortKey) {
			FindIterable<Document> objResult = client.getDatabase(DATABASE_NAME).getCollection(COLLECTION_NAME).find(Filters.eq("zipCode", Integer.parseInt(strZip)));
			
			if("rating".equalsIgnoreCase(strSortKey)) {
				objResult = objResult.sort(Sorts.descending("rating"));
			} else if("distance".equalsIgnoreCase(strSortKey)) {
				objResult = objResult.sort(Sorts.ascending("distance"));
			}
			
			MongoCursor<Document> cursor = objResult.iterator();

			List<Doctor> lstDoctors = new ArrayList<Doctor>();
		    while (cursor.hasNext()) {

		        Document doc = cursor.next();
				String strDoctorJSON = doc.toJson();
				try {
					Doctor objDoctor = mapper.readValue(strDoctorJSON, Doctor.class);
					lstDoctors.add(objDoctor);
				} catch (JsonParseException e) {
					e.printStackTrace();
				} catch (JsonMappingException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
		    }
		    return lstDoctors;
	}
}
