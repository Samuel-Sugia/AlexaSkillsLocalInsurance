package warehouse;

import java.util.concurrent.Future;

import org.bson.Document;

import com.mongodb.client.AggregateIterable;

public interface DoctorDB {
	Future<Boolean> testConnetion();
	public void CreateDoctor(Object obj);
	Future<AggregateIterable<Document>> ReadDoctor(Object obj);
	public void UpdateDoctors(Object obj);
	public void DeleteDoctors(Object obj);
	public void QueryDoctors(Object obj);

}
