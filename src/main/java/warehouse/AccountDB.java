package warehouse;

import java.util.concurrent.Future;

import org.bson.Document;

import com.mongodb.client.AggregateIterable;

import models.Account;

public interface AccountDB{
	void CreateAccount(Account acct) throws Exception ;

	void UpdateAccount(Account acct) throws Exception;

	void DeleteAccount(Object obj);

	void QueryAccount(Object obj);
	
	void CreateAccount(String json);

	Future<AggregateIterable<Document>> ReadAccount(String username);
	Future<Document> FindByAccountNumber(String acctNumber);

	Future<Boolean> testConection();

}
