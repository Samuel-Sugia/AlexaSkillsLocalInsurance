package warehouse;

import java.util.concurrent.Future;

import org.bson.Document;

import com.mongodb.client.AggregateIterable;

import models.User;

public interface UserDB {
	public void CreateUser(User user);
	public Future<AggregateIterable<Document>> ReadUser(String username);
	public void UpdateUser(User user);
	public void DeleteUser(User user);


}
