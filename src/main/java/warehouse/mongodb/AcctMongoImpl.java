package warehouse.mongodb;

import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bson.Document;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.client.AggregateIterable;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.model.Aggregates;
import com.mongodb.client.model.Filters;

import models.Account;
import streamhandler.LocalInsuranceStreamHandler;
import warehouse.AccountDB;

public class AcctMongoImpl implements AccountDB {
	private static final Logger logger = LogManager.getLogger(AcctMongoImpl.class);
	private static ObjectMapper mapper = new ObjectMapper();
	private static final String DATABASE_NAME = "local_insurance";
	private static final String COLLECTION_NAME = "account";
	private static ExecutorService executor = Executors.newCachedThreadPool();
	private MongoClient client = null;

	public AcctMongoImpl() {
		mapper.enableDefaultTyping();
		mapper.enable(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT);
		client = LocalInsuranceStreamHandler.getMongoClient();
		if (client == null) {
			client = MongoClients.create(LocalInsuranceStreamHandler.getConnectionString());
		}
	}

	@Override
	public Future<Boolean> testConection() {
		return executor.submit(() -> {
			return (client.getDatabase(DATABASE_NAME).listCollectionNames() != null ? true : false);
		});
	}

	@Override
	public void CreateAccount(Account acct) throws Exception {

		String json = mapper.writeValueAsString(acct);
		Document doc = Document.parse(json);
		client.getDatabase(DATABASE_NAME).getCollection(COLLECTION_NAME).insertOne(doc);

	}

	@Override
	public void CreateAccount(String json) {
		executor.execute(() -> {
			Document doc = Document.parse(json);
			client.getDatabase(DATABASE_NAME).getCollection(COLLECTION_NAME).insertOne(doc);
		});
	}

	@Override
	public Future<AggregateIterable<Document>> ReadAccount(String username) {
		return executor.submit(() -> {
			return client.getDatabase(DATABASE_NAME).getCollection(COLLECTION_NAME)
					.aggregate(Arrays.asList(Aggregates.match(Filters.and(Filters.expr("this.dependents<3"),
							Filters.eq("accountholder.state", "Texas")))));
		});
	}

	public Future<Document> FindByAccountNumber(String acctNumber) {
		return executor.submit(() -> {
			Document test = client.getDatabase(DATABASE_NAME).getCollection(COLLECTION_NAME)
					.find(Filters.eq("acctNumber", acctNumber)).first();
			logger.info("Account number: " + acctNumber + "found\n");
			return test;
		});
	}

	@Override
	public void UpdateAccount(Account acct) throws Exception {
		String json = mapper.writeValueAsString(acct);
		Document doc = Document.parse(json);
		logger.info(doc.toJson());
		client.getDatabase(DATABASE_NAME).getCollection(COLLECTION_NAME)
		.replaceOne(Filters.eq("acctNumber", acct.getAcctNumber()), doc);
		
		/*
		client.getDatabase(DATABASE_NAME).getCollection(COLLECTION_NAME)
				.updateOne(Filters.eq("acctNumber", acct.getAcctNumber()), doc);*/
	}

	@Override
	public void DeleteAccount(Object obj) {
		throw new UnsupportedOperationException();

	}

	@Override
	public void QueryAccount(Object obj) {
		throw new UnsupportedOperationException();

	}

}
