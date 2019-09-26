package warehouse.mongodb;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bson.Document;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.client.AggregateIterable;
import com.mongodb.client.ClientSession;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;

import models.User;
import warehouse.UserDB;

public class UserMongoImpl implements UserDB{
	private static final Logger logger = LogManager.getLogger(AcctMongoImpl.class);
	private static final ObjectMapper mapper = new ObjectMapper();
	private static final String DATABASE_NAME = "LocalInsurance";
	private static final String COLLECTION_NAME = "User";
	private static ExecutorService executor = Executors.newCachedThreadPool();
	private String connectionString = null;
	
	public UserMongoImpl(String connectionString) {
		this.connectionString = connectionString;
	}
	

	@Override
	public void CreateUser(User user) {
		executor.execute(()->{
			ClientSession session = null;
			try(MongoClient client = MongoClients.create(connectionString)){
				session=client.startSession();
				session.startTransaction();
				String json = mapper.writeValueAsString(user);
				Document doc = Document.parse(json);
				client.getDatabase(DATABASE_NAME).getCollection(COLLECTION_NAME).insertOne(doc);
				session.commitTransaction();
				session.close();
			} catch (Exception e) {
				logger.catching(e);
				session.abortTransaction();
				session.close();
			}
			
		});
		
	}

	@Override
	public Future<AggregateIterable<Document>> ReadUser(String username) {
		return executor.submit(()->{
			try(MongoClient client = MongoClients.create(connectionString)){
				try(ClientSession session = client.startSession()){
					return client.getDatabase(DATABASE_NAME).getCollection(COLLECTION_NAME).aggregate(session, null);
				}
			}
		});
		
	}

	@Override
	public void UpdateUser(User user) {
		throw new UnsupportedOperationException();
		
	}

	@Override
	public void DeleteUser(User user) {
		throw new UnsupportedOperationException();
		
	}

}
