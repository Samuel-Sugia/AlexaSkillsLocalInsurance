package streamhandler;

import org.apache.logging.log4j.LogManager;

import com.amazon.ask.Skill;
import com.amazon.ask.SkillStreamHandler;
import com.amazon.ask.Skills;
import com.mongodb.client.MongoClient;

import intenthandlers.AddDepenIntent;
import intenthandlers.BenefitsDetailsIntent;
import intenthandlers.CancelandStopIntent;
import intenthandlers.CreateClaimIntent;
import intenthandlers.CustomerSupportIntent;
import intenthandlers.FeedbackIntent;
import intenthandlers.FindDoctorIntent;
import intenthandlers.HelpandFallbackIntentHandler;
import intenthandlers.LaunchRequestHandler;
import intenthandlers.MailOrderPrescriptionIntent;
import intenthandlers.PendingClaimsIntent;
import intenthandlers.SearchClaimIntent;
import intenthandlers.SessionEndedHandler;
import intenthandlers.UpdatePersonalInfoIntent;
import requestinterceptors.RequestInteceptorDBTester;

public class LocalInsuranceStreamHandler extends SkillStreamHandler {
	private static org.apache.logging.log4j.Logger log = LogManager.getLogger(LocalInsuranceStreamHandler.class);
	//private static String CONNECT_STRING =System.getenv("connection_string");
	//private static MongoClient mongoClient = MongoClients.create(CONNECT_STRING);

	@SuppressWarnings("unchecked")
	private static Skill getSkills() {
		log.info("GetSkills");
		return Skills.standard().addRequestInterceptor(new RequestInteceptorDBTester()).addRequestHandlers(new LaunchRequestHandler(), new HelpandFallbackIntentHandler(),
				new SearchClaimIntent(), new PendingClaimsIntent(), new BenefitsDetailsIntent(), new FindDoctorIntent(),
				new CreateClaimIntent(), new SessionEndedHandler(), new CancelandStopIntent(), new AddDepenIntent(), new FeedbackIntent(), 
				new MailOrderPrescriptionIntent(), new CustomerSupportIntent(), new UpdatePersonalInfoIntent()).build();
	}

	public LocalInsuranceStreamHandler() {
		super(getSkills());
		System.setProperty("org.apache.logging.log4j.simplelog.StatusLogger.level", "trace");
	}

	public static MongoClient getMongoClient() {
		return null;
	}
	
	public static String getConnectionString() {
		return System.getenv("connection_string");
	}
	
	public static String getDemoAccountNumber() {
		return System.getenv("demo_acct");
	}

}