package requestinterceptors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.amazon.ask.dispatcher.request.handler.HandlerInput;
import com.amazon.ask.dispatcher.request.interceptor.RequestInterceptor;

public class RequestInteceptorDBTester implements RequestInterceptor {
	// private static ObjectMapper mapper = new ObjectMapper();
	private static Logger logger = LogManager.getLogger(RequestInteceptorDBTester.class);

	@Override
	public void process(HandlerInput input) {
		logger.info("Recieved request @ %s",input.getRequest().getTimestamp().toLocalDate().toString());
		/*
		StringBuffer logText = new StringBuffer("DocumentDB Test Connection Results\n");
		try (MongoClient client = MongoClients.create(
				"mongodb://LocalDBA:Local2019!@docdb-2019-04-30-20-16-11.cluster-cltnbqfwyj0c.us-east-1.docdb.amazonaws.com:27017/?ssl=true&ssl_ca_certs=rds-combined-ca-bundle.pem&replicaSet=rs0")) {
			for (String dbname : client.listDatabaseNames()) {
				logText.append(dbname + " is an available db\n");
				for (String colname : client.getDatabase(dbname).listCollectionNames()) {
					logText.append("\t" + colname + " is a available collection on " + dbname+"\n");
				}
			}
			System.out.println(logText.toString());
			logger.info(logText.toString());
		}
		*/
	}

}
